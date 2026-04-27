package com.gacha.project.service;

import com.gacha.project.entity.CouponPolicy;
import com.gacha.project.entity.CouponStock;
import com.gacha.project.entity.CouponHistory;
import com.gacha.project.repository.CouponPolicyRepository;
import com.gacha.project.repository.CouponStockRepository;
import com.gacha.project.repository.CouponHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class GachaService {

    private final RedissonClient redissonClient;
    private final CouponPolicyRepository couponPolicyRepository;
    private final CouponStockRepository couponStockRepository;
    private final CouponHistoryRepository couponHistoryRepository;
    private final TransactionTemplate transactionTemplate;

    public String tryDrawGacha(Long userId) {
        Long policyId = 1L; 
        String lockKey = "lock:gacha:policy:" + policyId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean isLocked = lock.tryLock(100, 3, TimeUnit.SECONDS);
            if (!isLocked) {
                throw new RuntimeException("현재 참여자가 많아 잠시 후 다시 시도해주세요.");
            }

            return transactionTemplate.execute(status -> processCouponIssuance(policyId, userId));
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("시스템 오류가 발생했습니다.");
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    public String tryDrawGachaWithDbLock(Long userId) {
        Long policyId = 1L;
        return transactionTemplate.execute(status -> {
            CouponPolicy policy = couponPolicyRepository.findById(policyId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰 정책입니다."));

            if (!policy.isAvailable()) {
                throw new IllegalStateException("현재는 쿠폰 발급 기간이 아닙니다.");
            }

            if (couponHistoryRepository.existsByCouponPolicyIdAndUserId(policyId, userId)) {
                throw new IllegalStateException("이미 당첨된 이력이 있습니다.");
            }

            CouponStock stock = couponStockRepository.findByCouponPolicyIdWithLock(policyId)
                    .orElseThrow(() -> new IllegalStateException("해당 쿠폰의 재고 정보가 없습니다."));
            
            stock.decrease();
            
            CouponHistory history = new CouponHistory(policyId, userId);
            couponHistoryRepository.save(history);

            log.info("[DB Lock] 쿠폰 발급 성공 - userId: {}, 남은 재고: {}", userId, stock.getRemainingQuantity());
            
            return policy.getName() + " 당첨! (남은 재고: " + stock.getRemainingQuantity() + ")";
        });
    }

    public String tryDrawGachaWithLua(Long userId) {
        Long policyId = 1L;
        String stockKey = "gacha:stock:" + policyId;
        String userKey = "gacha:winners:" + policyId;

        String luaScript = 
            "if redis.call('sismember', KEYS[2], ARGV[1]) == 1 then return -1 end " +
            "if tonumber(redis.call('get', KEYS[1]) or 0) <= 0 then return -2 end " +
            "redis.call('decr', KEYS[1]) " +
            "redis.call('sadd', KEYS[2], ARGV[1]) " +
            "return redis.call('get', KEYS[1])";

        try {
            Object resultObj = redissonClient.getScript().eval(
                org.redisson.api.RScript.Mode.READ_WRITE,
                luaScript,
                org.redisson.api.RScript.ReturnType.INTEGER,
                java.util.Arrays.asList(stockKey, userKey),
                userId.toString()
            );

            Long result = Long.valueOf(resultObj.toString());

            if (result == -1) throw new IllegalStateException("이미 당첨된 이력이 있습니다.");
            if (result == -2) throw new IllegalStateException("준비된 쿠폰 재고가 모두 소진되었습니다.");

            log.info("[Lua] 쿠폰 발급 성공 - userId: {}, 남은 재고: {}", userId, result);
            return "당첨! (남은 재고: " + result + ")";
            
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private String processCouponIssuance(Long policyId, Long userId) {
        CouponPolicy policy = couponPolicyRepository.findById(policyId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰 정책입니다."));

        if (!policy.isAvailable()) {
            throw new IllegalStateException("현재는 쿠폰 발급 기간이 아닙니다.");
        }

        if (couponHistoryRepository.existsByCouponPolicyIdAndUserId(policyId, userId)) {
            throw new IllegalStateException("이미 당첨된 이력이 있습니다.");
        }

        CouponStock stock = couponStockRepository.findByCouponPolicyId(policyId)
                .orElseThrow(() -> new IllegalStateException("해당 쿠폰의 재고 정보가 없습니다."));
        
        stock.decrease();
        
        CouponHistory history = new CouponHistory(policyId, userId);
        couponHistoryRepository.save(history);

        log.info("쿠폰 발급 성공 - userId: {}, 남은 재고: {}", userId, stock.getRemainingQuantity());
        
        return policy.getName() + " 당첨! (남은 재고: " + stock.getRemainingQuantity() + ")";
    }
}

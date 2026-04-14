package com.gacha.project.service;

import com.gacha.project.entity.Coupon;
import com.gacha.project.entity.CouponHistory;
import com.gacha.project.repository.CouponHistoryRepository;
import com.gacha.project.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class GachaService {

    private final RedissonClient redissonClient;
    private final CouponRepository couponRepository;
    private final CouponHistoryRepository couponHistoryRepository;

    public String tryDrawGacha(Long userId) {
        String lockKey = "lock:gacha:coupon:1"; // 1번 쿠폰에 대한 락
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 최대 10초 대기, 획득 후 2초 유지
            boolean isLocked = lock.tryLock(10, 2, TimeUnit.SECONDS);

            if (!isLocked) {
                throw new RuntimeException("현재 참여자가 많아 잠시 후 다시 시도해주세요.");
            }

            // --- 락 획득 성공 후 실제 발급 로직 실행 ---
            // 별도 메서드로 분리하여 락 해제 전 트랜잭션 커밋 보장 고려
            return processCouponIssuance(userId);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("시스템 오류가 발생했습니다.");
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Transactional
    public String processCouponIssuance(Long userId) {
        // 1. 쿠폰 정보 조회 (ID 1번으로 고정하여 테스트)
        Coupon coupon = couponRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("이벤트 정보를 찾을 수 없습니다."));

        // 2. 중복 참여 체크
        if (couponHistoryRepository.existsByCouponIdAndUserId(coupon.getId(), userId)) {
            throw new IllegalStateException("이미 당첨된 이력이 있습니다. (1인 1회 한정)");
        }

        // 3. 재고 체크 및 차감
        log.info("유저 {} 가챠 시도. 현재 재고: {}", userId, coupon.getCurrentQuantity());
        coupon.decreaseStock();

        // 4. 당첨 내역 저장
        CouponHistory history = new CouponHistory(coupon.getId(), userId);
        couponHistoryRepository.save(history);

        return coupon.getName() + " 당첨! (남은 재고: " + coupon.getCurrentQuantity() + ")";
    }
}

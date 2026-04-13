package com.gacha.project.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class GachaService {

    private final RedissonClient redissonClient;
    // 테스트용 메모리 재고 (실제 운영 시에는 DB 연동 필수)
    private int couponStock = 10; 

    public String tryDrawGacha(Long userId) {
        String lockKey = "lock:gacha:coupon";
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 락 획득 시도 (최대 10초 대기, 획득 후 2초 유지)
            boolean isLocked = lock.tryLock(10, 2, TimeUnit.SECONDS);

            if (!isLocked) {
                log.info("유저 {} - 락 획득 실패 (대기 시간 초과)", userId);
                throw new RuntimeException("현재 참여자가 많아 잠시 후 다시 시도해주세요.");
            }

            // --- 비즈니스 로직 (재고 체크 및 차감) ---
            log.info("유저 {} 가챠 로직 진입. 현재 재고: {}", userId, couponStock);
            
            if (couponStock <= 0) {
                throw new IllegalStateException("준비된 쿠폰이 모두 소진되었습니다.");
            }

            couponStock--;
            return "치킨 1마리 당첨! (남은 재고: " + couponStock + ")";

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("시스템 오류가 발생했습니다.");
        } finally {
            // 락 해제 (현재 스레드가 락을 가지고 있는 경우에만)
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}

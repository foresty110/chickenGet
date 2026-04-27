package com.gacha.project.repository;

import com.gacha.project.entity.CouponHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponHistoryRepository extends JpaRepository<CouponHistory, Long> {
    boolean existsByCouponPolicyIdAndUserId(Long couponPolicyId, Long userId);
}

package com.gacha.project.repository;

import com.gacha.project.entity.CouponStock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CouponStockRepository extends JpaRepository<CouponStock, Long> {
    Optional<CouponStock> findByCouponPolicyId(Long couponPolicyId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from CouponStock s where s.couponPolicyId = :policyId")
    Optional<CouponStock> findByCouponPolicyIdWithLock(@Param("policyId") Long policyId);
}


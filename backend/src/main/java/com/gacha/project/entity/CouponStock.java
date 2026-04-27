package com.gacha.project.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class CouponStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long couponPolicyId;

    @Column(nullable = false)
    private int totalQuantity;

    @Column(nullable = false)
    private int remainingQuantity;

    public CouponStock(Long couponPolicyId, int totalQuantity) {
        this.couponPolicyId = couponPolicyId;
        this.totalQuantity = totalQuantity;
        this.remainingQuantity = totalQuantity;
    }

    public void decrease() {
        if (this.remainingQuantity <= 0) {
            throw new IllegalStateException("준비된 쿠폰 재고가 모두 소진되었습니다.");
        }
        this.remainingQuantity--;
    }
}

package com.gacha.project.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(uniqueConstraints = {
    @UniqueConstraint(name = "uk_coupon_policy_user", columnNames = {"couponPolicyId", "userId"})
})
public class CouponHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long couponPolicyId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private LocalDateTime issuedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponStatus status;

    public CouponHistory(Long couponPolicyId, Long userId) {
        this.couponPolicyId = couponPolicyId;
        this.userId = userId;
        this.issuedAt = LocalDateTime.now();
        this.status = CouponStatus.ISSUED;
    }
}

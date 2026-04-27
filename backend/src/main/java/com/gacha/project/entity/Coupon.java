package com.gacha.project.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int totalQuantity;

    @Column(nullable = false)
    private int currentQuantity;

    public Coupon(String name, int totalQuantity) {
        this.name = name;
        this.totalQuantity = totalQuantity;
        this.currentQuantity = totalQuantity;
    }

    public void decreaseStock() {
        if (this.currentQuantity <= 0) {
            throw new IllegalStateException("준비된 쿠폰이 모두 소진되었습니다.");
        }
        this.currentQuantity--;
    }
}

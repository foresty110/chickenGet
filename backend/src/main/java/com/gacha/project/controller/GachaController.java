package com.gacha.project.controller;

import com.gacha.project.dto.GachaResponse;
import com.gacha.project.service.GachaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "선착순 쿠폰 발급 요청 API", description = "쿠폰 뽑기 및 재고 관리 관련 API")
@RestController
@RequestMapping("/api/v1/gacha")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class GachaController {

    private final GachaService gachaService;

    @Operation(summary = "선착순 할인 쿠폰 뽑기", description = "분산 락을 활용하여 선착순으로 쿠폰을 발급합니다.")
    @PostMapping("/draw")
    public GachaResponse<String> draw(@RequestParam Long userId) {
        try {
            String result = gachaService.tryDrawGacha(userId);
            return GachaResponse.ok("성공", result);
        } catch (Exception e) {
            log.error("가챠 발급 실패 - userId: {}, error: {}", userId, e.getMessage());
            return GachaResponse.fail(e.getMessage());
        }
    }

    @Operation(summary = "선착순 할인 쿠폰 뽑기 (DB 락)", description = "DB 비관적 락을 활용하여 선착순으로 쿠폰을 발급합니다.")
    @PostMapping("/draw-db")
    public GachaResponse<String> drawDb(@RequestParam Long userId) {
        try {
            String result = gachaService.tryDrawGachaWithDbLock(userId);
            return GachaResponse.ok("성공", result);
        } catch (Exception e) {
            log.error("[DB Lock] 가챠 발급 실패 - userId: {}, error: {}", userId, e.getMessage());
            return GachaResponse.fail(e.getMessage());
        }
    }

    @Operation(summary = "선착순 할인 쿠폰 뽑기 (Redis Lua)", description = "Redis Lua 스크립트를 활용하여 초고속으로 쿠폰을 발급합니다.")
    @PostMapping("/draw-lua")
    public GachaResponse<String> drawLua(@RequestParam Long userId) {
        try {
            String result = gachaService.tryDrawGachaWithLua(userId);
            return GachaResponse.ok("성공", result);
        } catch (Exception e) {
            return GachaResponse.fail(e.getMessage());
        }
    }
}

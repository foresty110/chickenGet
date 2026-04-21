package com.gacha.project.controller;

import com.gacha.project.dto.GachaResponse;
import com.gacha.project.service.GachaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "가챠(Gacha) API", description = "쿠폰 뽑기 및 재고 관리 관련 API")
@RestController
@RequestMapping("/api/v1/gacha")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class GachaController {

    private final GachaService gachaService;

    @Operation(summary = "치킨 쿠폰 뽑기", description = "분산 락을 활용하여 선착순으로 치킨 쿠폰을 발급합니다.")
    @PostMapping("/draw")
    public GachaResponse<String> draw(@RequestParam Long userId) {
        try {
            String result = gachaService.tryDrawGacha(userId);
            return GachaResponse.ok("성공", result);
        } catch (Exception e) {
            return GachaResponse.fail(e.getMessage());
        }
    }
}

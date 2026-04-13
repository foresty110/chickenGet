package com.gacha.project.controller;

import com.gacha.project.dto.GachaResponse;
import com.gacha.project.service.GachaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/gacha")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class GachaController {

    private final GachaService gachaService;

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

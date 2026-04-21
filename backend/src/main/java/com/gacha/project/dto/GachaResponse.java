package com.gacha.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "공통 응답 객체")
public class GachaResponse<T> {
    
    @Schema(description = "성공 여부", example = "true")
    private boolean success;
    
    @Schema(description = "응답 메시지", example = "성공")
    private String message;
    
    @Schema(description = "응답 데이터")
    private T data;

    public static <T> GachaResponse<T> ok(String message, T data) {
        return GachaResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> GachaResponse<T> fail(String message) {
        return GachaResponse.<T>builder()
                .success(false)
                .message(message)
                .build();
    }
}

package com.gacha.project.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GachaResponse<T> {
    private boolean success;
    private String message;
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

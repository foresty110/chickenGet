package com.gacha.project.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ChickenGet API 명세서")
                        .description("실시간 선착순 치킨 쿠폰 가챠 시스템 API 문서입니다.")
                        .version("v1.0.0"));
    }
}

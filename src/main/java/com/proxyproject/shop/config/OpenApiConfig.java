package com.proxyproject.shop.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI().info(new Info()
            .title("Shop Service API")
            .version("v1")
            .description("Customers, Orders, Payments, Checkout with JWT security"));
    }

    @Bean
    public OpenApiCustomizer securityForProtectedPaths() {
        return openApi -> openApi.getPaths().forEach((path, item) -> {
            // Ajusta aquí cuáles son públicos
            boolean isPublic = path.startsWith("/auth/")
                             || path.startsWith("/api/products/");

            if (!isPublic) {
                item.readOperations().forEach(op ->
                    op.addSecurityItem(new SecurityRequirement().addList("bearerAuth")));
            }
        });
    }
}

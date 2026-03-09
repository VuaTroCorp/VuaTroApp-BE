package fpt.ntu.vuatrovn.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("VuaTro API")
                .version("1.0.0")
                .description("API cho hệ thống VuaTro – Login Google OAuth2"))
            .addSecurityItem(new SecurityRequirement().addList("oauth2"))
            .components(new io.swagger.v3.oas.models.Components()
                .addSecuritySchemes("oauth2",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                )
            );
    }
}

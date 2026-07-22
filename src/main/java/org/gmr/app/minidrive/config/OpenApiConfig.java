package org.gmr.app.minidrive.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;

public class OpenApiConfig {

    @Bean
    public OpenAPI miniDriveOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MiniDrive API")
                        .description(
                                "API para almacenamiento y administración " +
                                        "de archivos con Amazon S3 y MongoDB"
                        )
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Gerardo Mandujano")
                        )
                );
    }
}
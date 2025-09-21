package org.garlikoff.restdata.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;

/**
 * Настраивает документацию OpenAPI/Swagger для проекта Rest Data Spain.
 */
public class SwaggerConfig {
    /**
     * Создаёт бин OpenAPI, публикующий базовые метаданные API.
     *
     * @return предварительно настроенный экземпляр OpenAPI для документации
     */
    @Bean
    public OpenAPI customOpenAPI() {
        var info = new Info();
        info.setDescription("rest-data proxy");
        info.setVersion("1.0");
        return new OpenAPI()
                .components(new Components())
                .info(info);
    }
}

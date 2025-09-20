package org.garlikoff.restdata.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;

/**
 * Configures the OpenAPI/Swagger documentation for the REST Data Spain project.
 */
public class SwaggerConfig {
    /**
     * Creates an OpenAPI bean to expose the basic API metadata.
     *
     * @return the preconfigured OpenAPI instance for documentation
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

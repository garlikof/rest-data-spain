package org.garlikoff.restdata.config;

import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;
import org.garlikoff.restdata.service.vector.TextEmbeddingService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация клиента Milvus.
 */
@Configuration
@EnableConfigurationProperties(MilvusProperties.class)
public class MilvusConfiguration {

    @Bean(destroyMethod = "close")
    public MilvusServiceClient milvusServiceClient(MilvusProperties properties) {
        ConnectParam connectParam = ConnectParam.newBuilder()
                .withHost(properties.getHost())
                .withPort(properties.getPort())
                .build();
        return new MilvusServiceClient(connectParam);
    }

    @Bean
    public TextEmbeddingService textEmbeddingService(MilvusProperties properties) {
        return new TextEmbeddingService(properties.getDimension());
    }
}

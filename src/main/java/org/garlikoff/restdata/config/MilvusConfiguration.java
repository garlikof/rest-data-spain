package org.garlikoff.restdata.config;

import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация клиента Milvus.
 */
@Configuration
@EnableConfigurationProperties(MilvusProperties.class)
public class MilvusConfiguration {

    /**
     * Создаёт и настраивает клиент Milvus.
     *
     * @param properties конфигурация подключения
     * @return настроенный клиент
     */
    @Bean(destroyMethod = "close")
    public MilvusServiceClient milvusClient(MilvusProperties properties) {
        ConnectParam.Builder builder = ConnectParam.newBuilder()
            .withHost(properties.getHost())
            .withPort(properties.getPort())
            .withDatabaseName(properties.getDatabase());
        if (StringUtils.isNotBlank(properties.getUsername())) {
            String token = properties.getUsername();
            if (StringUtils.isNotBlank(properties.getPassword())) {
                token = token + ":" + properties.getPassword();
            }
            builder.withToken(token);
        }
        return new MilvusServiceClient(builder.build());
    }
}

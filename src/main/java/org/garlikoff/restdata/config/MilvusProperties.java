package org.garlikoff.restdata.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Настройки подключения к Milvus, используемые для выгрузки векторных данных.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "milvus")
public class MilvusProperties {

    /**
     * Хост Milvus.
     */
    private String host = "localhost";

    /**
     * Порт Milvus.
     */
    private int port = 19530;

    /**
     * Название коллекции, в которую выгружаются данные.
     */
    private String collectionName = "real_estate_objects";

    /**
     * Размерность векторного представления.
     */
    private int dimension = 128;

    /**
     * Создавать ли коллекцию автоматически, если она отсутствует.
     */
    private boolean autoCreateCollection = true;
}

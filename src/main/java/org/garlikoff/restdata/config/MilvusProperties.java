package org.garlikoff.restdata.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Настройки подключения и схемы коллекции Milvus для объектов недвижимости.
 */
@Data
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
     * Имя базы данных Milvus.
     */
    private String database = "default";
    /**
     * Имя коллекции, в которой хранятся векторы объектов недвижимости.
     */
    private String collection = "real_estate_objects";
    /**
     * Имя поля коллекции, содержащего векторные представления.
     */
    private String vectorField = "embedding";
    /**
     * Размерность создаваемых векторов.
     */
    private int dimension = 256;
    /**
     * Тип индекса Milvus.
     */
    private String indexType = "IVF_FLAT";
    /**
     * Тип метрики для поиска.
     */
    private String metricType = "COSINE";
    /**
     * Дополнительные параметры индекса в формате JSON.
     */
    private String indexParams = "{\"nlist\":1024}";
    /**
     * Параметры поиска Milvus в формате JSON.
     */
    private String searchParams = "{\"nprobe\":16}";
    /**
     * Имя пользователя Milvus (опционально).
     */
    private String username;
    /**
     * Пароль Milvus (опционально).
     */
    private String password;
    /**
     * Количество шардов коллекции.
     */
    private int shardsNum = 2;
    /**
     * Максимальная длина текстового описания, сохраняемого в Milvus.
     */
    private int descriptionMaxLength = 1024;
    /**
     * Целевой язык переводов для формирования описаний.
     */
    private String language = "es";
}

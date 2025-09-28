package org.garlikoff.restdata.service;

import io.milvus.client.MilvusServiceClient;
import io.milvus.param.MetricType;
import io.milvus.param.index.CreateIndexParam;
import org.garlikoff.restdata.config.MilvusProperties;
import org.garlikoff.restdata.repo.RealEstateObjectParamRepository;
import org.garlikoff.restdata.repo.RealEstateObjectRepository;
import org.garlikoff.restdata.repo.TranslationRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RealEstateMilvusServiceTest {

    @Test
    void configureIndexBuilderSkipsExtraParamsWhenIndexTypeUnavailable() {
        MilvusProperties properties = new MilvusProperties();
        properties.setIndexType("missing_type");
        properties.setIndexParams("{\"nlist\":256}");

        RealEstateMilvusService service = new RealEstateMilvusService(
            Mockito.mock(MilvusServiceClient.class),
            properties,
            Mockito.mock(RealEstateObjectRepository.class),
            Mockito.mock(RealEstateObjectParamRepository.class),
            Mockito.mock(TranslationRepository.class),
            Mockito.mock(SimpleEmbeddingService.class),
            Mockito.mock(RealEstateDescriptionBuilder.class)
        );

        CreateIndexParam.Builder builder = CreateIndexParam.newBuilder()
            .withFieldName(properties.getVectorField())
            .withIndexName(properties.getVectorField() + "_idx")
            .withMetricType(MetricType.COSINE);

        boolean applied = service.configureIndexBuilder(builder);

        assertFalse(applied, "index type should not be applied when enum is unavailable");
        CreateIndexParam param = builder.build();
        String extraParam = readExtraParam(param);
        assertTrue(extraParam == null || extraParam.isBlank(), "extra params must be omitted for AutoIndex");
    }

    @Test
    void configureIndexBuilderAppliesExtraParamsWhenIndexTypeAvailable() {
        MilvusProperties properties = new MilvusProperties();
        properties.setIndexType("IVF_FLAT");
        properties.setIndexParams("{\"nlist\":128}");

        RealEstateMilvusService service = new RealEstateMilvusService(
            Mockito.mock(MilvusServiceClient.class),
            properties,
            Mockito.mock(RealEstateObjectRepository.class),
            Mockito.mock(RealEstateObjectParamRepository.class),
            Mockito.mock(TranslationRepository.class),
            Mockito.mock(SimpleEmbeddingService.class),
            Mockito.mock(RealEstateDescriptionBuilder.class)
        );

        CreateIndexParam.Builder builder = CreateIndexParam.newBuilder()
            .withFieldName(properties.getVectorField())
            .withIndexName(properties.getVectorField() + "_idx")
            .withMetricType(MetricType.COSINE);

        boolean applied = service.configureIndexBuilder(builder);

        assertTrue(applied, "explicit index type should be applied when available");
        CreateIndexParam param = builder.build();
        assertEquals(properties.getIndexParams(), readExtraParam(param),
            "extra params must match configured value when index type is set");
    }

    private static String readExtraParam(CreateIndexParam param) {
        try {
            Method getter = param.getClass().getMethod("getExtraParam");
            Object value = getter.invoke(param);
            return value != null ? value.toString() : null;
        } catch (NoSuchMethodException ignored) {
            try {
                Field field = param.getClass().getDeclaredField("extraParam");
                field.setAccessible(true);
                Object value = field.get(param);
                return value != null ? value.toString() : null;
            } catch (ReflectiveOperationException reflectionFailure) {
                throw new IllegalStateException("Unable to inspect extra param", reflectionFailure);
            }
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Unable to read extra param", ex);
        }
    }
}

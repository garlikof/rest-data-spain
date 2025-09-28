package org.garlikoff.restdata.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Service responsible for preparing Milvus collection creation requests without depending on the
 * Milvus client at compile time.
 */
public class RealEstateMilvusService {

    /**
     * Applies the configured index type and extra parameters to the provided builder. Extra
     * parameters are sent only when a concrete index type is successfully resolved to keep the
     * request compatible with Milvus AutoIndex.
     *
     * @param builder the Milvus builder instance (CreateCollectionParam.Builder in production)
     * @param indexTypeName the configured index type name
     * @param extraParamsJson extra parameters serialized as JSON
     */
    public void createCollection(Object builder, String indexTypeName, String extraParamsJson) {
        Objects.requireNonNull(builder, "builder must not be null");

        boolean indexApplied = applyIndexType(builder, indexTypeName);
        if (indexApplied && extraParamsJson != null && !extraParamsJson.isBlank()) {
            applyExtraParam(builder, extraParamsJson);
        }
    }

    protected void applyExtraParam(Object builder, String extraParamsJson) {
        Method method = findExtraParamMethod(builder.getClass());
        if (method == null) {
            throw new IllegalStateException(
                    "Unable to find withExtraParam(String) method on builder " + builder.getClass());
        }

        try {
            method.invoke(builder, extraParamsJson);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Failed to apply extra params", e);
        }
    }

    private Method findExtraParamMethod(Class<?> builderClass) {
        for (Method candidate : builderClass.getMethods()) {
            if (!"withExtraParam".equals(candidate.getName())) {
                continue;
            }
            Class<?>[] parameterTypes = candidate.getParameterTypes();
            if (parameterTypes.length == 1 && parameterTypes[0].isAssignableFrom(String.class)) {
                return candidate;
            }
        }
        return null;
    }

    protected boolean applyIndexType(Object builder, String indexTypeName) {
        if (builder == null || indexTypeName == null || indexTypeName.isBlank()) {
            return false;
        }

        return setIndexType(builder, indexTypeName, "io.milvus.param.index.IndexType")
                || setIndexType(builder, indexTypeName, "io.milvus.param.IndexType");
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected boolean setIndexType(Object builder, String indexTypeName, String className) {
        try {
            Class<?> indexTypeClass = Class.forName(className);
            if (!Enum.class.isAssignableFrom(indexTypeClass)) {
                return false;
            }

            Class<? extends Enum> enumClass = (Class<? extends Enum>) indexTypeClass;
            Enum<?> indexTypeValue = Enum.valueOf(enumClass, indexTypeName);
            Method method = builder.getClass().getMethod("withIndexType", indexTypeClass);
            method.invoke(builder, indexTypeValue);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        } catch (NoSuchMethodException
                | IllegalAccessException
                | InvocationTargetException
                | IllegalArgumentException e) {
            return false;
        }
    }
}

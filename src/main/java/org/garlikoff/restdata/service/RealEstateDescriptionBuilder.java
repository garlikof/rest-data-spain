package org.garlikoff.restdata.service;

import org.garlikoff.restdata.model.Location;
import org.garlikoff.restdata.model.RealEstateObjectParam;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * Формирует текстовое описание параметров объекта недвижимости на основе переводов.
 */
@Component
public class RealEstateDescriptionBuilder {
    private static final String YES = "Sí";
    private static final String NO = "No";

    /**
     * Собирает текстовое описание, используя переводы словарных значений.
     *
     * @param param                параметры объекта
     * @param translationResolver  словарь переводов (ключ слова -> перевод)
     * @return связное текстовое описание
     */
    public String buildDescription(RealEstateObjectParam param, Map<String, String> translationResolver) {
        StringBuilder builder = new StringBuilder();
        appendLocation(builder, param.getLocation(), translationResolver);
        appendNumeric(builder, "Superficie", Optional.ofNullable(param.getArea()).map(v -> v + " m²").orElse(null));
        appendNumeric(builder, "Dormitorios", valueOrNull(param.getNumberOfBedrooms()));
        appendNumeric(builder, "Baños", valueOrNull(param.getNumberOfBathrooms()));
        appendDictionary(builder, "Tipo de vivienda", param.getType(), translationResolver);
        appendDictionary(builder, "Mobiliario", param.getFurnishings(), translationResolver);
        appendBoolean(builder, "Ascensor", param.getElevator());
        appendDictionary(builder, "Balcón o terraza", param.getBalconyTerrace(), translationResolver);
        appendDictionary(builder, "Garaje o aparcamiento", param.getGarageParking(), translationResolver);
        appendDictionary(builder, "Jardín o patio", param.getGardenYard(), translationResolver);
        appendBoolean(builder, "Piscina", param.getPool());
        appendBoolean(builder, "Trastero", param.getStoreroom());
        appendDictionary(builder, "Estado", param.getHousingCondition(), translationResolver);
        appendDictionary(builder, "Planta", param.getFloor(), translationResolver);
        appendBoolean(builder, "Aire acondicionado", param.getAirConditioner());
        appendDictionary(builder, "Calefacción", param.getHeating(), translationResolver);
        appendDictionary(builder, "Certificado energético", param.getEnergyCertificate(), translationResolver);
        appendNumeric(builder, "Año de construcción", valueOrNull(param.getYearBuilt()));
        appendDictionary(builder, "Orientación", param.getOrientation(), translationResolver);
        return builder.toString().trim();
    }

    private static void appendLocation(StringBuilder builder, Location location, Map<String, String> translationResolver) {
        if (location == null || location.getNameKey() == null) {
            return;
        }
        String translated = translationResolver.getOrDefault(location.getNameKey(), location.getNameKey());
        builder.append("Ubicación: ").append(translated).append('.').append(' ');
    }

    private static void appendNumeric(StringBuilder builder, String label, String value) {
        if (value == null) {
            return;
        }
        builder.append(label).append(':').append(' ').append(value).append('.').append(' ');
    }

    private static String valueOrNull(Number number) {
        return number == null ? null : String.valueOf(number);
    }

    private static void appendDictionary(StringBuilder builder, String label, Object word, Map<String, String> translationResolver) {
        if (word == null) {
            return;
        }
        String key = null;
        try {
            key = (String) word.getClass().getMethod("getKey").invoke(word);
        } catch (ReflectiveOperationException ignored) {
            // Если структура изменилась, пропускаем значение.
        }
        if (key == null) {
            return;
        }
        String translated = translationResolver.getOrDefault(key, key);
        builder.append(label).append(':').append(' ').append(translated).append('.').append(' ');
    }

    private static void appendBoolean(StringBuilder builder, String label, Boolean value) {
        if (value == null) {
            return;
        }
        builder.append(label).append(':').append(' ').append(Boolean.TRUE.equals(value) ? YES : NO).append('.').append(' ');
    }
}

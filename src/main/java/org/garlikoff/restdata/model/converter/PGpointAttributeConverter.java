package org.garlikoff.restdata.model.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.postgresql.geometric.PGpoint;

/**
 * Converts between {@link PGpoint} objects and the PostgreSQL {@code point} textual representation.
 */
@Converter(autoApply = false)
public class PGpointAttributeConverter implements AttributeConverter<PGpoint, String> {

    @Override
    public String convertToDatabaseColumn(PGpoint attribute) {
        if (attribute == null) {
            return null;
        }
        return "(" + format(attribute.x) + "," + format(attribute.y) + ")";
    }

    @Override
    public PGpoint convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        String value = dbData.trim();
        if (value.isEmpty()) {
            return null;
        }
        if (value.startsWith("(") && value.endsWith(")")) {
            value = value.substring(1, value.length() - 1);
        }

        String[] parts = value.split(",");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid PostgreSQL point value: " + dbData);
        }

        try {
            double x = Double.parseDouble(parts[0].trim());
            double y = Double.parseDouble(parts[1].trim());
            return new PGpoint(x, y);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid PostgreSQL point numeric values: " + dbData, ex);
        }
    }

    private String format(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException("Invalid coordinate value: " + value);
        }
        return Double.toString(value);
    }
}

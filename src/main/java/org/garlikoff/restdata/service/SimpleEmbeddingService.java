package org.garlikoff.restdata.service;

import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.Locale;

/**
 * Простейший генератор векторных представлений текста.
 */
@Component
public class SimpleEmbeddingService {

    /**
     * Формирует нормализованный вектор фиксированной размерности из текстового описания.
     *
     * @param text       исходный текст
     * @param dimension  требуемая размерность вектора
     * @return нормализованный вектор признаков
     */
    public float[] embed(String text, int dimension) {
        if (dimension <= 0) {
            throw new IllegalArgumentException("Vector dimension must be positive");
        }
        float[] vector = new float[dimension];
        if (text == null || text.isBlank()) {
            vector[0] = 1f;
            return vector;
        }
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFKD)
            .toLowerCase(Locale.ROOT);
        for (int i = 0; i < normalized.length(); i++) {
            int index = normalized.charAt(i) % dimension;
            vector[index] += 1f;
        }
        float norm = 0f;
        for (float value : vector) {
            norm += value * value;
        }
        norm = (float) Math.sqrt(norm);
        if (norm == 0f) {
            vector[0] = 1f;
            return vector;
        }
        for (int i = 0; i < vector.length; i++) {
            vector[i] /= norm;
        }
        return vector;
    }
}

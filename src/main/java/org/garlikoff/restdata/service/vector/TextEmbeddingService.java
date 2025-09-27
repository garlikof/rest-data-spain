package org.garlikoff.restdata.service.vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.StringTokenizer;

/**
 * Простейший сервис построения векторных представлений текста.
 * <p>
 * В реальной системе вместо него следует использовать сервис эмбеддингов
 * (например, OpenAI, HuggingFace и т.д.). Данный вариант обеспечивает
 * детерминированный результат, что позволяет интегрировать Milvus даже без
 * внешних зависимостей.
 */
public class TextEmbeddingService {

    private final int dimension;

    public TextEmbeddingService(int dimension) {
        this.dimension = dimension;
    }

    /**
     * Строит простое векторное представление текста на основе счётчиков токенов.
     *
     * @param text текст для кодирования
     * @return вектор фиксированной размерности
     */
    public List<Float> embed(String text) {
        float[] vector = new float[dimension];
        if (text != null && !text.isBlank()) {
            String normalized = text.toLowerCase(Locale.ROOT);
            StringTokenizer tokenizer = new StringTokenizer(normalized, " ,.;:\n\t\r!?()[]{}<>-_");
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                int index = Math.floorMod(Objects.hash(token), dimension);
                vector[index] += 1.0f;
            }
            normalize(vector);
        }
        List<Float> result = new ArrayList<>(dimension);
        for (float value : vector) {
            result.add(value);
        }
        return result;
    }

    private void normalize(float[] vector) {
        double sumSquares = 0.0;
        for (float value : vector) {
            sumSquares += value * value;
        }
        if (sumSquares <= 0.0) {
            return;
        }
        double norm = Math.sqrt(sumSquares);
        for (int i = 0; i < vector.length; i++) {
            vector[i] = (float) (vector[i] / norm);
        }
    }
}

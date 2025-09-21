package org.garlikoff.restdata.model;

import java.io.Serializable;

/**
 * Составной идентификатор связи {@link Language} и {@link Word}.
 */
public class LanguageId implements Serializable {
    /**
     * Ключ слова.
     */
    private Word wordKey;
    /**
     * Ключ языка.
     */
    private Language languageKey;
}

package org.garlikoff.restdata.model;

import java.io.Serializable;

/**
 * Composite identifier for {@link Language} and {@link Word} association.
 */
public class LanguageId implements Serializable {
    /**
     * The word key.
     */
    private Word wordKey;
    /**
     * The language key.
     */
    private Language languageKey;
}

package org.bighw1.big_hw_1.enums;

public enum CuisineType {
    ITALIAN("Italian"),
    ASIAN("Asian"),
    MEXICAN("Mexican"),
    FRENCH("French"),
    MEDITERRANEAN("Mediterranean"),
    INDIAN("Indian"),
    AMERICAN("American"),
    BRITISH("British"),
    MIDDLE_EASTERN("Middle-Eastern"),
    GREEK("Greek");

    private final String label;

    CuisineType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static boolean contains(String value) {
        for (CuisineType ct : values()) {
            if (ct.label.equals(value)) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return label;
    }
}

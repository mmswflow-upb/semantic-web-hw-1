package org.bighw1.big_hw_1.enums;

public enum DifficultyLevel {
    BEGINNER("Beginner"),
    INTERMEDIATE("Intermediate"),
    ADVANCED("Advanced");

    private final String label;

    DifficultyLevel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static boolean contains(String value) {
        for (DifficultyLevel d : values()) {
            if (d.label.equals(value)) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return label;
    }
}

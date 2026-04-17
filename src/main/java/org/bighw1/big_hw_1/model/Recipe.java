package org.bighw1.big_hw_1.model;

public class Recipe {

    private String id;
    private String title;
    private String cuisineType1;
    private String cuisineType2;
    private String difficulty;

    public Recipe() {}

    public Recipe(String id, String title, String cuisineType1, String cuisineType2, String difficulty) {
        this.id = id;
        this.title = title;
        this.cuisineType1 = cuisineType1;
        this.cuisineType2 = cuisineType2;
        this.difficulty = difficulty;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCuisineType1() { return cuisineType1; }
    public void setCuisineType1(String cuisineType1) { this.cuisineType1 = cuisineType1; }

    public String getCuisineType2() { return cuisineType2; }
    public void setCuisineType2(String cuisineType2) { this.cuisineType2 = cuisineType2; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
}

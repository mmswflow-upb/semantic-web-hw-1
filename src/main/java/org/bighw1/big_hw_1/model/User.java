package org.bighw1.big_hw_1.model;

public class User {

    private String id;
    private String name;
    private String surname;
    private String skillLevel;
    private String preferredCuisine;

    public User() {}

    public User(String id, String name, String surname, String skillLevel, String preferredCuisine) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.skillLevel = skillLevel;
        this.preferredCuisine = preferredCuisine;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getSkillLevel() { return skillLevel; }
    public void setSkillLevel(String skillLevel) { this.skillLevel = skillLevel; }

    public String getPreferredCuisine() { return preferredCuisine; }
    public void setPreferredCuisine(String preferredCuisine) { this.preferredCuisine = preferredCuisine; }
}

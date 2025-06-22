package com.example.myapplication;

public class Template {
    private int id;
    private String name;
    private String description;
    private int layoutResId;
    private boolean isSelected;

    public Template(int id, String name, String description, int layoutResId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.layoutResId = layoutResId;
        this.isSelected = false;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getLayoutResId() {
        return layoutResId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    // Setters
    public void setSelected(boolean selected) {
        isSelected = selected;
    }
} 
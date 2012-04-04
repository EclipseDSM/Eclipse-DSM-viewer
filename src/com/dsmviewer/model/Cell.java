package com.dsmviewer.model;

public class Cell {
    private int weight;
    private boolean violated;
    private String violation;
    
    public Cell() {
        this(0, false, "N/A");
    }
    
    public Cell(int weight, boolean violated, String violation) {
        this.weight = weight;
        this.violated = violated;
        this.violation = violation;
    }

    public int getWeight() {
        return weight;
    }
    public void setWeight(int weight) {
        this.weight = weight;
    }
    public boolean isViolated() {
        return violated;
    }
    public void setViolated(boolean violated) {
        this.violated = violated;
    }
    public String getViolation() {
        return violation;
    }
    public void setViolation(String violation) {
        this.violation = violation;
    }
}

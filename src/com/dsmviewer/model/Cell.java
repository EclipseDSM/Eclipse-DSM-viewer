package com.dsmviewer.model;

import java.util.Set;

import org.dtangler.core.analysisresult.Violation;

public class Cell {
    private int weight;
    private boolean violated;
    private Set<Violation> violation;
    
    public Cell() {
        this(0, false, null);
    }
    
    public Cell(int weight, boolean violated, Set<Violation> set) {
        this.weight = weight;
        this.violated = violated;
        this.violation = set;
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
    public Set<Violation> getViolation() {
        return violation;
    }
    public void setViolation(Set<Violation> violation) {
        this.violation = violation;
    }
}

package com.dsmviewer.model;

public class Label {
    private int number;
    int fold;
    private boolean folded;
    String fullname;
    private String shortname;

    public Label(final int number, final int fold, final String fullname,
            final String shortname, final boolean folded) {
        this.number = number;
        this.folded = folded;
        this.fold = fold;
        this.fullname = fullname;
        this.shortname = shortname;
    }

    public int getNumber() {
        return number;
    }

    public int getFold() {
        return fold;
    }

    public String getFullname() {
        return fullname;
    }

    public String getShortname() {
        return shortname;
    }

    public boolean isFolded() {
        return folded;
    }

    public void setFolded(final boolean folded) {
        this.folded = folded;
    }
    
    public String toString()
    {
        return number + ": " + fullname;
    }
}
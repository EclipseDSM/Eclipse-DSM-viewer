package com.dsmviewer.dsm;

import org.eclipse.swt.graphics.Image;

import com.dsmviewer.Activator;

public enum DependencyScope {
    CLASSES("classes"),
    PACKAGES("packages");

    private String displayName;

    DependencyScope(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Image getDisplayIcon() {
        switch (ordinal()) {
        case 0:
            return Activator.getImageFromRegistry("class.gif");
        case 1:
            return Activator.getImageFromRegistry("package.gif");
        default:
            return null;
        }
    }
}

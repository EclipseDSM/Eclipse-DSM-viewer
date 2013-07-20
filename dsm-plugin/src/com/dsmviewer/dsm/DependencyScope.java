package com.dsmviewer.dsm;

import org.dtangler.core.dependencies.Scope;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public enum DependencyScope {
    CLASSES("classes"),
    PACKAGES("packages");

    private String displayName;

    DependencyScope(String displayName) {
        this.displayName = displayName;
    }

    public static DependencyScope parse(Scope scope) {
        for (DependencyScope depScope : values()) {
            if (depScope.equals(scope)) {
                return depScope;
            }
        }
        throw new IllegalArgumentException("Cannot parse the scope " + scope);
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean equals(Scope scope) {
        return scope.getDisplayName().equals(getDisplayName());
    }
}

package com.dsmviewer.dsm;

/**
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public enum DependencyMatrixOrdering {
    NATURAL_ORDERING,
    BY_INSTABILITY;

    public static boolean isDtanglerDefaultOrdering(DependencyMatrixOrdering ordering) {
        return BY_INSTABILITY == ordering;
    }

    public static DependencyMatrixOrdering getPluginDefaultOrdering() {
        return NATURAL_ORDERING;
    }
}

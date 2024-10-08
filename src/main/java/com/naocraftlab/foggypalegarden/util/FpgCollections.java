package com.naocraftlab.foggypalegarden.util;

import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

@UtilityClass
public class FpgCollections {

    public static <T> Set<T> treeSetOf(T... elements) {
        return new TreeSet<>(Arrays.asList(elements));
    }
}

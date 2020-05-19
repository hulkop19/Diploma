package com.company.Sets;

import java.util.List;

public class SetUtils {

    private SetUtils() {}

    public static Set128 getUnion(Set128 set1, Set128 set2, Set128 ... other) throws Exception {
        Set128 union =  new Set128(set1.getLeftNumber() | set2.getLeftNumber()
                               , set1.getRightNumber() | set2.getRightNumber());
        for (var set : other) {
            union = new Set128(union.getLeftNumber() | set.getLeftNumber()
                               , union.getRightNumber() | set.getRightNumber());
        }

        return union;
    }

    public static Set128 getUnion(List<Set128> sets) throws Exception {
        Set128 union = new Set128(sets.get(0));

        for (var set : sets) {
            union = new Set128(union.getLeftNumber() | set.getLeftNumber()
                    , union.getRightNumber() | set.getRightNumber());
        }

        return union;
    }

    public static Set128 getIntersectionWithoutEmptySet(List<Set128> sets) {
        Set128 intersection = new Set128(sets.get(1));

        for (int i = 1; i < sets.size(); ++i) {
            intersection = new Set128(intersection.getLeftNumber() & sets.get(i).getLeftNumber()
                    , intersection.getRightNumber() & sets.get(i).getRightNumber());
        }

        return intersection;
    }
}

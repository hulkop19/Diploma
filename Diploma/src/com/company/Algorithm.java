package com.company;

import com.company.Sets.Set128;
import com.company.Sets.SetUtils;

import java.util.*;

public class Algorithm {

    private Algorithm() {}

    public static void run() throws Exception {
        List<List<Result>> results = new ArrayList<>();
        int targetFamiliesNumber = TargetFamilies.getTargetFamiliesNumber();

        for (int targetFamilyId = 0; targetFamilyId < targetFamiliesNumber; ++targetFamilyId) {
            results.add(new ArrayList<>());

            int maxOtherElemNumber = TargetFamilies.getUniverseSize() - TargetFamilies.getTargetFamilyUniverseSize(targetFamilyId);
            for (int otherElemNumber = 0; otherElemNumber <= maxOtherElemNumber; ++otherElemNumber) {
                results.get(targetFamilyId).add(new Result());

                List<Set128> baseMemberSets = computeBaseMemberSets(targetFamilyId, otherElemNumber);

                backtracking(targetFamilyId, new Set128(1L), baseMemberSets, 0, results, otherElemNumber);

                if (otherElemNumber > 0) {
                    backtracking(targetFamilyId, new Set128(0L), baseMemberSets, 0, results, otherElemNumber);
                }

            }
        }

        viewResults(results);
    }

    private static List<Set128> computeBaseMemberSets(int targetFamilyId, int otherElemNumber) throws Exception {
        List<Set128> tmp = new ArrayList<>();
        List<Set128> baseMemberSets = new ArrayList<>();
        int targetFamilyUniverseSize = TargetFamilies.getTargetFamilyUniverseSize(targetFamilyId);
        Set128 currentSet = new Set128(0);

        for (int elem = targetFamilyUniverseSize + 1; elem <= targetFamilyUniverseSize + otherElemNumber; ++elem) {
            currentSet.addElem(elem);
        }

        baseMemberSets.add(new Set128(currentSet));

        for (var set : TargetFamilies.getTargetFamily(targetFamilyId)) {
            tmp.add(SetUtils.getUnion(set, currentSet));
        }

        while((currentSet.hasElem(targetFamilyUniverseSize + 1) && otherElemNumber != 0)
              || (!currentSet.hasElem(targetFamilyUniverseSize + 1) && otherElemNumber == 0)) {
            if (!tmp.contains(currentSet)
                && TargetFamilies.getSetShare(currentSet, targetFamilyId) < 0) {
                baseMemberSets.add(new Set128(currentSet));
            }
            currentSet.increment(1L);
        }

        baseMemberSets.sort(Comparator.comparingInt(a -> {
            try {
                return TargetFamilies.getSetShare(a, targetFamilyId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        }));

        return baseMemberSets;
    }

    private static int backtracking(int targetFamilyId, Set128 currentFamily, List<Set128> baseMemberSets
                                    , int index, List<List<Result>> results, int otherElemNumber) throws Exception {
        List<Set128> closure = getFcClosure(targetFamilyId, currentFamily, baseMemberSets, otherElemNumber);
        int stepsNumber = 1;

        if (!hasFc(closure, otherElemNumber) && !has12Fc(closure, targetFamilyId, otherElemNumber)) {
            Result currentResult = results.get(targetFamilyId).get(otherElemNumber);
            Set128 currentSet = new Set128(0);
            currentSet.addOtherElements(TargetFamilies.getTargetFamilyUniverseSize(targetFamilyId), otherElemNumber);

            boolean containOtherElemSet = currentFamily.hasElem(1);

            if (getSpecialShare(closure, baseMemberSets, targetFamilyId, index) < currentResult.get(containOtherElemSet)) {
                if (closure.size() > 0) {
                    currentResult.update(containOtherElemSet, TargetFamilies.getFamilyShare(closure, targetFamilyId));
                }

                if (index < baseMemberSets.size() - 1) {
                    Set128 newCurrentFamily = new Set128(currentFamily);
                    newCurrentFamily.addElem(index + 2);
                    stepsNumber += backtracking(targetFamilyId, newCurrentFamily, baseMemberSets, index + 1, results, otherElemNumber);

                    if (!closure.contains(baseMemberSets.get(index + 1))) {
                        newCurrentFamily = new Set128(currentFamily);
                        stepsNumber += backtracking(targetFamilyId, newCurrentFamily, baseMemberSets, index + 1, results, otherElemNumber);
                    }
                }
            }
        }

        return stepsNumber;
    }

    private static int getSpecialShare(List<Set128> family, List<Set128> baseMemberSets, int targetFamilyId, int index) throws Exception {
        int share = TargetFamilies.getFamilyShare(family, targetFamilyId);

        for (int i = index + 1; i < baseMemberSets.size(); ++i) {
            if (!family.contains(baseMemberSets.get(i))) {
                share += TargetFamilies.getSetShare(baseMemberSets.get(i), targetFamilyId);
            }
        }

        return share;
    }

    private static boolean hasFc(List<Set128> family, int otherElemNumber) throws Exception {
        if (has2ElementsFc(family, otherElemNumber)) return true;

        if (has3ElementsFc(family, otherElemNumber)) return true;

        return false;
    }

    private static boolean has2ElementsFc(List<Set128> family, int otherElemNumber) throws Exception {
        if (otherElemNumber <= 2) {
            for (var set : family) {
                int size = set.size();
                if (size <= 2 && size > 0) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean has3ElementsFc(List<Set128> family, int otherElemNumber) throws Exception {
        if (otherElemNumber >= 3) {
            return false;
        }

        List<Set128> threeSizeSets = new ArrayList<>();
        for (var set : family) {
            int size = set.size();

            if (size == 3) {
                threeSizeSets.add(set);
            }
        }

        if (hasThreeMemberSets3ElementFc(threeSizeSets)) {
            return true;
        }

        if (hasFourMemberSets3ElementFc(threeSizeSets)) {
            return true;
        }

        return false;
    }

    private static boolean hasThreeMemberSets3ElementFc(List<Set128> threeSizeSets) throws Exception {
        int counter1 = 0;
        while (counter1 < threeSizeSets.size() - 2) {
            int counter2 = counter1 + 1;
            while (counter2 < threeSizeSets.size() - 1) {
                int counter3 = counter2 + 1;
                while (counter3 < threeSizeSets.size()) {
                    if (SetUtils.getUnion(threeSizeSets.get(counter1), threeSizeSets.get(counter2), threeSizeSets.get(counter3)).size() <= 5) {
                        return true;
                    }
                    ++counter3;
                }
                ++counter2;
            }
            ++counter1;
        }

        return false;
    }

    private static boolean hasFourMemberSets3ElementFc(List<Set128> threeSizeSets) throws Exception {
        int counter1 = 0;
        while (counter1 < threeSizeSets.size() - 3) {
            int counter2 = counter1 + 1;
            while (counter2 < threeSizeSets.size() - 2) {
                int counter3 = counter2 + 1;
                while (counter3 < threeSizeSets.size() - 1) {
                    int counter4 = counter3 + 1;
                    while (counter4 < threeSizeSets.size()) {
                        if (SetUtils.getUnion(threeSizeSets.get(counter1), threeSizeSets.get(counter2)
                                , threeSizeSets.get(counter3), threeSizeSets.get(counter4)).size() <= 7) {
                            return true;
                        }
                        ++counter4;
                    }
                    ++counter3;
                }
                ++counter2;
            }
            ++counter1;
        }

        return false;
    }

    private static boolean has12Fc(List<Set128> mainFamily, int targetFamilyId, int otherElemNumber) throws Exception {
        HashSet<Set128> mainFamilyHashSet = new HashSet<>(mainFamily);
        for (int familyId = 0; familyId < targetFamilyId; ++familyId) {
            var family = TargetFamilies.getTargetFamily(familyId);
            int intersectionSize = SetUtils.getIntersectionWithoutEmptySet(family).size();

            if (otherElemNumber <= intersectionSize) {
                var transpositions = TargetFamilies.transpositions.get(targetFamilyId).get(otherElemNumber).get(familyId);

                for (var transpose : transpositions) {
                    if (mainFamilyHashSet.containsAll(transpose)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private static List<Set128> getFcClosure(int targetFamilyId, Set128 currentFamily, List<Set128> baseMemberSets, int otherElemNumber) throws Exception {
        List<Set128> targetFamily = new ArrayList<>();
        Set128 currentSet = new Set128(0);
        currentSet.addOtherElements(TargetFamilies.getTargetFamilyUniverseSize(targetFamilyId), otherElemNumber);
        Set128 targetFamilyMask = new Set128(0L);

        int counter = 1;
        for (var set : TargetFamilies.getTargetFamily(targetFamilyId)) {
            targetFamilyMask.addElem(counter);
            targetFamily.add(SetUtils.getUnion(set, currentSet));
            ++counter;
        }

        List<Set128> tfClosure = getClosure(targetFamilyId, targetFamilyMask, targetFamily);
        List<Set128> msClosure = getClosure(targetFamilyId, currentFamily, baseMemberSets);
        List<Set128> closure = new ArrayList<>();

        for (var set1 : tfClosure) {
            for (var set2 : msClosure) {
                Set128 union = SetUtils.getUnion(set1, set2);
                if (!closure.contains(union)) {
                    closure.add(union);
                }
            }
        }

        return closure;
    }

    private static List<Set128> getClosure(int targetFamilyId, Set128 currentFamily, List<Set128> baseMemberSets) throws Exception {
        List<Set128> closure = new ArrayList<>();

        for (int i = 0; i < baseMemberSets.size(); ++i) {
            if (currentFamily.hasElem(i + 1)) {
                closure.add(baseMemberSets.get(i));
            }
        }

        int externalCounter = 0;
        while (externalCounter < closure.size()) {
            int innerCounter = externalCounter + 1;

            while (innerCounter < closure.size()) {
                Set128 union = SetUtils.getUnion(closure.get(externalCounter), closure.get(innerCounter));
                if (!closure.contains(union)) {
                    closure.add(union);
                }
                ++innerCounter;
            }
            ++externalCounter;
        }

        return closure;
    }

    private static void viewResults(List<List<Result>> results) {
        for (var line : results) {
            for (var elem : line) {
                System.out.print(elem.get(true) + "##" + elem.get(false) + "\t");
            }
            System.out.println();
        }
    }
}

package com.company;

import com.company.Sets.Set128;
import com.company.Sets.SetUtils;

import java.util.ArrayList;
import java.util.List;

public class TargetFamilies {

    private TargetFamilies() {}

    private static final int universeSize = 13;

    private static final int[][][] targetFamilies =
            {
                    {{}, {1, 2, 3}},
                    {{}, {1, 2, 3, 4}},
                    {{}, {1, 2, 3, 4, 5}},
                    {{}, {1, 2, 3, 4, 5, 6}}
            };

    private static final int[][] weights =
            {
                    {6, 6, 6, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
                    {5, 5, 5, 5, 2, 2, 2, 2, 2, 2, 2, 2, 2},
                    {4, 4, 4, 4, 4, 2, 2, 2, 2, 2, 2, 2, 2},
                    {8, 8, 8, 8, 8, 8, 6, 6, 6, 6, 6, 6, 6},
            };

    public static List<Integer> targetWeights;
    private static List<List<Set128>> binaryTargetFamilies;
    public static List<List<List<List<List<Set128>>>>> transpositions;
    private static List<Integer> targetFamilyUniverseSizes;

    static {
        try {
            binaryTargetFamilies = convertFamilies();
            targetWeights = computeTargetWeights();
            targetFamilyUniverseSizes = computeTargetFamilyUniverseSizes();
            transpositions = computeTargetFamiliesTranspositions();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<List<Set128>> convertFamilies() throws Exception {
        List<List<Set128>> binaryFamilies = new ArrayList<>();

        for (int familyId = 0; familyId < TargetFamilies.targetFamilies.length; ++familyId) {
            List<Set128> currentFamily = new ArrayList<>();
            for (int setId = 0; setId < TargetFamilies.targetFamilies[familyId].length; ++setId) {
                Set128 currentSet = new Set128(0L);
                for (var elem : TargetFamilies.targetFamilies[familyId][setId]) {
                    currentSet.addElem(elem);
                }
                currentFamily.add(currentSet);
            }
            binaryFamilies.add(currentFamily);
        }

        return binaryFamilies;
    }

    private static List<Integer> computeTargetWeights() throws Exception {
        List<Integer> targetWeights = new ArrayList<>();

        for (int familyId = 0; familyId < TargetFamilies.weights.length; ++familyId) {
            int currentTargetWeight = 0;

            for (int elementId = 0; elementId < universeSize; ++elementId) {
                currentTargetWeight += TargetFamilies.weights[familyId][elementId];
            }

            if (TargetFamilies.weights[familyId].length != universeSize || currentTargetWeight % 2 != 0) {
                throw new Exception("incorrect weights in family " + familyId);
            }

            targetWeights.add(currentTargetWeight / 2);
        }

        return targetWeights;
    }

    private static List<Integer> computeTargetFamilyUniverseSizes() throws Exception {
        List<Integer> targetFamilyUniverseSizes = new ArrayList<>();

        for (List<Set128> binaryTargetFamily : binaryTargetFamilies) {
            Set128 union = new Set128(0);
            for (var set : binaryTargetFamily) {
                union = SetUtils.getUnion(union, set);
            }
            targetFamilyUniverseSizes.add(union.size());
        }

        return targetFamilyUniverseSizes;
    }

    private static List<List<List<List<List<Set128>>>>> computeTargetFamiliesTranspositions() throws Exception {
        List<List<List<List<List<Set128>>>>> tran = new ArrayList<>();
        for (int targetFamilyId = 0; targetFamilyId < getTargetFamiliesNumber(); ++targetFamilyId) {
            tran.add(new ArrayList<>());
            int maxOtherElemNumber = TargetFamilies.getUniverseSize() - TargetFamilies.getTargetFamilyUniverseSize(targetFamilyId);
            for (int otherElemNumber = 0; otherElemNumber <= maxOtherElemNumber; ++otherElemNumber) {
                tran.get(targetFamilyId).add(new ArrayList<>());
                int targetFamilyUniverseSize = TargetFamilies.getTargetFamilyUniverseSize(targetFamilyId);
                for (int familyId = 0; familyId < targetFamilyId; ++familyId) {
                    var family = TargetFamilies.getTargetFamily(familyId);
                    int intersectionSize = SetUtils.getIntersectionWithoutEmptySet(family).size();

                    if (otherElemNumber > intersectionSize) {
                        tran.get(targetFamilyId).get(otherElemNumber).add(new ArrayList<>());
                        continue;
                    }

                    List<Set128> cutFamily = new ArrayList<>();
                    for (int set = 1; set < family.size(); ++set) {
                        Set128 currentSet = new Set128(0);
//                otherElemNumber + 1
                        for (int i = otherElemNumber + 1; i <= 13; ++i) {
                            if (family.get(set).hasElem(i)) {
                                currentSet.addElem(i);
                            }
                        }
                        cutFamily.add(currentSet);
                    }

                    var transpositions = getFamilyTranspositions(cutFamily, targetFamilyUniverseSize, targetFamilyId, otherElemNumber);
                    tran.get(targetFamilyId).get(otherElemNumber).add(transpositions);
                }
            }
        }
        return tran;
    }

    private static List<List<Set128>> getFamilyTranspositions(List<Set128> family, int universeSize, int targetFamilyId, int otherElemNumber) throws Exception {
        List<List<Set128>> results = new ArrayList<>();
        List<List<Integer>> transpositions = new ArrayList<>();
        List<Integer> elements = new ArrayList<>();
        Set128 union = SetUtils.getUnion(family);
        for (int i = 0 ; i < 13; ++i) {
            if (union.hasElem(i + 1)) {
                elements.add(i + 1);
            }
        }

        int targetFamilyUniverseSize = TargetFamilies.getTargetFamilyUniverseSize(targetFamilyId);
        Set128 otherMask = new Set128(0);

        for (int elem = targetFamilyUniverseSize + 1; elem <= targetFamilyUniverseSize + otherElemNumber; ++elem) {
            otherMask.addElem(elem);
        }

        List<Integer> used = new ArrayList<>();
        getTranspositionRecursive(0, elements.size() - 1, transpositions, used, universeSize);

        for (var transpose : transpositions) {
            List<Set128> result = new ArrayList<>();
            for (var set : family) {
                Set128 newSet = new Set128(0L);
                for (int i = 0 ; i < 13; ++i) {
                    if (set.hasElem(i + 1)) {
                        newSet.addElem(transpose.get(elements.indexOf(i + 1)));
                    }
                }
                result.add(SetUtils.getUnion(newSet, otherMask));
            }
            results.add(result);
        }

        return results;
    }

    private static void getTranspositionRecursive(int currentInd, int maxIndex, List<List<Integer>> transpositions, List<Integer> used, int universeSize) {
        if (currentInd > maxIndex) {
            transpositions.add(used);
            return;
        }

        for (int i = 1; i <= universeSize; ++i) {
            if (!used.contains(i)) {
                List<Integer> newUsed = new ArrayList<>(used);
                newUsed.add(i);
                getTranspositionRecursive(currentInd + 1, maxIndex, transpositions, newUsed, universeSize);
            }
        }
    }


    public static int getUniverseSize() {
        return universeSize;
    }

    public static int getSetShare(Set128 set, int targetFamilyId) throws Exception {
        int currentShare = 0;
        for (int i = 0; i < universeSize; ++i) {
            if (set.hasElem(i + 1)) {
                currentShare += weights[targetFamilyId][i];
            }
        }
        currentShare -= targetWeights.get(targetFamilyId);

        return currentShare;
    }

    public static int getFamilyShare(List<Set128> family, int targetFamilyId) throws Exception {
        int share = 0;

        for (var set : family) {
            share += getSetShare(set, targetFamilyId);
        }

        return share;
    }

    public static List<Set128> getTargetFamily(int targetFamilyId) {
        return binaryTargetFamilies.get(targetFamilyId);
    }

    public static int getTargetFamilyUniverseSize(int targetFamilyId) {
        return targetFamilyUniverseSizes.get(targetFamilyId);
    }

    public static int getTargetFamiliesNumber() {
        return binaryTargetFamilies.size();
    }
}

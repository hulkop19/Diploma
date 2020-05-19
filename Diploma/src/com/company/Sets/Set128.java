package com.company.Sets;


public class Set128 {

    private long leftNumber;
    private long rightNumber;

    public Set128(long leftSet, long rightSet) {
        rightNumber = rightSet;
        leftNumber = leftSet;
    }

    public Set128(long rightSet) {
        rightNumber = rightSet;
        leftNumber = 0;
    }

    public Set128(Set128 set) {
        leftNumber = set.leftNumber;
        rightNumber = set.rightNumber;
    }

    public long getLeftNumber() {
        return leftNumber;
    }

    public long getRightNumber() {
        return rightNumber;
    }

    public boolean hasElem(int elem) throws Exception {
        if (elem <= 64 && elem >= 1) {
            return (rightNumber & (1L << (elem - 1))) != 0;
        } else if (elem <= 128 && elem >= 1) {
            return (leftNumber & (1L << (elem - 65))) != 0;
        } else {
            throw new Exception("Index out of bound");
        }
    }

    public void addElem(int elem) throws Exception {
        if (elem <= 64 && elem >= 1) {
            rightNumber |= (1L << (elem - 1));
        } else if (elem <= 128 && elem >= 1) {
            leftNumber |= (1L << (elem - 65));
        } else {
            throw new Exception("Index out of bound");
        }
    }

    public void increment(long step) {
        rightNumber += step;
    }

    public int size() throws Exception {
        int size = 0;

        for (int i = 1; i <= 128; ++i) {
            if (hasElem(i)) {
                ++size;
            }
        }

        return size;
    }

    public void addOtherElements(int targetFamilyUniverseSize, int otherElemNumber) throws Exception {
        for (int elem = targetFamilyUniverseSize + 1; elem <= targetFamilyUniverseSize + otherElemNumber; ++elem) {
            addElem(elem);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Set128)) {
            return false;
        }

        return this.rightNumber == ((Set128) obj).rightNumber
               && this.leftNumber == ((Set128) obj).leftNumber;
    }

    @Override
    public int hashCode() {
        return Long.valueOf(this.getRightNumber()).hashCode();
    }
}

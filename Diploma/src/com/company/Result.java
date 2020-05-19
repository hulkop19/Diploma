package com.company;

public class Result {
    private int d0 = Integer.MAX_VALUE;
    private int d1 = Integer.MAX_VALUE;

    public int get(boolean condition) {
        return (condition) ? d1 : d0;
    }

    public void update(boolean condition, int value) {
        if (condition) {
            d1 = Math.min(d1, value);
        } else {
            d0 = Math.min(d0, value);
        }
    }
}

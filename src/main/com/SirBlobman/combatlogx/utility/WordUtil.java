package com.SirBlobman.combatlogx.utility;

import java.util.stream.IntStream;

public class WordUtil extends Util {
    public static String withAmount(String o, int i) {
        if (i == 1)
            return format(o, i);
        else {
            String s = o + "s";
            return format(s, i);
        }
    }

    public static String getTimeBars(int amount) {
        String s = color("&a");
        for (int i = 0; i < amount; i++)
            s = s + '|';
        return s;
    }

    public static String getPassedBars(int amount) {
        StringBuilder s = new StringBuilder(color("&c"));
        IntStream.range(0, amount).map(i -> '|').forEach(s::append);
        return s.toString();
    }
}
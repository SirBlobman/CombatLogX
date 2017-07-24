package com.SirBlobman.combatlogx.utility;

public class WordUtil extends Util {
    public static String withAmount(String o, int i) {
        if(i == 1) return format(o, i);
        else {
            String s = o + "s";
            return format(s, i);
        }
    }
}
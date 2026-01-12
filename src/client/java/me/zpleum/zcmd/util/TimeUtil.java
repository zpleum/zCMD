package me.zpleum.zcmd.util;

public class TimeUtil {

    public static String format(long ms) {
        long sec = ms / 1000;
        long min = sec / 60;
        sec %= 60;
        return min + "m " + sec + "s";
    }
}

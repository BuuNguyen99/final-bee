package com.example.hairstyle.util;

public class CalculateUtils {
    public static double addRate(int length, double average, double newVal) {
        return (average * length + newVal) / (length + 1);
    }

    public static double replaceRate(int length, double average, double current, double newVal) {
        return (average * length - current + newVal) / length;
    }
}

package com.company;

public class Flags {

    private static boolean zero = false;
    private static boolean carry = false;
    private static boolean negative = false;

    public static boolean getZero() {
        return zero;
    }

    public static void setZero(boolean zero) {
        Flags.zero = zero;
    }

    public static boolean getCarry() {
        return carry;
    }

    public static void setCarry(boolean carry) {
        Flags.carry = carry;
    }

    public static boolean getNegative() {
        return negative;
    }

    public static void setNegative(boolean negative) {
        Flags.negative = negative;
    }
}

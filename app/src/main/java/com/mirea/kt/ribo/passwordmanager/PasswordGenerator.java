package com.mirea.kt.ribo.passwordmanager;

import java.security.SecureRandom;

public class PasswordGenerator {

    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*()-_=+?";

    public static String generate(int length, boolean upper, boolean digits, boolean symbols) {
        String chars = LOWER;
        if (upper) {
            chars = chars + UPPER;
        }
        if (digits) {
            chars = chars + DIGITS;
        }
        if (symbols) {
            chars = chars + SYMBOLS;
        }

        SecureRandom random = new SecureRandom();
        String password = "";

        while (true) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < length; i++) {
                sb.append(chars.charAt(random.nextInt(chars.length())));
            }
            password = sb.toString();

            if (upper && !contains(password, UPPER)) {
                continue;
            }
            if (digits && !contains(password, DIGITS)) {
                continue;
            }
            if (symbols && !contains(password, SYMBOLS)) {
                continue;
            }
            return password;
        }
    }

    private static boolean contains(String password, String group) {
        for (int i = 0; i < password.length(); i++) {
            if (group.indexOf(password.charAt(i)) != -1) {
                return true;
            }
        }
        return false;
    }
}

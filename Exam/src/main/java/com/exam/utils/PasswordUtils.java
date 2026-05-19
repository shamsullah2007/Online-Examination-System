package com.exam.utils;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class PasswordUtils {

    // Hashes a plain text password using BCrypt with cost factor 12
    // Called during registration before saving to database
    public static String hash(String rawPassword) {
        return BCrypt.withDefaults()
                .hashToString(12, rawPassword.toCharArray());
    }

    // Verifies a plain text password against a stored BCrypt hash
    // Called during login to check if entered password matches DB hash
    public static boolean verify(String rawPassword, String storedHash) {
        return BCrypt.verifyer()
                .verify(rawPassword.toCharArray(), storedHash)
                .verified;
    }
}
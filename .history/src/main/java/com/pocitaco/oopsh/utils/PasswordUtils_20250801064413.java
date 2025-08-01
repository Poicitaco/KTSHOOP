package com.pocitaco.oopsh.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class PasswordUtils {

    private static final String ALGORITHM = "SHA-256";

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            byte[] hashedBytes = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public static boolean verifyPassword(String plainPassword, String storedPassword) {
        // First try to verify as plain text (for backward compatibility)
        if (plainPassword.equals(storedPassword)) {
            return true;
        }
        
        // Then try to verify as hashed password
        try {
            String hashedPlainPassword = hashPassword(plainPassword);
            return hashedPlainPassword.equals(storedPassword);
        } catch (Exception e) {
            return false;
        }
    }
}

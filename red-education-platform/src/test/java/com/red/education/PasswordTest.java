package com.red.education;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordTest {

    @Test
    public void testPasswords() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        System.out.println("=== Testing Password Hashes ===\n");

        // Test admin123 hash
        String adminPassword = "admin123";
        String adminHash = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi";
        boolean adminMatches = encoder.matches(adminPassword, adminHash);
        System.out.println("admin123 matches admin hash: " + adminMatches);

        // Test user123 hash - THIS IS THE ONE FROM DATABASE
        String userPassword = "user123";
        String userHash = "$2a$10$8.gKTRrCv9J3sP6XmBfaQOU7Xmz8CqKOj8DQ0Z5PqxPCqHMZYXB1K";
        boolean userMatches = encoder.matches(userPassword, userHash);
        System.out.println("user123 matches testuser hash: " + userMatches);

        System.out.println("\n=== Testing Different Passwords ===");
        // Try other common passwords
        String[] testPasswords = { "user123", "testuser", "123456", "password", "User123" };
        for (String pwd : testPasswords) {
            boolean match = encoder.matches(pwd, userHash);
            System.out.println("Password '" + pwd + "' matches: " + match);
        }

        System.out.println("\n=== Generate New Hashes ===");
        System.out.println("New hash for 'admin123': " + encoder.encode("admin123"));
        System.out.println("New hash for 'user123': " + encoder.encode("user123"));
    }
}

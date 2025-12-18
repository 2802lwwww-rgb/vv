package com.red.education;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class PasswordHashTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testPasswordHashes() {
        System.out.println("=== Testing with Spring's PasswordEncoder Bean ===\n");

        // Test admin123 hash
        String adminPassword = "admin123";
        String adminHash = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi";
        boolean adminMatches = passwordEncoder.matches(adminPassword, adminHash);
        System.out.println("admin123 matches admin hash: " + adminMatches);

        // Test user123 hash - FROM DATABASE
        String userPassword = "user123";
        String userHash = "$2a$10$8.gKTRrCv9J3sP6XmBfaQOU7Xmz8CqKOj8DQ0Z5PqxPCqHMZYXB1K";
        boolean userMatches = passwordEncoder.matches(userPassword, userHash);
        System.out.println("user123 matches testuser hash: " + userMatches);

        System.out.println("\n=== Testing Different Passwords ===");
        // Try other common passwords
        String[] testPasswords = { "user123", "testuser", "123456", "password", "User123", "admin123" };
        for (String pwd : testPasswords) {
            boolean match = passwordEncoder.matches(pwd, userHash);
            System.out.println("Password '" + pwd + "' matches testuser hash: " + match);
        }

        System.out.println("\n=== PasswordEncoder Class ===");
        System.out.println("Using: " + passwordEncoder.getClass().getName());

        System.out.println("\n=== Generate Fresh Hashes ===");
        String newAdminHash = passwordEncoder.encode("admin123");
        String newUserHash = passwordEncoder.encode("user123");
        System.out.println("New admin123 hash: " + newAdminHash);
        System.out.println("New user123 hash: " + newUserHash);

        // Verify the new hashes work
        System.out.println("\nVerify new hashes:");
        System.out.println("admin123 matches new hash: " + passwordEncoder.matches("admin123", newAdminHash));
        System.out.println("user123 matches new hash: " + passwordEncoder.matches("user123", newUserHash));
    }
}

package com.inn.cafe.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class PasswordUtils {
    @Autowired
    private PasswordEncoder passwordEncoder;

    public String encryptPassword(String normalPassword) {
        return passwordEncoder.encode(normalPassword);
    }

    /**
     * @param oldOrNewPassword
     * @param currentPassword
     * @return boolean, it's depend on the verification
     * @apiNote Verify if the old password or new password is the same of the current password
     */
    public boolean verifyPasswordAuthenticity(String oldOrNewPassword, String currentPassword) {
        return passwordEncoder.matches(oldOrNewPassword, currentPassword);
    }

    public String generateRandomPassword(Integer passwordLength) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%^&*()";
        SecureRandom random = new SecureRandom();
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < passwordLength ; i++) {
            int index = random.nextInt(passwordLength);
            builder.append(characters.charAt(index));
        }
        return builder.toString();
    }
}

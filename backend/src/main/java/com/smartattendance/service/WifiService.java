package com.smartattendance.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.Map;

@Service
public class WifiService {
    public Map<String, String> startForCourse(Long courseId) {
        // Placeholder: integrate with your network controller here.
        String ssid = "Classroom_" + courseId + "_WiFi";
        String password = generatePassword();
        return Map.of("ssid", ssid, "password", password);
    }

    private String generatePassword() {
        byte[] b = new byte[6]; new SecureRandom().nextBytes(b);
        return HexFormat.of().formatHex(b);
    }
}



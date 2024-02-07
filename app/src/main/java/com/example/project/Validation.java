package com.example.project;

import android.util.Log;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import android.util.Base64;

public class Validation {

    protected static boolean isValidName(String name) {
        return name != null && name.length() >= 3;
    }

    protected static boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    protected static boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{5,}$";
        return password.matches(passwordPattern);
    }


    protected static String getCountryIsoCode(String dialCode) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        for (String region : phoneUtil.getSupportedRegions()) {
            int code = phoneUtil.getCountryCodeForRegion(region);
            Log.d("TAG","+"+code);
            if (dialCode.equals("+" + code)) {
                Log.d("TAG","+"+region);
                return region;
            }
        }
        return null;
    }

    protected static boolean isValidPhoneNumber(String dialCode, String phoneNumber) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            String countryCode = getCountryIsoCode(dialCode);
            if (countryCode == null) {
                Log.e("PhoneValidation", "Invalid dial code: " + dialCode);
                return false;
            }
            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(phoneNumber, countryCode);
            return phoneUtil.isValidNumber(numberProto);
        } catch (NumberParseException e) {
            Log.e("NumberParseException", "Error: " + e.toString());
            return false;
        }
    }
    protected static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Generate a session token with the user's email
    protected static String generateSessionToken(String email) {
        // Create a random UUID (Universally Unique Identifier)
        UUID uuid = UUID.randomUUID();

        // Convert the UUID to a byte array
        byte[] uuidBytes = asBytes(uuid);

        // Encode the byte array to Base64
        String base64Uuid = Base64.encodeToString(uuidBytes, Base64.NO_WRAP);

        // Combine the email and Base64-encoded UUID to create the session token
        String sessionToken = email + "|" + base64Uuid;

        return sessionToken;
    }

    // Convert UUID to byte array
    protected static byte[] asBytes(UUID uuid) {
        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();
        byte[] buffer = new byte[16];

        for (int i = 0; i < 8; i++) {
            buffer[i] = (byte) (msb >>> 8 * (7 - i));
        }

        for (int i = 8; i < 16; i++) {
            buffer[i] = (byte) (lsb >>> 8 * (7 - i));
        }

        return buffer;
    }



}

package ca.mcgill.ecse321.treeple.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// Hashes passwords before sending them to the backend.
public class PasswordHash {

    public static String generatePasswordHash(String password) throws NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(password.getBytes());

        byte data[] = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }
}

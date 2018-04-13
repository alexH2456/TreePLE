package ca.mcgill.ecse321.treeple.utils;

import org.mindrot.jbcrypt.BCrypt;

// Hashes and salts passwords before sending them to the backend. Validates passwords entered at login.
public class PasswordHash {

    public static String generatePasswordHash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean validatePassword(String enteredPassword, String storedPassword) {
        return BCrypt.checkpw(enteredPassword, storedPassword);
    }
}

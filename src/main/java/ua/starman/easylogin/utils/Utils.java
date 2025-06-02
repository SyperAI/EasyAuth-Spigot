package ua.starman.easylogin.utils;

import org.bukkit.ChatColor;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {

    public static void checkDirs(File path) {
        if (!path.exists()) {
            if (path.mkdirs()) {
            }
        }
    }

    public static String parseMessage(String text) {
        return Vars.pluginTab + ChatColor.RESET + " " + text;
    }

    public static String encodeToSHA256(String originalString) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(originalString.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}

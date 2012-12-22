package org.lolownia.dev.lapach;

public class LapachUtil {
    public static String getValidFileName(String fileName) {
        String newFileName =  fileName.replaceAll("[:\\\\/*?|<>]", "_");
        if (newFileName.length() == 0) {
            return null;
        }
        return newFileName;
    }
}

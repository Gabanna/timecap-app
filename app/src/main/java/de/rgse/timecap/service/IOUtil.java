package de.rgse.timecap.service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class IOUtil {

    private IOUtil() {
    }

    public static synchronized String readInputStream(InputStream inputStream) throws IOException {
        BufferedInputStream reader = new BufferedInputStream(inputStream);

        byte[] contentBytes = new byte[1024];
        int bytesRead;
        String result = "";

        while ((bytesRead = reader.read(contentBytes)) != -1) {
            result += new String(contentBytes, 0, bytesRead);
        }

        return result;
    }

    public static boolean stringHasContent(String toCheck) {
        return null != toCheck && !toCheck.trim().isEmpty();
    }
}

package de.rgse.timecap.service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class IOUtil {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

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

    public static String formatDate(Date date) {
        return null == date ? null : DATE_FORMAT.format(date);
    }

    public static Date formatDate(String date) throws ParseException {
        return null == date ? null : DATE_FORMAT.parse(date);
    }
}

package org.library.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class LogManager {
    private static final String LOGFILE = "library_log.txt";

    public static void log(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOGFILE, true))) {
            writer.write(message);
            writer.newLine();
            System.out.println("Logged");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

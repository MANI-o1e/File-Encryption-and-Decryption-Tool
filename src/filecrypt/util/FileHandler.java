package filecrypt.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/**
 * Handles all file read and write operations, validating file paths,
 * checking file attributes, and managing IO exceptions.
 */
public class FileHandler {

    /**
     * Reads the entire contents of a text file.
     *
     * @param file the File object to read
     * @return the string content of the file
     * @throws FileNotFoundException if the file does not exist
     * @throws IOException if an I/O error occurs during reading
     */
    public static String readFile(File file) throws FileNotFoundException, IOException {
        if (file == null) {
            throw new IllegalArgumentException("File reference cannot be null.");
        }
        if (!file.exists()) {
            throw new FileNotFoundException("Target file does not exist: " + file.getAbsolutePath());
        }
        if (file.isDirectory()) {
            throw new IOException("Cannot read a directory as a text file: " + file.getAbsolutePath());
        }

        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            char[] buffer = new char[4096];
            int charsRead;
            while ((charsRead = reader.read(buffer, 0, buffer.length)) != -1) {
                builder.append(buffer, 0, charsRead);
            }
        }
        return builder.toString();
    }

    /**
     * Writes content to a text file.
     *
     * @param file the destination File
     * @param content the text content to write
     * @throws IOException if writing fails
     */
    public static void writeFile(File file, String content) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("Destination file cannot be null.");
        }
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            if (content != null) {
                writer.write(content);
            }
        }
    }

    /**
     * Formats file size in readable units (Bytes, KB, MB).
     */
    public static String formatFileSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else {
            return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        }
    }

    /**
     * Retrieves the file extension using String methods.
     */
    public static String getFileExtension(File file) {
        if (file == null) {
            return "";
        }
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1).toLowerCase();
        }
        return "";
    }
}

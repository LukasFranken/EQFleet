package de.instinct.eqlibgdxutils;

import java.io.*;
import java.nio.charset.StandardCharsets;

import de.instinct.eqlibgdxutils.debug.logging.ConsoleColor;
import de.instinct.eqlibgdxutils.debug.logging.Logger;

public class FileManager {
	
	private static final String LOGTAG = "FileManager";

	private static File resolve(String fileName) {
        return new File(fileName).getAbsoluteFile();
    }

    public static String loadFile(String fileName) {
        try {
            File filePath = resolve(fileName);
            if (!filePath.exists()) {
                Logger.log(LOGTAG, "File not found: " + filePath, ConsoleColor.RED);
                return null;
            }
            try (InputStream input = new FileInputStream(filePath);
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[8192];
                int count;
                while ((count = input.read(buffer)) != -1) bytes.write(buffer, 0, count);
                return new String(bytes.toByteArray(), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static void saveFile(String fileName, String content) {
        try {
            File filePath = resolve(fileName);
            File parent = filePath.getParentFile();
            if (!parent.isDirectory() && !parent.mkdirs() && !parent.isDirectory())
                throw new IOException("Cannot create directory: " + parent);
            try (OutputStream output = new FileOutputStream(filePath)) {
                output.write(content.getBytes(StandardCharsets.UTF_8));
            }
            Logger.log(LOGTAG, "File saved: " + filePath, ConsoleColor.YELLOW);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
}

package de.instinct.eqlibgdxutils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import de.instinct.eqlibgdxutils.debug.logging.ConsoleColor;
import de.instinct.eqlibgdxutils.debug.logging.Logger;

public class FileManager {
	
	private static final String LOGTAG = "FileManager";

	private static Path getAppDirectoryPath() {
        return Paths.get("").toAbsolutePath();
    }
    
    public static String loadFile(String fileName) {
        try {
            Path filePath = getAppDirectoryPath().resolve(fileName);
            if (!Files.exists(filePath)) {
                Logger.log(LOGTAG, "File not found: " + filePath, ConsoleColor.RED);
                return null;
            }
            return new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static void saveFile(String fileName, String content) {
        try {
            Path filePath = getAppDirectoryPath().resolve(fileName);
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, content.getBytes(StandardCharsets.UTF_8), 
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            Logger.log(LOGTAG, "File saved: " + filePath, ConsoleColor.YELLOW);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
}

package de.instinct.eqlibgdxutils;

import java.util.UUID;

import com.badlogic.gdx.Gdx;

public class ClipboardUtil {

    public static String getUUID() {
        try {
            UUID uuid = UUID.fromString(getContent());
            return uuid.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public static String getContent() {
        try {
            String content = Gdx.app.getClipboard().getContents();
            return (content != null) ? content.trim() : null;
        } catch (Exception e) {
            return null;
        }
    }

    public static void setContent(String text) {
        if (text != null) {
        	Gdx.app.getClipboard().setContents(text);
        }
    }
}

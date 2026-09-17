package de.instinct.eqlibgdxutils.audio;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

/** Gives native decoders a real file when desktop assets live inside a JAR. */
final class MiniAudioAssets {
    private final Map<String, File> extracted = new HashMap<>();

    String resolve(String path) {
        ApplicationType type = Gdx.app.getType();
        if (type == ApplicationType.Android || type == ApplicationType.iOS) return path;
        FileHandle asset = Gdx.files.internal(path);
        if (asset.file().isFile()) return asset.file().getAbsolutePath();
        File file = extracted.get(path);
        if (file == null) {
            try {
                file = File.createTempFile("eq-audio-", "." + asset.extension());
                file.deleteOnExit();
                asset.copyTo(new FileHandle(file));
                extracted.put(path, file);
            } catch (IOException e) {
                throw new GdxRuntimeException("Cannot extract audio asset: " + path, e);
            }
        }
        return file.getAbsolutePath();
    }

    void dispose() {
        for (File file : extracted.values()) file.delete();
        extracted.clear();
    }
}

package de.instinct.eqlibgdxutils.rendering.ui.texture.load;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;

import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;

public class TextureLoader {
	
	private Map<String, Texture> textures = new HashMap<>();
	
	public Texture getTexture(String packageName, String key) {
		Texture texture = textures.get(key);
    	if (texture != null) {
    		return texture;
    	} else {
    		texture = new Texture(packageName + "/" + key.toLowerCase().replaceAll(" ", "_") + ".png");
    		textures.put(key, texture);
    		return texture;
    	}
    }
	
	@Deprecated
	public Texture getTexture(String key) {
		Texture texture = textures.get(key);
    	if (texture != null) {
    		return texture;
    	} else {
    		FileHandle file = null;
    		String fileName = key.toLowerCase().replaceAll(" ", "_") + ".png";
    		if (TextureManager.class.getResource("") != null && TextureManager.class.getResource("").toString().startsWith("jar:")) {
    			file = searchInFolder(Gdx.files.internal("assets"), fileName);
    		} else {
    			file = searchInFolder(Gdx.files.external("EQIdleWorkspace/EQIdleGame/EquilibriumIdle/assets"), fileName);
    		}
            
    		texture = new Texture(file);
    		textures.put(key, texture);
    		return texture;
    	}
	}
	
	private FileHandle searchInFolder(FileHandle folder, String fileName) {
        if (!folder.isDirectory()) return null;

        FileHandle[] files = folder.list();
        for (FileHandle file : files) {
            if (file.isDirectory()) {
                FileHandle result = searchInFolder(file, fileName);
                if (result != null) return result;
            } else if (file.name().equals(fileName)) {
                return file;
            }
        }

        return null;
    }

}

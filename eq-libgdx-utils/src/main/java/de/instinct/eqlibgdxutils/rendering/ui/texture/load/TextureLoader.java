package de.instinct.eqlibgdxutils.rendering.ui.texture.load;

import com.badlogic.gdx.graphics.Texture;

public class TextureLoader {
	
	public Texture getTexture(String packageName, String key) {
		return new Texture(packageName + "/" + key.toLowerCase().replaceAll(" ", "_") + ".png");
    }
	
	
	/*public Texture getTexture(String key) {
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
    }*/

}

package de.instinct.eqlibgdxutils.engine.cursor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;

public class CursorUtil {
	
	public static void setCursor(String imageName, Hotspot hotspot) {
		Pixmap pixmap = new Pixmap(Gdx.files.internal(imageName + ".png"));
		Vector2 hotspotCoords = null;
		switch (hotspot) {
			case TOPLEFT:
				hotspotCoords = new Vector2(0, 0);
				break;
			case CENTER:
				hotspotCoords = new Vector2(pixmap.getWidth() / 2f, pixmap.getHeight() / 2f);
				break;
		}
		Cursor cursor = Gdx.graphics.newCursor(pixmap, (int)hotspotCoords.x, (int)hotspotCoords.y);
		Gdx.graphics.setCursor(cursor);
		pixmap.dispose();
	}

}

package de.instinct.eqlibgdxutils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.math.Vector2;

import de.instinct.eqlibgdxutils.debug.console.Console;
import de.instinct.eqlibgdxutils.platform.cursor.CursorUtil;
import de.instinct.eqlibgdxutils.platform.cursor.Hotspot;
import de.instinct.eqlibgdxutils.platform.preference.Preferences;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontUtil;
import de.instinct.eqlibgdxutils.rendering.ui.popup.PopupRenderer;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.Shapes;

public class LibraryManager {
	
	public static void initialize(Vector2 windowSize, String appName) {
		Console.init();
		Preferences.init(appName.toLowerCase());
		GraphicsUtil.init(windowSize);
		Gdx.input.setInputProcessor(new InputMultiplexer());
		PopupRenderer.init();
		FontUtil.init();
		Shapes.init();
    	SkinManager.init();
    	CursorUtil.setCursor("cursor", Hotspot.TOPLEFT);
        Console.build();
        AccelerometerUtil.init();
	}
	
	public static void update() {
		InputUtil.update();
		AccelerometerUtil.update();
	}
	
	public static void dispose() {
        Console.dispose();
        Shapes.dispose();
	}

}

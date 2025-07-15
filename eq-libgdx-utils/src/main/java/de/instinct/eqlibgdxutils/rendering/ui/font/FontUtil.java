package de.instinct.eqlibgdxutils.rendering.ui.font;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class FontUtil {
	
	private static Map<FontType, BitmapFont> fonts;
	private static SpriteBatch batch;

	public static void init() {
		init(FontConfiguration.builder().build());
	}
	
	public static void init(FontConfiguration newConfiguration) {
		fonts = new HashMap<>();
		loadFont(newConfiguration);
		batch = new SpriteBatch();
	}
	
	public static void loadFont(FontConfiguration configuration) {
        float scaleFactor = Gdx.graphics.getHeight() / 1080f;
        
        FreeTypeFontGenerator normalGenerator = new FreeTypeFontGenerator(Gdx.files.internal("ui/font/" + configuration.getName() + ".ttf"));
        FreeTypeFontGenerator boldGenerator = new FreeTypeFontGenerator(Gdx.files.internal("ui/font/" + configuration.getName() + "_bold.ttf"));
        
        FreeTypeFontParameter largeParams = new FreeTypeFontParameter();
        largeParams.size = Math.round(configuration.getLargeSize() * scaleFactor);
        fonts.put(FontType.LARGE, normalGenerator.generateFont(largeParams));
        
        FreeTypeFontParameter normalParams = new FreeTypeFontParameter();
        normalParams.size = Math.round(configuration.getNormalSize() * scaleFactor);
        fonts.put(FontType.NORMAL, normalGenerator.generateFont(normalParams));
        
        FreeTypeFontParameter boldParams = new FreeTypeFontParameter();
        boldParams.size = Math.round(configuration.getBoldSize() * scaleFactor);
        fonts.put(FontType.BOLD, boldGenerator.generateFont(boldParams));
        
        FreeTypeFontParameter smallParams = new FreeTypeFontParameter();
        smallParams.size = Math.round(configuration.getSmallSize() * scaleFactor);
        fonts.put(FontType.SMALL, boldGenerator.generateFont(smallParams));
        
        FreeTypeFontParameter tinyParams = new FreeTypeFontParameter();
        tinyParams.size = Math.round(configuration.getTinySize() * scaleFactor);
        fonts.put(FontType.TINY, boldGenerator.generateFont(tinyParams));
        
        normalGenerator.dispose();
	}

	public static void draw(Color color, String text, float x, float y, FontType type) {
		batch.begin();
		fonts.get(type).setColor(color);
		fonts.get(type).draw(batch, text, x, y);
		batch.end();
	}
	
	public static float getFontHeightPx() {
		return getFontHeightPx(FontType.NORMAL);
	}
	
	public static float getFontHeightPx(FontType type) {
		return fonts.get(type).getCapHeight();
	}

	public static float getFontTextWidthPx(int length) {
		return getFontTextWidthPx(length, FontType.NORMAL);
	}
	
	public static float getFontTextWidthPx(String text) {
		return getFontTextWidthPx(text, FontType.NORMAL);
	}

	public static float getFontTextWidthPx(int length, FontType type) {
		return fonts.get(type).getSpaceXadvance() * length;
	}
	
	public static float getFontTextWidthPx(String text, FontType type) {
		return fonts.get(type).getSpaceXadvance() * text.length();
	}
	
}

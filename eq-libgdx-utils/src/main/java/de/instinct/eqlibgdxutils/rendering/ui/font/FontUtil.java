package de.instinct.eqlibgdxutils.rendering.ui.font;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

import de.instinct.eqlibgdxutils.GraphicsUtil;

public class FontUtil {
	
	private static Map<FontType, BitmapFont> fonts;
	private static SpriteBatch batch;

	public static void init() {
		init(FontConfiguration.builder().build());
	}
	
	public static void init(FontConfiguration newConfiguration) {
		fonts = new HashMap<>();
		loadFonts(newConfiguration);
		batch = new SpriteBatch();
	}
	
	public static void loadFonts(FontConfiguration configuration) {
        for (FontTypeConfiguration fontTypeConfiguration : configuration.getFontTypes()) {
        	FileHandle fontFile = Gdx.files.internal("ui/font/" + fontTypeConfiguration.getName() + ".ttf");
        	if (!fontFile.exists()) {
        		fontFile = Gdx.files.internal("ui/font/" + fontTypeConfiguration.getName() + ".otf");
        	}
        	FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
            FreeTypeFontParameter params = new FreeTypeFontParameter();
            params.size = Math.round(fontTypeConfiguration.getSize() * GraphicsUtil.getHorizontalDisplayScaleFactor());
            fonts.put(fontTypeConfiguration.getType(), generator.generateFont(params));
            generator.dispose();
        }
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
	
	public static float getNormalizedFontHeightPx(FontType type) {
		return fonts.get(type).getCapHeight() / GraphicsUtil.getVerticalDisplayScaleFactor();
	}
	
	public static float getFontHeightPx(FontType type) {
		return fonts.get(type).getCapHeight();
	}
	
	public static float getNormalizedFontTextWidthPx(int length, FontType type) {
		return getFontTextWidthPx(length, type) / GraphicsUtil.getHorizontalDisplayScaleFactor();
	}

	public static float getFontTextWidthPx(int length) {
		return getFontTextWidthPx(length, FontType.NORMAL);
	}
	
	public static float getNormalizedFontTextWidthPx(String text, FontType type) {
		return getFontTextWidthPx(text, type) / GraphicsUtil.getHorizontalDisplayScaleFactor();
	}
	
	public static float getFontTextWidthPx(String text) {
		return getFontTextWidthPx(text, FontType.NORMAL);
	}

	public static float getFontTextWidthPx(int length, FontType type) {
		return (fonts.get(type).getSpaceXadvance() * length);
	}
	
	public static float getFontTextWidthPx(String text, FontType type) {
		return (fonts.get(type).getSpaceXadvance() * text.length());
	}
	
}

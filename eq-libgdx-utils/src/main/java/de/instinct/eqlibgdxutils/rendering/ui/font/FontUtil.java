package de.instinct.eqlibgdxutils.rendering.ui.font;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.rendering.ui.DefaultUIValues;

public class FontUtil {

	private static float FONT_SCALE;
	
	private static Map<FontType, BitmapFont> fonts;
	private static SpriteBatch batch;

	public static void init() {
		FONT_SCALE = (Gdx.graphics.getHeight() / 1000f) * 1.1f;
		fonts = new HashMap<>();
		fonts.put(FontType.NORMAL, new BitmapFont(Gdx.files.internal("ui/font/source.fnt")));
		fonts.get(FontType.NORMAL).getData().setScale(FONT_SCALE);
		fonts.put(FontType.BOLD, new BitmapFont(Gdx.files.internal("ui/font/source_bold.fnt")));
		fonts.get(FontType.BOLD).getData().setScale(FONT_SCALE);
		fonts.put(FontType.SMALL, new BitmapFont(Gdx.files.internal("ui/font/source_small.fnt")));
		fonts.get(FontType.SMALL).getData().setScale(FONT_SCALE);
		batch = new SpriteBatch();
	}

	public static void draw(Color color, String text, float x, float y) {
		draw(color, text, x, y, FontType.NORMAL);
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

	public static float getFontTextWidthPx(int length) {
		return getFontTextWidthPx(length, FontType.NORMAL);
	}
	
	public static float getFontTextWidthPx(String text) {
		return getFontTextWidthPx(text, FontType.NORMAL);
	}

	public static float getFontHeightPx(FontType type) {
		return fonts.get(type).getCapHeight();
	}

	public static float getFontTextWidthPx(int length, FontType type) {
		return fonts.get(type).getSpaceXadvance() * length;
	}
	
	public static float getFontTextWidthPx(String text, FontType type) {
		return fonts.get(type).getSpaceXadvance() * text.length();
	}
	
	public static void drawLabel(String label, Rectangle bounds) {
		drawLabel(DefaultUIValues.skinColor, label, bounds);
	}
	
	public static void drawLabel(Color color, String label, Rectangle bounds) {
		drawLabel(color, label, bounds, 1f);
	}
	
	public static void drawLabel(String label, Rectangle bounds, float alpha) {
		drawLabel(DefaultUIValues.skinColor, label, bounds, alpha);
	}
	
	public static void drawLabel(Color color, String label, Rectangle bounds, float alpha) {
		Color labelColor = new Color(color); 
		labelColor.a = alpha;
		draw(labelColor, label, bounds.x + ((bounds.width - getFontTextWidthPx(label)) / 2), bounds.y + ((bounds.height + getFontHeightPx()) / 2));
	}
}

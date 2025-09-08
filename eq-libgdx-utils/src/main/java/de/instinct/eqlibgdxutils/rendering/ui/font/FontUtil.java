package de.instinct.eqlibgdxutils.rendering.ui.font;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.Vector2;

import de.instinct.eqlibgdxutils.GraphicsUtil;

public class FontUtil {
	
	private static Map<FontType, BitmapFont> fonts;
	private static SpriteBatch batch;
	private static GlyphLayout layout;

	public static void init() {
		init(FontConfiguration.builder().build());
	}
	
	public static void init(FontConfiguration newConfiguration) {
		fonts = new HashMap<>();
		loadFonts(newConfiguration);
		batch = new SpriteBatch();
		batch.enableBlending();
		layout = new GlyphLayout();
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
            BitmapFont bitmapFont = generator.generateFont(params);
            bitmapFont.getData().markupEnabled = true;
            fonts.put(fontTypeConfiguration.getType(), bitmapFont);
            generator.dispose();
        }
	}
	
	public static void draw(Vector2 position, FontType type) {
		batch.begin();
		fonts.get(type).draw(batch, layout, position.x, position.y);
		batch.end();
		layout.setText(fonts.get(type), "");
	}
	
	public static void setLayoutText(String line, FontType type) {
		layout.setText(fonts.get(type), line);
	}
	
	public static GlyphLayout getGlyphLayout() {
		return layout;
	}
	
	public static float getFontTextWidthPx(int length) {
		return getFontTextWidthPx(length, FontType.NORMAL);
	}

	public static float getFontTextWidthPx(int length, FontType type) {
		return (fonts.get(type).getSpaceXadvance() * length) / GraphicsUtil.getHorizontalDisplayScaleFactor();
	}

	public static float getFontHeightPx() {
		return getFontHeightPx(FontType.NORMAL);
	}

	public static float getFontHeightPx(FontType type) {
		return fonts.get(type).getCapHeight() / GraphicsUtil.getVerticalDisplayScaleFactor();
	}
	
}

package de.instinct.eqlibgdxutils.rendering.ui.font;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.Vector2;

import de.instinct.eqlibgdxutils.GraphicsUtil;

public class FontUtil {
	
	public static Map<FontType, BitmapFont> fonts;
	private static SpriteBatch batch;
	private static GlyphLayout layout;
    private static FontConfiguration configuration;
    private static int windowWidth, windowHeight, bufferWidth, bufferHeight;
    private static long resizeDetectedAt;
    private static boolean resizePending;
    private static final long RESIZE_SETTLE_NS = 150_000_000L;

	public static void init() {
		List<FontTypeConfiguration> defaultFontTypes = new ArrayList<>();
		defaultFontTypes.add(FontTypeConfiguration.builder()
				.type(FontType.GALACTIC)
				.name("source")
				.size(64)
				.build());
		defaultFontTypes.add(FontTypeConfiguration.builder()
				.type(FontType.MONSTROUS)
				.name("source")
				.size(48)
				.build());
		defaultFontTypes.add(FontTypeConfiguration.builder()
				.type(FontType.GIANT)
				.name("source")
				.size(32)
				.build());
		defaultFontTypes.add(FontTypeConfiguration.builder()
				.type(FontType.LARGE)
				.name("source")
				.size(24)
				.build());
		defaultFontTypes.add(FontTypeConfiguration.builder()
				.type(FontType.NORMAL)
				.name("source")
				.size(16)
				.build());
		defaultFontTypes.add(FontTypeConfiguration.builder()
				.type(FontType.BOLD)
				.name("source")
				.size(16)
				.build());
		defaultFontTypes.add(FontTypeConfiguration.builder()
				.type(FontType.MEDIUM)
				.name("source")
				.size(12)
				.build());
		defaultFontTypes.add(FontTypeConfiguration.builder()
				.type(FontType.MEDIUM_BOLD)
				.name("source")
				.size(12)
				.build());
		defaultFontTypes.add(FontTypeConfiguration.builder()
				.type(FontType.SMALL)
				.name("source")
				.size(10)
				.build());
		defaultFontTypes.add(FontTypeConfiguration.builder()
				.type(FontType.SMALL_BOLD)
				.name("source")
				.size(10)
				.build());
		defaultFontTypes.add(FontTypeConfiguration.builder()
				.type(FontType.TINY)
				.name("source")
				.size(8)
				.build());
		defaultFontTypes.add(FontTypeConfiguration.builder()
				.type(FontType.MICRO_BOLD)
				.name("source")
				.size(7)
				.build());
		defaultFontTypes.add(FontTypeConfiguration.builder()
				.type(FontType.MICRO)
				.name("source")
				.size(7)
				.build());
		
		init(FontConfiguration.builder()
				.fontTypes(defaultFontTypes)
				.build());
	}
	
    public static void init(FontConfiguration newConfiguration) {
        dispose();
        configuration = newConfiguration;
        fonts = new HashMap<>();
        batch = new SpriteBatch();
        batch.enableBlending();
        layout = new GlyphLayout();
        rememberDimensions();
        loadFonts(configuration);
        updateProjection();
    }

    /** Rebuild once after resizing settles, including changes in framebuffer density. */
    public static void update() {
        if (batch == null || Gdx.graphics.getWidth() <= 0 || Gdx.graphics.getHeight() <= 0) return;
        if (windowWidth != Gdx.graphics.getWidth() || windowHeight != Gdx.graphics.getHeight()
                || bufferWidth != Gdx.graphics.getBackBufferWidth() || bufferHeight != Gdx.graphics.getBackBufferHeight()) {
            rememberDimensions();
            updateProjection();
            resizeDetectedAt = System.nanoTime();
            resizePending = true;
        }
        if (resizePending && System.nanoTime() - resizeDetectedAt >= RESIZE_SETTLE_NS) {
            loadFonts(configuration);
            resizePending = false;
        }
    }

    private static void rememberDimensions() {
        windowWidth = Gdx.graphics.getWidth();
        windowHeight = Gdx.graphics.getHeight();
        bufferWidth = Gdx.graphics.getBackBufferWidth();
        bufferHeight = Gdx.graphics.getBackBufferHeight();
    }

    private static void updateProjection() {
        batch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Math.max(1, windowWidth), Math.max(1, windowHeight)));
    }

    public static void loadFonts(FontConfiguration newConfiguration) {
        Map<FontType, BitmapFont> replacement = new HashMap<>();
        try {
            for (FontTypeConfiguration type : newConfiguration.getFontTypes()) {
                replacement.put(type.getType(), createFont(type));
            }
        } catch (RuntimeException failure) {
            for (BitmapFont font : replacement.values()) font.dispose();
            throw failure;
        }
        if (fonts != null) for (BitmapFont font : fonts.values()) font.dispose();
        fonts = replacement;
        configuration = newConfiguration;
    }

    private static BitmapFont createFont(FontTypeConfiguration type) {
        FileHandle file = resolveFont(type.getName());
        if (type.getType().name().contains("BOLD")) {
            FileHandle bold = findFont(type.getName() + "_bold");
            if (bold != null) file = bold;
        }
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(file);
        try {
            float displaySize = Math.max(1f, type.getSize() * GraphicsUtil.getScaleFactor());
            float density = Math.max((float) Gdx.graphics.getBackBufferWidth() / Math.max(1, Gdx.graphics.getWidth()),
                    (float) Gdx.graphics.getBackBufferHeight() / Math.max(1, Gdx.graphics.getHeight()));
            FreeTypeFontParameter params = new FreeTypeFontParameter();
            // Rasterize for framebuffer pixels; layout remains in the window coordinate system.
            params.size = Math.max(1, Math.round(displaySize * Math.max(1f, density)));
            params.minFilter = Texture.TextureFilter.Linear;
            params.magFilter = Texture.TextureFilter.Linear;
            params.genMipMaps = false;
            params.hinting = FreeTypeFontGenerator.Hinting.Slight;
            BitmapFont font = generator.generateFont(params);
            font.getData().setScale(displaySize / params.size);
            font.getData().markupEnabled = true;
            font.setUseIntegerPositions(false);
            return font;
        } finally {
            generator.dispose();
        }
    }

    private static FileHandle resolveFont(String name) {
        FileHandle file = findFont(name);
        if (file == null) throw new IllegalStateException("Missing font: " + name);
        return file;
    }

    private static FileHandle findFont(String name) {
        for (String extension : new String[] { ".ttf", ".otf" }) {
            String path = "ui/font/" + name + extension;
            FileHandle internal = Gdx.files.internal(path);
            if (internal.exists()) return internal;
            FileHandle classpath = Gdx.files.classpath(path);
            if (classpath.exists()) return classpath;
        }
        return null;
    }

    public static void dispose() {
        if (fonts != null) {
            for (BitmapFont font : fonts.values()) font.dispose();
            fonts.clear();
        }
        if (batch != null) batch.dispose();
        batch = null;
        resizePending = false;
    }

	public static void draw(Vector2 position, FontType type) {
		batch.begin();
		// Snap the baseline in framebuffer pixels, including logical-coordinate Retina windows.
        float pixelsX = (float) Math.max(1, bufferWidth) / Math.max(1, windowWidth);
        float pixelsY = (float) Math.max(1, bufferHeight) / Math.max(1, windowHeight);
        fonts.get(type).draw(batch, layout, Math.round(position.x * pixelsX) / pixelsX,
                Math.round(position.y * pixelsY) / pixelsY);
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
		return (fonts.get(type).getSpaceXadvance() * length) / GraphicsUtil.getScaleFactor();
	}

	public static float getFontHeightPx() {
		return getFontHeightPx(FontType.NORMAL);
	}

	public static float getFontHeightPx(FontType type) {
		return fonts.get(type).getCapHeight() / GraphicsUtil.getScaleFactor();
	}
	
}

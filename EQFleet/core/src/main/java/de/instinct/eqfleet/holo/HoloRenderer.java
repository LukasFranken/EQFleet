package de.instinct.eqfleet.holo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.MathUtil;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.Shapes;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQRectangle;

public class HoloRenderer {
	
	private static final float UPSCALE_FACTOR = 2f;
	private static final float K_FACTOR_ALPHA = 0.5f;
	private static final float K_FACTOR_RGB = 0.1f;
	private static Color color = new Color(0.455f, 0.804f, 0.91f, 1f);
	
	private static float elapsedTime;
	private static HoloPanelStyle style;
	
	private static EQRectangle workingRectangle;
	private static Rectangle workingBounds;
	
	private static Texture whitePixel;
	private static SpriteBatch batch = new SpriteBatch();
	private static ShaderProgram panelShader;
	
	public static void init() {
		style = new HoloPanelStyle();
		workingBounds = new Rectangle();
		
		workingRectangle = EQRectangle.builder()
				.bounds(new Rectangle())
				.color(new Color())
				.build();
		
		ShaderProgram.pedantic = false;
		panelShader = new ShaderProgram(
		        Gdx.files.internal("shader/glass/glass.vert"),
		        Gdx.files.internal("shader/glass/glass.frag")
		);

		if (!panelShader.isCompiled()) {
		    throw new IllegalStateException(panelShader.getLog());
		}
		
		Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
		pixmap.setColor(Color.WHITE);
		pixmap.fill();
		whitePixel = new Texture(pixmap);
		pixmap.dispose();
	}
	
	public static void render() {
		elapsedTime += Gdx.graphics.getDeltaTime();
	}

	public static void drawShader(Rectangle bounds, Color baseColor, boolean outerGlow) {
		float panelX = bounds.x;
		float panelY = bounds.y;
		float panelW = bounds.width;
		float panelH = bounds.height;
		float glowPad = 24f;

		float quadX = panelX - glowPad;
		float quadY = panelY - glowPad;
		float quadW = panelW + glowPad * 2f;
		float quadH = panelH + glowPad * 2f;

		float panelMinX = glowPad / quadW;
		float panelMinY = glowPad / quadH;
		float panelMaxX = (glowPad + panelW) / quadW;
		float panelMaxY = (glowPad + panelH) / quadH;

		batch.setShader(panelShader);
		batch.begin();

		panelShader.setUniformf("u_quadSize", quadW, quadH);
		panelShader.setUniformf("u_panelMin", panelMinX, panelMinY);
		panelShader.setUniformf("u_panelMax", panelMaxX, panelMaxY);

		if (outerGlow) {
			panelShader.setUniformf("u_fillColor", baseColor.r / 4, baseColor.g / 4, baseColor.b / 4, 0.1f);
			panelShader.setUniformf("u_edgeColor", baseColor.r, baseColor.g, baseColor.b, 0.5f);
		} else {
			panelShader.setUniformf("u_fillColor", baseColor.r / 4, baseColor.g / 4, baseColor.b / 4, 0.2f);
			panelShader.setUniformf("u_edgeColor", baseColor.r, baseColor.g, baseColor.b, 0.2f);
		}
		panelShader.setUniformf("u_glowColor", baseColor.r, baseColor.g, baseColor.b, 1f);

		panelShader.setUniformf("u_glowKAlpha", 0.4f);
		panelShader.setUniformf("u_glowKRgb", 0.3f);
		panelShader.setUniformf("u_borderSize", 1f * GraphicsUtil.getScaleFactor());
		
		if (outerGlow) {
			panelShader.setUniformf("u_bevelSize", 8f * GraphicsUtil.getScaleFactor());
			panelShader.setUniformf("u_glowSize", 12f * GraphicsUtil.getScaleFactor());
		} else {
			panelShader.setUniformf("u_bevelSize", 0f * GraphicsUtil.getScaleFactor());
			panelShader.setUniformf("u_glowSize", 3f * GraphicsUtil.getScaleFactor());
		}
		
		panelShader.setUniformf("u_softness", 1.0f);
		panelShader.setUniformf("u_reflectionStrength", 0.1f);

		batch.draw(whitePixel, quadX, quadY, quadW, quadH);

		batch.end();
		batch.setShader(null);
	}

	public static void drawWindow(Rectangle rectangle, Color baseColor) {
		workingBounds.set(rectangle);
		GraphicsUtil.translateToPhysical(workingBounds);
		drawShader(workingBounds, baseColor, true);
		workingBounds.set(rectangle);
		workingBounds.y = workingBounds.y + workingBounds.height - 37;
		workingBounds.height = 37;
		GraphicsUtil.translateToPhysical(workingBounds);
		drawShader(workingBounds, baseColor, false);
	}
	
	private static void drawHeader(Rectangle rectangle, Color baseColor) {
		Rectangle headerRect = new Rectangle(rectangle.x, rectangle.y + rectangle.height - 30, rectangle.width, 30);
		drawPanel(headerRect, baseColor);
	}

	public static void drawPanel(Rectangle rectangle, Color baseColor) {
		drawBaseFrame(rectangle, baseColor);
		drawGlow(rectangle, baseColor);
		drawInnerFill(rectangle, baseColor);
	}

	private static void drawBaseFrame(Rectangle rectangle, Color baseColor) {
		workingRectangle.setFilled(false);
		workingRectangle.setThickness(1f / UPSCALE_FACTOR);
		workingRectangle.setBounds(rectangle);
		workingRectangle.setColor(baseColor);
		Shapes.draw(workingRectangle);
	}

	private static void drawGlow(Rectangle rectangle, Color baseColor) {
		workingRectangle.setFilled(false);
		workingRectangle.setThickness(1f / UPSCALE_FACTOR);
		float finalSizeInner = style.glowSize * style.innerGlowSizeProportion;
		for (float index = 0; index < finalSizeInner; index++) {
			float upscaledIndex = index / UPSCALE_FACTOR;
			workingRectangle.setBounds(rectangle.x + upscaledIndex, rectangle.y + upscaledIndex, rectangle.width - (upscaledIndex * 2), rectangle.height - (upscaledIndex * 2));
			float t = index / finalSizeInner;
			setGlowColor(t, baseColor);
			Shapes.draw(workingRectangle);
		}
		
		float finalSizeOuter = style.glowSize * style.outerGlowSizeProportion;
		for (float index = 0; index < finalSizeOuter; index++) {
			float upscaledIndex = index / UPSCALE_FACTOR;
			workingRectangle.setBounds(rectangle.x - upscaledIndex, rectangle.y - upscaledIndex, rectangle.width + (upscaledIndex * 2), rectangle.height + (upscaledIndex * 2));
			float t = index / finalSizeOuter;
			setGlowColor(t, baseColor);
			Shapes.draw(workingRectangle);
		}
	}

	private static void setGlowColor(float t, Color baseColor) {
		float fadeOutFactorAlpha = 1 - (K_FACTOR_ALPHA / (t + K_FACTOR_ALPHA));
		float fadeOutFactorRGB = 1 - (K_FACTOR_RGB / (t + K_FACTOR_RGB));
		workingRectangle.setColor(MathUtil.clamp(baseColor.r - fadeOutFactorRGB, 0, 1), MathUtil.clamp(baseColor.g - fadeOutFactorRGB, 0, 1), MathUtil.clamp(baseColor.b - fadeOutFactorRGB, 0, 1), baseColor.a - fadeOutFactorAlpha);
	}
	
	private static void drawInnerFill(Rectangle rectangle, Color baseColor) {
		workingRectangle.setBounds(rectangle);
		workingRectangle.setColor(baseColor.r, baseColor.g, baseColor.b, style.innerFillAlpha);
		workingRectangle.setFilled(true);
		Shapes.draw(workingRectangle);
	}

}

package de.instinct.eqfleet.holo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.AccelerometerUtil;
import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.InputUtil;
import de.instinct.eqlibgdxutils.MathUtil;
import de.instinct.eqlibgdxutils.debug.modulator.Modulator;
import de.instinct.eqlibgdxutils.debug.modulator.modulation.types.RangeModulation;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.slider.ValueChangeAction;

public class HoloRenderer {
	
	private static Rectangle workingBounds;
	private static Color workingColor;
	
	private static Texture whitePixel;
	private static SpriteBatch batch;
	private static ShaderProgram panelShader;
	
	private static float reflectionPos = 0.82f;
	private static float reflectionSlope = 0.22f;
	private static float reflectionWidth = 0.08f;
	
	public static void init() {
		workingBounds = new Rectangle();
		workingColor = new Color();
		batch = new SpriteBatch();
		
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
		
		RangeModulation reflectionPosMod = new RangeModulation("pos", new ValueChangeAction() {
			
			@Override
			public void execute(float value) {
				reflectionPos = value;
			}
			
		}, reflectionPos);
		Modulator.add(reflectionPosMod);
		
		RangeModulation reflectionSlopeMod = new RangeModulation("slope", new ValueChangeAction() {
			
			@Override
			public void execute(float value) {
				reflectionSlope = value;
			}
			
		}, reflectionSlope);
		Modulator.add(reflectionSlopeMod);
		
		RangeModulation reflectionWidthMod = new RangeModulation("width", new ValueChangeAction() {
			
			@Override
			public void execute(float value) {
				reflectionWidth = value;
			}
			
		}, reflectionWidth);
		Modulator.add(reflectionWidthMod);
	}
	
	public static void drawPanel(HoloPanel panel) {
		workingBounds.set(panel.getBounds());
		GraphicsUtil.translateToPhysical(workingBounds);
		workingColor.set(panel.getColor());
		
		animatePanel(panel);
		batch.setShader(panelShader);
		batch.begin();
		drawPanel(panel, HoloPanelRenderMode.BODY);
		drawPanel(panel, HoloPanelRenderMode.GLOW);
		batch.end();
		batch.setShader(null);
		
		panel.setElapsed(panel.getElapsed() + Gdx.graphics.getDeltaTime());
	}

	private static void animatePanel(HoloPanel panel) {
		float reflectionRatio = 0.5f;
		if (Gdx.app.getType().equals(ApplicationType.Desktop)) {
			reflectionRatio = InputUtil.getVirtualMousePosition().x / GraphicsUtil.screenBounds().width;
        } else {
        	reflectionRatio += AccelerometerUtil.getAcceleration().y / 20f;
        }
		reflectionPos = MathUtil.linear(-0.4f, 1.5f, reflectionRatio);
	}

	private static void drawPanel(HoloPanel panel, HoloPanelRenderMode mode) {
		float scale = GraphicsUtil.getScaleFactor();
		float glowPad = panel.getStyle().getGlowConfiguration().getGlowPad() * scale;
		
		float quadX = workingBounds.x - glowPad;
		float quadY = workingBounds.y - glowPad;
		float quadW = workingBounds.width + glowPad * 2f;
		float quadH = workingBounds.height + glowPad * 2f;

		float panelMinX = glowPad / quadW;
		float panelMinY = glowPad / quadH;
		float panelMaxX = (glowPad + workingBounds.width) / quadW;
		float panelMaxY = (glowPad + workingBounds.height) / quadH;
		
		switch (mode) {
			case BODY:
				batch.setBlendFunctionSeparate(
				    GL20.GL_SRC_ALPHA,
				    GL20.GL_ONE_MINUS_SRC_ALPHA,
				    GL20.GL_ONE,
				    GL20.GL_ONE_MINUS_SRC_ALPHA
				);
				break;
			case GLOW:
				batch.setBlendFunctionSeparate(
					GL20.GL_SRC_ALPHA,
					GL20.GL_ONE,
					GL20.GL_ZERO,
					GL20.GL_ONE
				);
				break;
		}
		
		panelShader.setUniformi("u_mode", mode.getShaderUniformValue());
		panelShader.setUniformf("u_quadSize", quadW, quadH);
		panelShader.setUniformf("u_panelMin", panelMinX, panelMinY);
		panelShader.setUniformf("u_panelMax", panelMaxX, panelMaxY);

		panelShader.setUniformf("u_fillColor", workingColor.r / 4, workingColor.g / 4, workingColor.b / 4, panel.getStyle().getFillAlpha());
		panelShader.setUniformf("u_edgeColor", workingColor.r, workingColor.g, workingColor.b, 0.5f);
		panelShader.setUniformf("u_glowColor", workingColor.r, workingColor.g, workingColor.b, 1f);

		panelShader.setUniformf("u_glowKAlpha", panel.getStyle().getGlowConfiguration().getGlowKAlpha());
		panelShader.setUniformf("u_glowKRgb", panel.getStyle().getGlowConfiguration().getGlowKRgb());
		panelShader.setUniformf("u_borderSize", 1f * scale);
		
		panelShader.setUniformf("u_bevelSize", panel.getStyle().getBevelSize() * scale);
		panelShader.setUniformf("u_glowSize", panel.getStyle().getGlowConfiguration().getGlowSize() * scale);
		
		panelShader.setUniformf("u_reflectionStrength", panel.getStyle().getReflectionStrength());
		panelShader.setUniformf("u_reflectionY", reflectionPos);
		panelShader.setUniformf("u_reflectionSlope", reflectionSlope);
		panelShader.setUniformf("u_reflectionWidth", reflectionWidth);
		
		panelShader.setUniformf("u_softness", panel.getStyle().getSoftness() * scale);
		batch.draw(whitePixel, quadX, quadY, quadW, quadH);
	}
	
	public static void dispose() {
		batch.dispose();
		panelShader.dispose();
		whitePixel.dispose();
	}

}

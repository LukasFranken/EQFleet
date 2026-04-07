package de.instinct.eqlibgdxutils.debug.modulator;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.debug.metrics.MetricUtil;
import de.instinct.eqlibgdxutils.debug.modulator.modulation.Modulation;
import de.instinct.eqlibgdxutils.debug.profiler.Profiler;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.Shapes;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQRectangle;

public class Modulator {
	
	private static Rectangle bounds;
	private static Rectangle modulationBounds;
	private static List<Modulation> modulations;
	private static EQRectangle boundsShape;
	
	public static void init() {
		bounds = new Rectangle();
		modulations = new ArrayList<>();
		modulationBounds = new Rectangle();
	}
	
	public static void build() {
		boundsShape = EQRectangle.builder()
				.color(SkinManager.skinColor)
				.thickness(1f)
				.filled(false)
				.build();
	}
	
	public static void render() {
		bounds.x = 10f;
		bounds.y = GraphicsUtil.screenBounds().height - bounds.height - MetricUtil.getFixedHeight() - Profiler.getFixedHeight() - 10f;
		bounds.width =  GraphicsUtil.screenBounds().width - 20f;
		
		modulationBounds.width = bounds.width - 20f;
		modulationBounds.x = bounds.x + 10f;
		modulationBounds.height = 20f;
		
		boundsShape.setBounds(bounds);
		Shapes.draw(boundsShape);
		
		for (int i = 0; i < modulations.size(); i++) {
			modulationBounds.y = bounds.y + 10 + (i * 30f);
			modulations.get(i).render(modulationBounds);
		}
	}
	
	public static void setFixedHeight(int height) {
		bounds.height = height;
	}
	
	public static void add(Modulation modulation) {
		modulations.add(modulation);
	}
	
	public static void dispose() {
		
	}

}

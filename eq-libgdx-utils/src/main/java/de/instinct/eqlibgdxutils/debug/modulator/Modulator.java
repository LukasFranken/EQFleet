package de.instinct.eqlibgdxutils.debug.modulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.MathUtil;
import de.instinct.eqlibgdxutils.debug.metrics.MetricUtil;
import de.instinct.eqlibgdxutils.debug.modulator.modulation.Modulation;
import de.instinct.eqlibgdxutils.debug.profiler.Profiler;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.HorizontalAlignment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.Shapes;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQRectangle;

public class Modulator {
	
	private static Rectangle bounds;
	private static Rectangle contentBounds;
	private static Rectangle modulationBounds;
	private static Map<String, List<Modulation>> modulations;
	private static EQRectangle boundsShape;
	private static EQRectangle contentShape;
	
	private static ColorButton upButton;
	private static ColorButton downButton;
	private static ColorButton selectButton;
	private static Label categoryLabel;
	private static Label titleLabel;
	
	private static int hoveredIndex;
	private static String activeCategory;
	private static final Color hoverColor = new Color(0.7f, 0f, 0f, 0.2f);
	
	public static void init() {
		bounds = new Rectangle();
		modulations = new HashMap<>();
		modulationBounds = new Rectangle();
		contentBounds = new Rectangle();
		categoryLabel = new Label("");
		categoryLabel.setType(FontType.SMALL);
		categoryLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
		categoryLabel.setStartMargin(5f);
		titleLabel = new Label("MODULATOR");
		titleLabel.setType(FontType.SMALL);
	}
	
	public static void build() {
		boundsShape = EQRectangle.builder()
				.color(SkinManager.skinColor)
				.thickness(1f)
				.filled(false)
				.build();
		contentShape = EQRectangle.builder()
				.color(Color.GRAY)
				.thickness(1f)
				.filled(false)
				.build();
		
		upButton = new ColorButton("^");
		upButton.setConsoleBypass(true);
		upButton.setAction(new Action() {
			
			@Override
			public void execute() {
				hoveredIndex = (int) MathUtil.clamp(hoveredIndex - 1, 0, modulations.size() - 1);
			}
			
		});
		downButton = new ColorButton("v");
		downButton.setConsoleBypass(true);
		downButton.setAction(new Action() {
			
			@Override
			public void execute() {
				hoveredIndex = (int) MathUtil.clamp(hoveredIndex + 1, 0, modulations.size() - 1);
			}
			
		});
		selectButton = new ColorButton(">");
		selectButton.setConsoleBypass(true);
		selectButton.setAction(new Action() {
			
			@Override
			public void execute() {
				if (activeCategory == null) {
					activeCategory = (String) modulations.keySet().toArray()[hoveredIndex];
					selectButton.getLabel().setText("<");
				} else {
					activeCategory = null;
					selectButton.getLabel().setText(">");
				}
			}
			
		});
	}
	
	public static void render() {
		bounds.x = 10f;
		bounds.y = GraphicsUtil.screenBounds().height - bounds.height - MetricUtil.getFixedHeight() - Profiler.getFixedHeight() - 10f;
		bounds.width =  GraphicsUtil.screenBounds().width - 20f;
		
		contentBounds.x = bounds.x + 10f;
		contentBounds.y = bounds.y + 50f;
		contentBounds.width = bounds.width - 20f;
		contentBounds.height = bounds.height - 60f;
		
		modulationBounds.width = contentBounds.width - 20f;
		modulationBounds.x = contentBounds.x + 10f;
		modulationBounds.height = 20f;
		
		boundsShape.setBounds(bounds);
		Shapes.draw(boundsShape);
		contentShape.setBounds(contentBounds);
		Shapes.draw(contentShape);
		
		if (activeCategory != null && modulations.containsKey(activeCategory)) {
			for (int i = 0; i < modulations.get(activeCategory).size(); i++) {
				modulationBounds.y = contentBounds.y + contentBounds.height - ((i + 1) * 30f);
				modulations.get(activeCategory).get(i).render(modulationBounds);
			}
		} else {
			for (int i = 0; i < modulations.size(); i++) {
				modulationBounds.y = contentBounds.y + contentBounds.height - 10f - ((i + 1) * 20f);
				categoryLabel.setText((String) modulations.keySet().toArray()[i]);
				categoryLabel.setBounds(modulationBounds);
				if (i == hoveredIndex) {
					categoryLabel.setBackgroundColor(hoverColor);
				} else {
					categoryLabel.setBackgroundColor(Color.CLEAR);
				}
				categoryLabel.render();
			}
			upButton.setBounds(bounds.x + 10f, bounds.y + 10f, 30f, 30f);
			upButton.render();
			downButton.setBounds(bounds.x + 45f, bounds.y + 10f, 30f, 30f);
			downButton.render();
		}
		
		selectButton.setBounds(bounds.x + bounds.width - 40f, bounds.y + 10f, 30f, 30f);
		selectButton.render();
		
		titleLabel.setBounds(bounds.x, bounds.y + 10f, bounds.width, 30f);
		titleLabel.render();
	}
	
	public static void setFixedHeight(int height) {
		bounds.height = height;
	}
	
	public static void add(String category, Modulation modulation) {
		if (!modulations.containsKey(category)) {
			modulations.put(category, new ArrayList<>());
		}
		modulations.get(category).add(modulation);
	}
	
	public static void dispose() {
		
	}

}

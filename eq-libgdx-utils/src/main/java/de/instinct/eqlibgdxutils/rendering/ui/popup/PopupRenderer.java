package de.instinct.eqlibgdxutils.rendering.ui.popup;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.InputUtil;
import de.instinct.eqlibgdxutils.debug.console.Console;
import de.instinct.eqlibgdxutils.debug.metrics.NumberMetric;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.container.list.ElementList;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.Shapes;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQRectangle;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.utility.EQGlowConfig;

public class PopupRenderer {
	
	private static List<PopupModel> activePopups;
	private static PopupModel newPopupModel;
	
	private static final float TITLE_BAR_HEIGHT = 30f;
	private static final float MARGIN = 10f;
	private static final float BG_DARKENING = 0.7f;
	
	private static boolean flagForDestroy;
	private static boolean inCreationFrame;
	
	public static void init() {
		activePopups = new ArrayList<>();
		Console.registerMetric(NumberMetric.builder()
        		.decimals(0)
        		.tag("current_layer")
        		.build());
	}
	
	public static void createMessageDialog(String title, String message) {
		Label messageLabel = new Label(message);
		ElementList popupContent = new ElementList();
		popupContent.getElements().add(messageLabel);
		create(Popup.builder()
				.title(title)
				.contentContainer(popupContent)
				.closeOnClickOutside(true)
				.build());
	}
	
	public static void create(Popup newPopup) {
		createWindow(newPopup);
		inCreationFrame = true;
	}
	
	public static boolean isActive() {
		return activePopups.size() > 0;
	}
	
	public static int getCurrentLayer() {
		return activePopups.size();
	}
	
	private static void createWindow(Popup newPopup) {
		Label title = new Label(newPopup.getTitle());
		if (newPopup.getWindowColor() != null) title.setColor(newPopup.getWindowColor());
		if (newPopup.getContentContainer().calculateWidth() < title.calculateWidth()) {
			newPopup.getContentContainer().setFixedWidth(title.calculateWidth());
		}
		newPopup.getContentContainer().update();
		Rectangle popupBounds = new Rectangle(
				(GraphicsUtil.screenBounds().width / 2) - (newPopup.getContentContainer().getBounds().getWidth() / 2) - MARGIN,
				(GraphicsUtil.screenBounds().height / 2) - ((newPopup.getContentContainer().getBounds().getHeight() + TITLE_BAR_HEIGHT) / 2) - MARGIN,
				newPopup.getContentContainer().getBounds().getWidth() + (MARGIN * 2),
				newPopup.getContentContainer().getBounds().getHeight() + TITLE_BAR_HEIGHT + (MARGIN * 2));
		
		newPopupModel = PopupModel.builder()
				.bounds(popupBounds)
				.titleLabel(title)
				.popup(newPopup)
				.windowColor(newPopup.getWindowColor() == null ? SkinManager.skinColor : newPopup.getWindowColor())
				.build();
	}

	public static void render() {
		Console.updateMetric("current_layer", activePopups.size());
		destroy();
		if (newPopupModel != null) {
			activePopups.add(newPopupModel);
			newPopupModel = null;
		}
		for (PopupModel popup : activePopups) {
			Shapes.draw(EQRectangle.builder()
					.bounds(GraphicsUtil.screenBounds())
					.color(new Color(0f, 0f, 0f, BG_DARKENING))
					.build());
			
			Shapes.draw(EQRectangle.builder()
					.bounds(popup.getBounds())
					.color(Color.BLACK)
					.build());
			
			Shapes.draw(EQRectangle.builder()
					.bounds(popup.getBounds())
					.color(popup.getWindowColor())
					.glowConfig(EQGlowConfig.builder().build())
					.thickness(2f)
					.build());
			
			Shapes.draw(EQRectangle.builder()
					.bounds(new Rectangle(popup.getBounds().x, popup.getBounds().y + popup.getBounds().height - TITLE_BAR_HEIGHT, popup.getBounds().width, 2))
					.color(popup.getWindowColor())
					.glowConfig(EQGlowConfig.builder().build())
					.thickness(2f)
					.build());
			
			popup.getTitleLabel().setBounds(new Rectangle(
					popup.getBounds().x + MARGIN,
					popup.getBounds().y + popup.getBounds().height - TITLE_BAR_HEIGHT,
					popup.getBounds().width - (MARGIN * 2),
					TITLE_BAR_HEIGHT));
			popup.getTitleLabel().render();
			popup.getPopup().getContentContainer().setPosition(popup.getBounds().x + MARGIN, popup.getBounds().y + MARGIN);
			if (inCreationFrame) {
				inCreationFrame = false;
			} else {
				popup.getPopup().getContentContainer().render();
				if (popup.getPopup().isCloseOnClickOutside() && InputUtil.isClicked() && !GraphicsUtil.scaleFactorAdjusted(popup.getBounds()).contains(InputUtil.getMousePosition())) {
					close();
				}
			}
		}
	}
	
	public static void close() {
		flagForDestroy = true;
	}
	
	private static void destroy() {
		if (flagForDestroy) {
			PopupModel activePopup = activePopups.get(activePopups.size() - 1);
			activePopup.getPopup().getContentContainer().dispose();
			activePopup.getTitleLabel().dispose();
			activePopups.remove(activePopups.size() - 1);
			flagForDestroy = false;
		}
	}

}

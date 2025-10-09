package de.instinct.eqlibgdxutils.rendering.ui.popup;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.InputUtil;
import de.instinct.eqlibgdxutils.debug.console.Console;
import de.instinct.eqlibgdxutils.debug.metrics.NumberMetric;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.container.list.ElementList;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.ComplexShapeType;

public class PopupRenderer {
	
	private static List<PopupModel> activePopups;
	private static PopupModel newPopupModel;
	
	private static final float TITLE_BAR_HEIGHT = 30f;
	private static final float MARGIN = 10f;
	private static final float BG_DARKENING = 0.7f;
	
	private static final String BG_DARKENING_TAG = "popup_screenDarkening";
	private static final String POPUP_BG_TAG = "popup_bg";
	
	private static boolean flagForDestroy;
	private static boolean inCreationFrame;
	
	public static void init() {
		activePopups = new ArrayList<>();
		Console.registerMetric(NumberMetric.builder()
        		.decimals(0)
        		.tag("current_layer")
        		.build());
		TextureManager.createTexture(BG_DARKENING_TAG, Color.BLACK);
		TextureManager.createTexture(POPUP_BG_TAG, Color.BLACK);
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
				(GraphicsUtil.baseScreenBounds().width / 2) - (newPopup.getContentContainer().getBounds().getWidth() / 2) - MARGIN,
				(GraphicsUtil.baseScreenBounds().height / 2) - ((newPopup.getContentContainer().getBounds().getHeight() + TITLE_BAR_HEIGHT) / 2) - MARGIN,
				newPopup.getContentContainer().getBounds().getWidth() + (MARGIN * 2),
				newPopup.getContentContainer().getBounds().getHeight() + TITLE_BAR_HEIGHT + (MARGIN * 2));
		String currentWindowTextureTag = "popup_" + newPopup.getTitle();
		TextureManager.createShapeTexture(
				currentWindowTextureTag, 
				ComplexShapeType.ROUNDED_RECTANGLE,
				popupBounds,
				newPopup.getWindowColor() == null ? SkinManager.skinColor : newPopup.getWindowColor());
		String currentWindowTitlebarTextureTag = currentWindowTextureTag + "_titlebar";
		TextureManager.createShapeTexture(
				currentWindowTitlebarTextureTag, 
				ComplexShapeType.ROUNDED_RECTANGLE,
				new Rectangle(popupBounds.x, popupBounds.y + popupBounds.height - TITLE_BAR_HEIGHT, popupBounds.width, 2),
				newPopup.getWindowColor() == null ? SkinManager.skinColor : newPopup.getWindowColor());
		newPopupModel = PopupModel.builder()
				.bounds(popupBounds)
				.titleLabel(title)
				.popup(newPopup)
				.windowTextureTag(currentWindowTextureTag)
				.windowTitlebarTextureTag(currentWindowTitlebarTextureTag)
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
			TextureManager.draw(BG_DARKENING_TAG, new Rectangle(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), BG_DARKENING);
			TextureManager.draw(POPUP_BG_TAG, GraphicsUtil.scaleFactorAdjusted(popup.getBounds()), 1f);
			TextureManager.draw(popup.getWindowTextureTag());
			TextureManager.draw(popup.getWindowTitlebarTextureTag());
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
			TextureManager.dispose(activePopup.getWindowTextureTag());
			TextureManager.dispose(activePopup.getWindowTitlebarTextureTag());
			activePopup.getTitleLabel().dispose();
			activePopups.remove(activePopups.size() - 1);
			flagForDestroy = false;
		}
	}

}

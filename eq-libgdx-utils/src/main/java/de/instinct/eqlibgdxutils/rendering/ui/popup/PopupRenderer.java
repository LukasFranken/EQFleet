package de.instinct.eqlibgdxutils.rendering.ui.popup;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.InputUtil;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.container.list.ElementList;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.ComplexShapeType;

public class PopupRenderer {
	
	private static Popup popup;
	private static Label title;
	private static Rectangle popupBounds;
	
	private static final float TITLE_BAR_HEIGHT = 30f;
	private static final float MARGIN = 10f;
	private static final float BG_DARKENING = 0.7f;
	
	private static String currentWindowTextureTag;
	private static String currentWindowTitlebarTextureTag;
	private static final String BG_DARKENING_TAG = "popup_screenDarkening";
	private static final String POPUP_BG_TAG = "popup_bg";
	
	private static boolean flagForDestroy;
	
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
		createWindowTextures(newPopup);
		popup = newPopup;
	}
	
	public static boolean isActive() {
		return popup != null;
	}
	
	private static void createWindowTextures(Popup newPopup) {
		newPopup.getContentContainer().update();
		popupBounds = new Rectangle(
				(Gdx.graphics.getWidth() / 2) - (newPopup.getContentContainer().getBounds().getWidth() / 2) - MARGIN,
				(Gdx.graphics.getHeight() / 2) - ((newPopup.getContentContainer().getBounds().getHeight() + TITLE_BAR_HEIGHT) / 2) - MARGIN,
				newPopup.getContentContainer().getBounds().getWidth() + (MARGIN * 2),
				newPopup.getContentContainer().getBounds().getHeight() + TITLE_BAR_HEIGHT + (MARGIN * 2));
		currentWindowTextureTag = "popup_" + newPopup.getTitle();
		TextureManager.createShapeTexture(
				currentWindowTextureTag, 
				ComplexShapeType.ROUNDED_RECTANGLE,
				popupBounds,
				SkinManager.skinColor);
		currentWindowTitlebarTextureTag = currentWindowTextureTag + "_titlebar";
		TextureManager.createShapeTexture(
				currentWindowTitlebarTextureTag, 
				ComplexShapeType.ROUNDED_RECTANGLE,
				new Rectangle(popupBounds.x, popupBounds.y + popupBounds.height - TITLE_BAR_HEIGHT, popupBounds.width, 2),
				SkinManager.skinColor);
		TextureManager.createTexture(BG_DARKENING_TAG, Color.BLACK);
		TextureManager.createTexture(POPUP_BG_TAG, Color.BLACK);
		title = new Label(newPopup.getTitle());
	}

	public static void render() {
		destroy();
		if (popup != null) {
			TextureManager.draw(BG_DARKENING_TAG, new Rectangle(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), BG_DARKENING);
			TextureManager.draw(POPUP_BG_TAG, popupBounds, 1f);
			TextureManager.draw(currentWindowTextureTag);
			TextureManager.draw(currentWindowTitlebarTextureTag);
			title.setBounds(new Rectangle(
					popupBounds.x + MARGIN,
					popupBounds.y + popupBounds.height - TITLE_BAR_HEIGHT,
					popupBounds.width - (MARGIN * 2),
					TITLE_BAR_HEIGHT));
			title.render();
			popup.getContentContainer().setPosition(popupBounds.x + MARGIN, popupBounds.y + MARGIN);
			popup.getContentContainer().render();
			if (popup.isCloseOnClickOutside() && InputUtil.isClicked() && !popupBounds.contains(InputUtil.getMousePosition())) {
				close();
			}
		}
	}
	
	public static void close() {
		flagForDestroy = true;
	}
	
	private static void destroy() {
		if (flagForDestroy) {
			TextureManager.dispose(currentWindowTextureTag);
			currentWindowTextureTag = null;
			currentWindowTitlebarTextureTag = null;
			popup.getContentContainer().dispose();
			popup = null;
			title.dispose();
			title = null;
			popupBounds = null;
			flagForDestroy = false;
		}
	}

}

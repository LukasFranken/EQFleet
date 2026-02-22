package de.instinct.eqfleet.menu.main;

import java.util.LinkedHashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.api.core.modules.MenuModule;
import de.instinct.eqfleet.menu.common.architecture.BaseModuleRenderer;
import de.instinct.eqfleet.menu.common.components.DefaultButtonFactory;
import de.instinct.eqfleet.menu.main.header.MenuHeader;
import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.debug.profiler.Profiler;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.Button;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.LabeledModelButton;

public class MenuRenderer extends BaseModuleRenderer {
	
	private final float OPEN_ANIMATION_DURATION = 1.5f;
	private float elapsed;
	
	private Rectangle menuBounds;

	private MenuWindow window;
	private MenuHeader header;
	private Map<MenuModule, Button> tabButtons;

	public MenuRenderer() {
		menuBounds = new Rectangle();
		
		tabButtons = new LinkedHashMap<>();
		window = new MenuWindow();
		header = new MenuHeader();
	}
	
	@Override
	public void init() {
		window.init();
		header.init();
		resetWindowAnimation();
		//loadContent();
	}
	
	public void resetWindowAnimation() {
		MenuModel.alpha = 0f;
		elapsed = 0f;
	}
	
	private void loadContent() {
		if (tabButtons != null && tabButtons.size() != MenuModel.buttons.size()) {
			tabButtons = new LinkedHashMap<>();
			for (MenuModule module : MenuModel.buttons) {
				createModuleButton(module);
			}
		}
	}
	
	private void createModuleButton(MenuModule module) {
		LabeledModelButton menuModelButton = DefaultButtonFactory.moduleButton(module);
		tabButtons.put(module, menuModelButton);
	}
	
	@Override
	public void update() {
		float margin = 20f;
		menuBounds.set(margin, margin + 20, GraphicsUtil.screenBounds().width - (margin * 2), GraphicsUtil.screenBounds().height - 120f - 40f);
		updateWindowAnimation();
		header.getBounds().set(menuBounds.x, menuBounds.y + menuBounds.height + 10f, menuBounds.width, 50);
		window.getBounds().set(menuBounds);
		
		header.setAlpha(MenuModel.alpha);
	}

	@Override
	public void render() {
		if (menuBounds != null) {
			Profiler.startFrame("MENU_RNDR");
			header.render();
			Profiler.checkpoint("MENU_RNDR", "header");
			window.render();
			Profiler.checkpoint("MENU_RNDR", "window");
			if (MenuModel.activeModule == null) {
				renderModuleButtons();
				Profiler.checkpoint("MENU_RNDR", "mod btns");
			}
			elapsed += Gdx.graphics.getDeltaTime();
			Profiler.endFrame("MENU_RNDR");
		}
	}

	private void updateWindowAnimation() {
		if (elapsed < OPEN_ANIMATION_DURATION / 2) {
			MenuModel.openAnimationElapsed = elapsed / (OPEN_ANIMATION_DURATION / 2);
		} else {
			MenuModel.openAnimationElapsed = 1f;
			if (elapsed < OPEN_ANIMATION_DURATION) {
				MenuModel.alpha = (elapsed - (OPEN_ANIMATION_DURATION / 2)) / (OPEN_ANIMATION_DURATION / 2);
			} else {
				MenuModel.alpha = 1f;
			}
		}
	}
	
	private void renderModuleButtons() {
		float labeledModelButtonHeight = 70f;
		float imageButtonHeight = 50f;
		float buttonWidth = 50f;
		int elementsPerRow = 5;
		float margin = (((float)menuBounds.width) - (buttonWidth * elementsPerRow)) / ((float)(elementsPerRow + 1));
		
		int i = 0;
		for (Button moduleButton : tabButtons.values()) {
			int column = i % elementsPerRow;
			int row = 1 + ((int)i / elementsPerRow);
			moduleButton.setPosition(menuBounds.x + margin + ((buttonWidth + margin) * column), menuBounds.y + menuBounds.height - ((labeledModelButtonHeight + margin) * row) + (moduleButton instanceof LabeledModelButton ? 0f : labeledModelButtonHeight - imageButtonHeight));
			moduleButton.setAlpha(MenuModel.alpha);
			moduleButton.setEnabled(MenuModel.alpha > 0.5f);
			moduleButton.render();
			i++;
		}
	}

	@Override
	public void dispose() {
		window.dispose();
		header.dispose();
		for (Button button : tabButtons.values()) {
			button.dispose();
		}
	}

}

package de.instinct.eqfleet.menu.module.main;

import java.util.LinkedHashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqfleet.GlobalStaticData;
import de.instinct.eqfleet.menu.MenuTab;
import de.instinct.eqfleet.menu.common.Renderer;
import de.instinct.eqfleet.menu.module.main.tab.inventory.InventoryTab;
import de.instinct.eqfleet.menu.module.main.tab.loadout.LoadoutTab;
import de.instinct.eqfleet.menu.module.main.tab.play.PlayTab;
import de.instinct.eqfleet.menu.module.main.tab.profile.ProfileTab;
import de.instinct.eqfleet.menu.module.main.tab.settings.SettingsTab;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.DefaultUIValues;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.subtypes.PlainRectangularLoadingBar;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontUtil;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.ComplexShapeType;

public class MainMenuRenderer extends Renderer {
	
	private Map<MenuTab, ColorButton> tabButtons;
	private PlainRectangularLoadingBar expBar;
	
	@Override
	public void init() {
		tabButtons = new LinkedHashMap<>();
		createTabButton(MenuTab.PLAY);
		createTabButton(MenuTab.LOADOUT);
		createTabButton(MenuTab.SETTINGS);
		createTabButton(MenuTab.INVENTORY);
		TextureManager.createShapeTexture("main_rankOutline", ComplexShapeType.ROUNDED_RECTANGLE, new Rectangle(15, Gdx.graphics.getHeight() - 65, 36, 35), DefaultUIValues.skinColor);
		TextureManager.createShapeTexture("main_nameOutline", ComplexShapeType.ROUNDED_RECTANGLE, new Rectangle(60, Gdx.graphics.getHeight() - 55, 120, 25), DefaultUIValues.skinColor);
		TextureManager.createShapeTexture("main_expOutline", ComplexShapeType.ROUNDED_RECTANGLE, new Rectangle(60, Gdx.graphics.getHeight() - 65, 120, 7), Color.BLUE);
		
		expBar = new PlainRectangularLoadingBar();
		expBar.setBar(TextureManager.createTexture(Color.BLUE));
		expBar.setCustomDescriptor("");
		expBar.setBackground(TextureManager.createTexture(new Color(0f, 0f, 0f, 0f)));
	}
	
	private void createTabButton(MenuTab tab) {
		Border buttonBorder = new Border();
		buttonBorder.setColor(new Color(DefaultUIValues.skinColor));
		buttonBorder.setSize(2);

		ColorButton tabButton = new ColorButton(tab.toString());
		tabButton.setBorder(buttonBorder);
		tabButton.setColor(Color.BLACK);
		tabButton.setFixedHeight(50);
		tabButton.setLabelColor(new Color(DefaultUIValues.skinColor));
		tabButton.setHoverColor(new Color(DefaultUIValues.darkerSkinColor));
		tabButton.setDownColor(new Color(DefaultUIValues.lighterSkinColor));
		tabButton.setAction(new Action() {
			
			@Override
			public void execute() {
				MainMenu.changeTab(tab);
			}
			
		});
		tabButtons.put(tab, tabButton);
	}

	@Override
	public void render() {
		renderTab();
		renderHeader();
		renderLayout();
		renderTabBar();
	}
	
	private void renderTab() {
		switch (MainMenu.currentTab) {
		case PLAY:
			PlayTab.render();
			break;
		case LOADOUT:
			LoadoutTab.render();
			break;
		case SETTINGS:
			SettingsTab.render();
			break;
		case PROFILE:
			ProfileTab.render();
			break;
		case INVENTORY:
			InventoryTab.render();
			break;
		}
	}

	private void renderTabBar() {
		float buttonWidth = Gdx.graphics.getWidth() / tabButtons.size();
		int i = 0;
		for (MenuTab tab : tabButtons.keySet()) {
			ColorButton currentTabButton = tabButtons.get(tab);
			currentTabButton.setFixedWidth(buttonWidth);
			currentTabButton.setPosition(buttonWidth * i, 0);
			currentTabButton.render();
			i++;
		}
	}

	private void renderLayout() {
		TextureManager.draw("main_rankOutline");
		TextureManager.draw("main_nameOutline");
		TextureManager.draw("main_expOutline");
	}
	
	private void renderHeader() {
		if (GlobalStaticData.profile != null) {
			TextureManager.draw(TextureManager.getTexture("ui/image/rank", GlobalStaticData.profile.getRank().getFileName()), new Rectangle(20, Gdx.graphics.getHeight() - 60, 25, 25));
			FontUtil.draw(Color.LIGHT_GRAY, GlobalStaticData.profile.getUsername() == null ? "???" : GlobalStaticData.profile.getUsername(), 70, Gdx.graphics.getHeight() - 38);
			expBar.setBounds(new Rectangle(60, Gdx.graphics.getHeight() - 65, 120, 7));
			//expBar.setMaxValue(GlobalStaticData.profile.getRank().getNextRequiredExp() - GlobalStaticData.profile.getRank().getRequiredExp());
			//expBar.setCurrentValue(GlobalStaticData.profile.getCurrentExp() - - GlobalStaticData.profile.getRank().getRequiredExp());
			expBar.setCurrentValue(877);
			expBar.setMaxValue(1000);
			expBar.render();
		}
	}
	
	@Override
	public void dispose() {
		PlayTab.dispose();
		LoadoutTab.dispose();
		SettingsTab.dispose();
		ProfileTab.dispose();
	}
	
}

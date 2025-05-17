package de.instinct.eqfleet.menu.module.main;

import java.util.LinkedHashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import de.instinct.eqfleet.GlobalStaticData;
import de.instinct.eqfleet.menu.MenuTab;
import de.instinct.eqfleet.menu.common.Renderer;
import de.instinct.eqfleet.menu.module.main.tab.inventory.InventoryTab;
import de.instinct.eqfleet.menu.module.main.tab.loadout.LoadoutTab;
import de.instinct.eqfleet.menu.module.main.tab.play.PlayTab;
import de.instinct.eqfleet.menu.module.main.tab.profile.ProfileTab;
import de.instinct.eqfleet.menu.module.main.tab.settings.SettingsTab;
import de.instinct.eqfleet.menu.module.main.tab.shop.ShopTab;
import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.DefaultUIValues;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.HorizontalAlignment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.subtypes.PlainRectangularLoadingBar;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.ComplexShapeType;

public class MainMenuRenderer extends Renderer {
	
	private Map<MenuTab, ColorButton> tabButtons;
	private PlainRectangularLoadingBar expBar;
	private Label usernameLabel;
	private Label creditsLabel;
	
	@Override
	public void init() {
		tabButtons = new LinkedHashMap<>();
		createTabButton(MenuTab.PLAY);
		createTabButton(MenuTab.LOADOUT);
		createTabButton(MenuTab.SETTINGS);
		createTabButton(MenuTab.SHOP);
		TextureManager.createShapeTexture("main_rankOutline", ComplexShapeType.ROUNDED_RECTANGLE, new Rectangle(15, Gdx.graphics.getHeight() - 65, 36, 35), DefaultUIValues.skinColor);
		TextureManager.createShapeTexture("main_nameOutline", ComplexShapeType.ROUNDED_RECTANGLE, new Rectangle(60, Gdx.graphics.getHeight() - 55, 120, 25), DefaultUIValues.skinColor);
		TextureManager.createShapeTexture("main_expOutline", ComplexShapeType.ROUNDED_RECTANGLE, new Rectangle(80, Gdx.graphics.getHeight() - 65, 100, 7), Color.BLUE);
		TextureManager.createShapeTexture("main_creditsOutline", ComplexShapeType.ROUNDED_RECTANGLE, new Rectangle(Gdx.graphics.getWidth() - 110, Gdx.graphics.getHeight() - 55, 80, 20), Color.GREEN);
		
		expBar = new PlainRectangularLoadingBar();
		expBar.setBar(TextureManager.createTexture(Color.BLUE));
		expBar.setCustomDescriptor("");
		expBar.setBackground(TextureManager.createTexture(new Color(0f, 0f, 0f, 0f)));
		
		usernameLabel = new Label("???");
		usernameLabel.setColor(Color.LIGHT_GRAY);
		usernameLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
		
		creditsLabel = new Label("");
		creditsLabel.setColor(Color.GREEN);
		creditsLabel.setHorizontalAlignment(HorizontalAlignment.RIGHT);
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
		case SHOP:
			ShopTab.render();
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
	
	private void renderHeader() {
		if (GlobalStaticData.profile != null) {
			if (MainMenu.currentTab != MenuTab.PROFILE) {
				TextureManager.draw(TextureManager.getTexture("ui/image/rank", GlobalStaticData.profile.getRank().getFileName()), new Rectangle(20, Gdx.graphics.getHeight() - 60, 25, 25));
				
				if (GlobalStaticData.profile.getUsername() != null) usernameLabel.setText(GlobalStaticData.profile.getUsername());
				usernameLabel.setBounds(new Rectangle(70, Gdx.graphics.getHeight() - 55, 100, 25));
				usernameLabel.render();
				expBar.setBounds(new Rectangle(80, Gdx.graphics.getHeight() - 65, 100, 7));
				Label expLabel = new Label("EXP");
				expLabel.setColor(Color.CYAN);
				expLabel.setType(FontType.TINY);
				expLabel.setBounds(new Rectangle(60, Gdx.graphics.getHeight() - 65, 20, 7));
				expLabel.render();
				expBar.setMaxValue(GlobalStaticData.profile.getRank().getNextRequiredExp() - GlobalStaticData.profile.getRank().getRequiredExp());
				expBar.setCurrentValue(GlobalStaticData.profile.getCurrentExp() - GlobalStaticData.profile.getRank().getRequiredExp());
				expBar.render();
				
				Rectangle profileBounds = new Rectangle(0, Gdx.graphics.getHeight() - 70, 180, 70);
				Vector3 touchPoint = new Vector3(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(), 0);
		        if (profileBounds.contains(touchPoint.x, touchPoint.y)) {
		        	if (Gdx.input.justTouched()) {
		        		MainMenu.changeTab(MenuTab.PROFILE);
		        	}
		        }
		        
		        TextureManager.draw("main_rankOutline");
				TextureManager.draw("main_nameOutline");
				TextureManager.draw("main_expOutline");
				
			}
			
			if (MainMenu.currentTab != MenuTab.INVENTORY) {
				Rectangle inventoryBounds = new Rectangle(Gdx.graphics.getWidth() - 110, Gdx.graphics.getHeight() - 60, 110, 60);
				Vector3 touchPoint = new Vector3(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(), 0);
		        if (inventoryBounds.contains(touchPoint.x, touchPoint.y)) {
		        	if (Gdx.input.justTouched()) {
		        		MainMenu.changeTab(MenuTab.INVENTORY);
		        	}
		        }
		        
				creditsLabel.setBounds(new Rectangle(Gdx.graphics.getWidth() - 110, Gdx.graphics.getHeight() - 55, 75, 20));
				creditsLabel.setText(StringUtils.formatBigNumber(GlobalStaticData.profile.getResources().getCredits()));
		        creditsLabel.render();
		        TextureManager.draw(TextureManager.getTexture("ui/image", "credits"), new Rectangle(Gdx.graphics.getWidth() - 30, Gdx.graphics.getHeight() - 53, 16, 16));
				TextureManager.draw("main_creditsOutline");
			}
		}
	}
	
	@Override
	public void dispose() {
		PlayTab.dispose();
		LoadoutTab.dispose();
		SettingsTab.dispose();
		ProfileTab.dispose();
		InventoryTab.dispose();
		ShopTab.dispose();
	}
	
}

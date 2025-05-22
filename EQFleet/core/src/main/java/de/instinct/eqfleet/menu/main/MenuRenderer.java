package de.instinct.eqfleet.menu.main;

import java.util.LinkedHashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import de.instinct.api.core.modules.MenuModule;
import de.instinct.eqfleet.menu.common.architecture.BaseModuleRenderer;
import de.instinct.eqfleet.menu.common.components.DefaultButtonFactory;
import de.instinct.eqfleet.menu.module.inventory.InventoryModel;
import de.instinct.eqfleet.menu.module.profile.ProfileModel;
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

public class MenuRenderer extends BaseModuleRenderer {
	
	private Map<MenuModule, ColorButton> tabButtons;
	private PlainRectangularLoadingBar expBar;
	private Label usernameLabel;
	private Label creditsLabel;
	
	private final float OPEN_ANIMATION_DURATION = 3f;
	private float elapsed;
	private Label menuBackground;
	private Label title;
	private float titleHeight = 40f;
	
	private float alpha;
	
	private Rectangle menuBounds;
	
	private ColorButton closeModuleButton;

	public MenuRenderer() {
		tabButtons = new LinkedHashMap<>();
		
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
		
		menuBackground = new Label("");
		Border backgroundBorder = new Border();
		backgroundBorder.setColor(DefaultUIValues.skinColor);
		backgroundBorder.setSize(2f);
		menuBackground.setBorder(backgroundBorder);
		
		title = new Label("");
		title.setHorizontalAlignment(HorizontalAlignment.LEFT);
		
		createCloseModuleButton();
	}
	
	private void createCloseModuleButton() {
		closeModuleButton = DefaultButtonFactory.colorButton("x", new Action() {
			
			@Override
			public void execute() {
				Menu.closeModule();
			}
			
		});
		closeModuleButton.setBorder(null);
		closeModuleButton.setColor(Color.CLEAR);
		Color downColor = new Color(DefaultUIValues.skinColor);
		downColor.a = 0.4f;
		closeModuleButton.setDownColor(downColor);
		closeModuleButton.setHoverColor(downColor);
	}

	public void reload() {
		alpha = 0f;
		calculateMenuBounds();
		TextureManager.createShapeTexture("main_menuOutline", ComplexShapeType.ROUNDED_RECTANGLE, menuBounds, DefaultUIValues.skinColor);
		TextureManager.createShapeTexture("main_titleOutline", ComplexShapeType.ROUNDED_RECTANGLE, new Rectangle(menuBounds.x, menuBounds.y + menuBounds.height - titleHeight, menuBounds.width, 2), DefaultUIValues.skinColor);
		TextureManager.createShapeTexture("main_rankOutline", ComplexShapeType.ROUNDED_RECTANGLE, new Rectangle(menuBounds.x, menuBounds.y + menuBounds.height + 10, 35, 35), DefaultUIValues.skinColor);
		TextureManager.createShapeTexture("main_nameOutline", ComplexShapeType.ROUNDED_RECTANGLE, new Rectangle(menuBounds.x + 45, menuBounds.y + menuBounds.height + 20, 120, 25), DefaultUIValues.skinColor);
		TextureManager.createShapeTexture("main_expOutline", ComplexShapeType.ROUNDED_RECTANGLE, new Rectangle(menuBounds.x + 65, menuBounds.y + menuBounds.height + 10, 100, 7), Color.BLUE);
		TextureManager.createShapeTexture("main_creditsOutline", ComplexShapeType.ROUNDED_RECTANGLE, new Rectangle(menuBounds.x + menuBounds.width - 103, menuBounds.y + menuBounds.height + 10, 85, 20), Color.GREEN);
		elapsed = 0f;
		tabButtons = new LinkedHashMap<>();
		for (MenuModule module : MenuModel.modules.getEnabledModules()) {
			createModuleButton(module);
		}
	}
	
	public void close() {
		alpha = 0f;
		menuBounds = null;
	}
	
	private void calculateMenuBounds() {
		float margin = 20f;
		menuBounds = new Rectangle(margin, margin, Gdx.graphics.getWidth() - (margin * 2), Gdx.graphics.getHeight() - 130);
		MenuModel.moduleBounds = new Rectangle(margin, margin, Gdx.graphics.getWidth() - (margin * 2), Gdx.graphics.getHeight() - 130 - titleHeight);
	}

	private void createModuleButton(MenuModule module) {
		ColorButton tabButton = DefaultButtonFactory.moduleButton(module.getLabel(), module);
		tabButtons.put(module, tabButton);
	}

	@Override
	public void render() {
		if (menuBounds != null) {
			renderIntro();
			renderHeader();
			TextureManager.draw("main_menuOutline", alpha);
			if (MenuModel.activeModule == null) {
				renderModuleButtons();
			} else {
				renderTitle();
			}
			elapsed += Gdx.graphics.getDeltaTime();
		}
	}

	private void renderTitle() {
		Rectangle titleBounds = new Rectangle(MenuModel.moduleBounds.x, MenuModel.moduleBounds.y + MenuModel.moduleBounds.height, MenuModel.moduleBounds.width, titleHeight);
		title.setText(MenuModel.activeModule.getLabel());
		title.setBounds(new Rectangle(titleBounds.x + 20f, titleBounds.y, titleBounds.width - 40f, titleBounds.height));
		title.render();
		closeModuleButton.setFixedHeight(titleHeight);
		closeModuleButton.setFixedWidth(titleHeight);
		closeModuleButton.setPosition(titleBounds.x + titleBounds.width - titleHeight, titleBounds.y);
		closeModuleButton.render();
		TextureManager.draw("main_titleOutline", alpha);
	}

	private void renderIntro() {
		if (elapsed < OPEN_ANIMATION_DURATION / 2) {
			float firstStageDuration = OPEN_ANIMATION_DURATION / 4;
			float secondStageDuration = OPEN_ANIMATION_DURATION / 4;
			if (elapsed < OPEN_ANIMATION_DURATION / 4) {
				float ratio = elapsed / firstStageDuration;
				menuBackground.setBounds(new Rectangle(menuBounds.x + (menuBounds.width / 2) - ((menuBounds.width / 2) * ratio), menuBounds.y + menuBounds.height / 2, menuBounds.width * ratio, 2));
			} else {
				float ratio = (elapsed - firstStageDuration) / secondStageDuration;
				menuBackground.setBounds(new Rectangle(menuBounds.x, menuBounds.y + (menuBounds.height / 2) - ((menuBounds.height / 2) * ratio), menuBounds.width, menuBounds.height * ratio));
			}
		} else {
			menuBackground.setBounds(menuBounds);
			if (elapsed < OPEN_ANIMATION_DURATION) {
				alpha = (elapsed - (OPEN_ANIMATION_DURATION / 2)) / (OPEN_ANIMATION_DURATION / 2);
			} else {
				alpha = 1f;
			}
		}
		menuBackground.render();
	}

	private void renderHeader() {
		if (ProfileModel.profile != null) {
			if (MenuModel.activeModule != MenuModule.PROFILE) {
				TextureManager.draw(TextureManager.getTexture("ui/image/rank", ProfileModel.profile.getRank().getFileName()), new Rectangle(menuBounds.x + 5, menuBounds.y + menuBounds.height + 15, 25, 25), alpha);
				
				if (ProfileModel.profile.getUsername() != null) usernameLabel.setText(ProfileModel.profile.getUsername());
				usernameLabel.setBounds(new Rectangle(menuBounds.x + 50, menuBounds.y + menuBounds.height + 20, 100, 25));
				usernameLabel.setAlpha(alpha);
				usernameLabel.render();
				expBar.setBounds(new Rectangle(menuBounds.x + 65, menuBounds.y + menuBounds.height + 10, 100, 7));
				Label expLabel = new Label("EXP");
				expLabel.setColor(Color.CYAN);
				expLabel.setType(FontType.TINY);
				expLabel.setBounds(new Rectangle(menuBounds.x + 45, menuBounds.y + menuBounds.height + 10, 20, 7));
				expLabel.setAlpha(alpha);
				expLabel.render();
				expBar.setMaxValue(ProfileModel.profile.getRank().getNextRequiredExp() - ProfileModel.profile.getRank().getRequiredExp());
				expBar.setCurrentValue(ProfileModel.profile.getCurrentExp() - ProfileModel.profile.getRank().getRequiredExp());
				expBar.setAlpha(alpha);
				expBar.render();
				
				Rectangle profileBounds = new Rectangle(0, menuBounds.y + menuBounds.height + 10, 185, 200);
				Vector3 touchPoint = new Vector3(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(), 0);
		        if (profileBounds.contains(touchPoint.x, touchPoint.y)) {
		        	if (Gdx.input.justTouched()) {
		        		Menu.openModule(MenuModule.PROFILE);
		        	}
		        }
		        
		        TextureManager.draw("main_rankOutline", alpha);
				TextureManager.draw("main_nameOutline", alpha);
				TextureManager.draw("main_expOutline", alpha);
			}
		}
		if (InventoryModel.resources != null && MenuModel.modules.getEnabledModules().contains(MenuModule.INVENTORY)) {
			if (MenuModel.activeModule != MenuModule.INVENTORY) {
				Rectangle inventoryBounds = new Rectangle(menuBounds.x + menuBounds.width - 80, menuBounds.y + menuBounds.height + 10, 120, 120);
				Vector3 touchPoint = new Vector3(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(), 0);
		        if (inventoryBounds.contains(touchPoint.x, touchPoint.y)) {
		        	if (Gdx.input.justTouched()) {
		        		Menu.openModule(MenuModule.INVENTORY);
		        	}
		        }
		        
				creditsLabel.setBounds(new Rectangle(menuBounds.x + menuBounds.width - 95, menuBounds.y + menuBounds.height + 10, 75, 20));
				creditsLabel.setText(StringUtils.formatBigNumber(InventoryModel.resources.getCredits()));
				creditsLabel.setAlpha(alpha);
		        creditsLabel.render();
		        TextureManager.draw(TextureManager.getTexture("ui/image", "credits"), new Rectangle(menuBounds.x + menuBounds.width - 16, menuBounds.y + menuBounds.height + 12, 16, 16), alpha);
				TextureManager.draw("main_creditsOutline", alpha);
			}
		}
	}
	
	private void renderModuleButtons() {
		int elementsPerRow = 5;
		float margin = (((float)menuBounds.width) - (50 * elementsPerRow)) / ((float)(elementsPerRow + 1));
		
		int i = 0;
		for (ColorButton moduleButton : tabButtons.values()) {
			int column = i % elementsPerRow;
			int row = 1 + ((int)i / elementsPerRow);
			moduleButton.setPosition(menuBounds.x + margin + ((50 + margin) * column), menuBounds.y + menuBounds.height - ((50 + margin) * row));
			moduleButton.setAlpha(alpha);
			moduleButton.render();
			i++;
		}
	}

	@Override
	public void dispose() {
		
	}

}

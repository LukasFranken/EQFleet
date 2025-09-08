package de.instinct.eqfleet.menu.main;

import java.util.LinkedHashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.api.core.modules.MenuModule;
import de.instinct.api.meta.dto.Resource;
import de.instinct.eqfleet.menu.common.architecture.BaseModuleRenderer;
import de.instinct.eqfleet.menu.common.components.DefaultButtonFactory;
import de.instinct.eqfleet.menu.module.profile.ProfileModel;
import de.instinct.eqfleet.menu.module.profile.inventory.Inventory;
import de.instinct.eqfleet.menu.module.profile.inventory.InventoryModel;
import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.Button;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.LabeledModelButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.image.Image;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.HorizontalAlignment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.subtypes.PlainRectangularLoadingBar;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.ComplexShapeType;

public class MenuRenderer extends BaseModuleRenderer {
	
	private Map<MenuModule, Button> tabButtons;
	private PlainRectangularLoadingBar expBar;
	private Label usernameLabel;
	private Label creditsLabel;
	
	private final float OPEN_ANIMATION_DURATION = 1.5f;
	private float elapsed;
	private Label menuBackground;
	private Label title;
	private float titleHeight;
	
	private Rectangle menuBounds;
	
	private ColorButton closeModuleButton;
	private Image rankImage;
	private Image creditsImage;

	public MenuRenderer() {
		titleHeight = 40f;
		tabButtons = new LinkedHashMap<>();
	}

	public void reload() {
		close();
		createCloseModuleButton();
		
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
		backgroundBorder.setColor(SkinManager.skinColor);
		backgroundBorder.setSize(2f);
		menuBackground.setBorder(backgroundBorder);
		
		title = new Label("");
		title.setHorizontalAlignment(HorizontalAlignment.LEFT);
		
		float margin = 20f;
		menuBounds = new Rectangle(margin, margin + 20, GraphicsUtil.baseScreenBounds().width - (margin * 2), GraphicsUtil.baseScreenBounds().height - 150);
		
		TextureManager.createShapeTexture("main_menuOutline", ComplexShapeType.ROUNDED_RECTANGLE, menuBounds, SkinManager.skinColor);
		TextureManager.createShapeTexture("main_titleOutline", ComplexShapeType.ROUNDED_RECTANGLE, new Rectangle(menuBounds.x, menuBounds.y + menuBounds.height - titleHeight, menuBounds.width, 2), SkinManager.skinColor);
		TextureManager.createShapeTexture("main_rankOutline", ComplexShapeType.ROUNDED_RECTANGLE, new Rectangle(menuBounds.x, menuBounds.y + menuBounds.height + 10, 35, 35), SkinManager.skinColor);
		TextureManager.createShapeTexture("main_nameOutline", ComplexShapeType.ROUNDED_RECTANGLE, new Rectangle(menuBounds.x + 45, menuBounds.y + menuBounds.height + 20, 120, 25), SkinManager.skinColor);
		TextureManager.createShapeTexture("main_expOutline", ComplexShapeType.ROUNDED_RECTANGLE, new Rectangle(menuBounds.x + 65, menuBounds.y + menuBounds.height + 10, 100, 7), Color.BLUE);
		TextureManager.createShapeTexture("main_creditsOutline", ComplexShapeType.ROUNDED_RECTANGLE, new Rectangle(menuBounds.x + menuBounds.width - 103, menuBounds.y + menuBounds.height + 10, 85, 20), Color.GREEN);
		
		tabButtons = new LinkedHashMap<>();
		for (MenuModule module : MenuModel.buttons) {
			createModuleButton(module);
		}
		
		rankImage = new Image(TextureManager.getTexture("ui/image/rank",  ProfileModel.profile != null ? ProfileModel.profile.getRank().getFileName() : "recruit1"));
		rankImage.setBounds(new Rectangle(menuBounds.x + 5, menuBounds.y + menuBounds.height + 15, 25, 25));
		creditsImage = new Image(TextureManager.getTexture("ui/image", "credits"));
        creditsImage.setBounds(new Rectangle(menuBounds.x + menuBounds.width - 16, menuBounds.y + menuBounds.height + 12, 16, 16));
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
		Color downColor = new Color(SkinManager.skinColor);
		downColor.a = 0.4f;
		closeModuleButton.getLabel().setType(FontType.LARGE);
		closeModuleButton.setDownColor(downColor);
		closeModuleButton.setHoverColor(downColor);
		closeModuleButton.setContentMargin(0f);
	}
	
	public void close() {
		MenuModel.alpha = 0f;
		elapsed = 0f;
		menuBounds = null;
	}
	
	private void createModuleButton(MenuModule module) {
		LabeledModelButton menuModelButton = DefaultButtonFactory.moduleButton(module);
		tabButtons.put(module, menuModelButton);
	}

	@Override
	public void render() {
		if (menuBounds != null) {
			renderIntro();
			renderHeader();
			TextureManager.draw("main_menuOutline", MenuModel.alpha);
			if (MenuModel.activeModule == null) {
				renderModuleButtons();
			} else {
				renderTitle();
			}
			elapsed += Gdx.graphics.getDeltaTime();
		}
	}

	private void renderTitle() {
		float titleHorizontalMargin = 20f;
		Rectangle titleBounds = new Rectangle(MenuModel.moduleBounds.x, MenuModel.moduleBounds.y + MenuModel.moduleBounds.height, MenuModel.moduleBounds.width, titleHeight);
		title.setText(MenuModel.activeModule.toString());
		title.setBounds(new Rectangle(titleBounds.x + titleHorizontalMargin, titleBounds.y, titleBounds.width - (titleHorizontalMargin * 2), titleBounds.height));
		title.render();
		
		float smallXFontAdjustment = 2f * GraphicsUtil.getVerticalDisplayScaleFactor();
		closeModuleButton.setFixedHeight(titleHeight - smallXFontAdjustment);
		closeModuleButton.setFixedWidth(titleHeight);
		closeModuleButton.setPosition(titleBounds.x + titleBounds.width - titleHeight, titleBounds.y + smallXFontAdjustment);
		closeModuleButton.render();
		TextureManager.draw("main_titleOutline", MenuModel.alpha);
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
				MenuModel.alpha = (elapsed - (OPEN_ANIMATION_DURATION / 2)) / (OPEN_ANIMATION_DURATION / 2);
			} else {
				MenuModel.alpha = 1f;
			}
		}
		menuBackground.render();
	}

	private void renderHeader() {
		if (ProfileModel.profile != null && MenuModel.unlockedModules.getEnabledModules().contains(MenuModule.PROFILE)) {
			if (MenuModel.activeModule != MenuModule.PROFILE) {
				rankImage.setAlpha(MenuModel.alpha);
				rankImage.render();
				if (ProfileModel.profile.getUsername() != null) usernameLabel.setText(ProfileModel.profile.getUsername());
				usernameLabel.setBounds(new Rectangle(menuBounds.x + 50, menuBounds.y + menuBounds.height + 20, 100, 25));
				usernameLabel.setAlpha(MenuModel.alpha);
				usernameLabel.render();
				expBar.setBounds(new Rectangle(menuBounds.x + 65, menuBounds.y + menuBounds.height + 10, 100, 7));
				Label expLabel = new Label("EXP");
				expLabel.setColor(Color.BLUE);
				expLabel.setType(FontType.TINY);
				expLabel.setBounds(new Rectangle(menuBounds.x + 45, menuBounds.y + menuBounds.height + 10, 20, 7));
				expLabel.setAlpha(MenuModel.alpha);
				expLabel.render();
				expBar.setMaxValue(ProfileModel.profile.getRank().getNextRequiredExp() - ProfileModel.profile.getRank().getRequiredExp());
				expBar.setCurrentValue(ProfileModel.profile.getCurrentExp() - ProfileModel.profile.getRank().getRequiredExp());
				expBar.setAlpha(MenuModel.alpha);
				expBar.render();
		        
		        TextureManager.draw("main_rankOutline", MenuModel.alpha);
				TextureManager.draw("main_nameOutline", MenuModel.alpha);
				TextureManager.draw("main_expOutline", MenuModel.alpha);
			}
		}
		if (InventoryModel.resources != null && MenuModel.unlockedModules.getEnabledModules().contains(MenuModule.INVENTORY)) {
			if (MenuModel.activeModule != MenuModule.INVENTORY) {
				creditsLabel.setBounds(new Rectangle(menuBounds.x + menuBounds.width - 95, menuBounds.y + menuBounds.height + 10, 70, 20));
				creditsLabel.setText(StringUtils.formatBigNumber(Inventory.getResource(Resource.CREDITS)));
				creditsLabel.setAlpha(MenuModel.alpha);
		        creditsLabel.render();
		        creditsImage.setAlpha(MenuModel.alpha);
		        creditsImage.render();
				TextureManager.draw("main_creditsOutline", MenuModel.alpha);
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
		
	}

}

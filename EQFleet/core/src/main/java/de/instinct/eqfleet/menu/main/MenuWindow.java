package de.instinct.eqfleet.menu.main;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqfleet.menu.common.components.DefaultButtonFactory;
import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.MathUtil;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.Component;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.HorizontalAlignment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.Shapes;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQRectangle;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.utility.EQGlowConfig;

public class MenuWindow extends Component {
	
	private Label title;
	private float titleHeight = 40f;
	
	private ColorButton closeModuleButton;
	
	private Rectangle titleBounds;
	private Rectangle titleDividerBounds;
	
	private EQRectangle titleDividerShape;
	private EQRectangle menuBackground;
	
	public void init() {
		menuBackground = EQRectangle.builder()
				.bounds(new Rectangle())
				.color(SkinManager.skinColor)
				.glowConfig(EQGlowConfig.builder().build())
				.thickness(2f)
				.build();
		
		createCloseModuleButton();
		initTitle();
	}
	
	private void createCloseModuleButton() {
		closeModuleButton = DefaultButtonFactory.colorButton("x", new Action() {
			
			@Override
			public void execute() {
				MenuModel.activeModule = null;
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
	
	private void initTitle() {
		titleBounds = new Rectangle();
		titleDividerBounds = new Rectangle();
		title = new Label("");
		title.setHorizontalAlignment(HorizontalAlignment.LEFT);
		
		Color titleColor = new Color(SkinManager.skinColor);
		titleDividerShape = EQRectangle.builder()
				.bounds(titleDividerBounds)
				.color(titleColor)
				.glowConfig(EQGlowConfig.builder().build())
				.thickness(2f)
				.build();
	}
	
	@Override
	protected void updateComponent() {
		float titleHorizontalMargin = 20f;
		titleBounds.set(getBounds().x, getBounds().y + getBounds().height - titleHeight, getBounds().width, titleHeight);
		
		title.setText(MenuModel.activeModule == null ? "MENU" : MenuModel.activeModule.toString());
		title.setPosition(titleBounds.x + titleHorizontalMargin, titleBounds.y);
		title.setFixedWidth(titleBounds.width - (titleHorizontalMargin * 2));
		title.setFixedHeight(titleBounds.height);
		title.getColor().a = MenuModel.alpha;
		
		titleDividerBounds.set(titleBounds.x, titleBounds.y, titleBounds.width, 2f);
		titleDividerShape.getColor().a = MenuModel.alpha;
		
		float smallXFontAdjustment = 2f * GraphicsUtil.getVerticalDisplayScaleFactor();
		closeModuleButton.setFixedHeight(titleHeight - smallXFontAdjustment);
		closeModuleButton.setFixedWidth(titleHeight);
		closeModuleButton.setPosition(titleBounds.x + titleBounds.width - titleHeight, titleBounds.y + smallXFontAdjustment);
		closeModuleButton.getColor().a = MenuModel.alpha;
		
		float ratio = MathUtil.easeInOut(0f, 1f, MenuModel.openAnimationElapsed);
		menuBackground.getBounds().set(getBounds().x, getBounds().y, getBounds().width, getBounds().height * ratio);
	}

	@Override
	protected void renderComponent() {
		title.render();
		closeModuleButton.render();
		Shapes.draw(titleDividerShape);
		Shapes.draw(menuBackground);
	}

	@Override
	public float calculateHeight() {
		return getBounds().height;
	}

	@Override
	public float calculateWidth() {
		return getBounds().width;
	}

	@Override
	public void dispose() {
		title.dispose();
		closeModuleButton.dispose();
	}

}

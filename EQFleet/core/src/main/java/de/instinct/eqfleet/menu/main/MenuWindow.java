package de.instinct.eqfleet.menu.main;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqfleet.holo.HoloPanel;
import de.instinct.eqfleet.holo.HoloRenderer;
import de.instinct.eqfleet.holo.style.HoloPanelStyle;
import de.instinct.eqfleet.menu.common.components.DefaultButtonFactory;
import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.MathUtil;
import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.debug.modulator.Modulator;
import de.instinct.eqlibgdxutils.debug.modulator.modulation.types.RangeModulation;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.Component;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.slider.LabelUpdateAction;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.slider.ValueChangeAction;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.HorizontalAlignment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;

public class MenuWindow extends Component {
	
	private Label title;
	private float titleHeight = 40f;
	
	private ColorButton closeModuleButton;
	private Rectangle titleBounds;
	
	private HoloPanel menuBackgroundPanel;
	private HoloPanel titleBackgroundPanel;
	
	public MenuWindow() {
		menuBackgroundPanel = HoloPanel.builder()
				.build();
		titleBackgroundPanel = HoloPanel.builder()
				.style(HoloPanelStyle.builder()
						.reflectionStrength(0f)
						.build())
				.build();
		
		createCloseModuleButton();
		initTitle();
		
		RangeModulation glowSizeMod = new RangeModulation("glow", new ValueChangeAction() {
			
			@Override
			public void execute(float value) {
				menuBackgroundPanel.getStyle().getGlowConfiguration().setGlowSize(value * 100f);
			}
			
		}, menuBackgroundPanel.getStyle().getGlowConfiguration().getGlowSize() / 100f, new LabelUpdateAction() {

			@Override
			public String getLabelText(float value) {
				return StringUtils.format(value * 100f, 2);
			}

		});
		Modulator.add(glowSizeMod);
		
		RangeModulation reflectionStrengthMod = new RangeModulation("refl", new ValueChangeAction() {
			
			@Override
			public void execute(float value) {
				menuBackgroundPanel.getStyle().setReflectionStrength(value);
			}
			
		}, menuBackgroundPanel.getStyle().getReflectionStrength());
		Modulator.add(reflectionStrengthMod);
		
		RangeModulation backgroundAlphaMod = new RangeModulation("bg-a", new ValueChangeAction() {
			
			@Override
			public void execute(float value) {
				menuBackgroundPanel.getStyle().setFillAlpha(value);
			}
			
		}, menuBackgroundPanel.getStyle().getFillAlpha());
		Modulator.add(backgroundAlphaMod);
	}
	
	private void createCloseModuleButton() {
		closeModuleButton = DefaultButtonFactory.colorButton("x", new Action() {
			
			@Override
			public void execute() {
				MenuModel.activeModule = null;
			}
			
		});
		closeModuleButton.setBorder(null);
		closeModuleButton.setColor(new Color(0, 0, 0, 0));
		Color downColor = new Color(SkinManager.skinColor);
		downColor.a = 0.4f;
		closeModuleButton.getLabel().setType(FontType.LARGE);
		closeModuleButton.getLabel().setColor(Color.WHITE);
		closeModuleButton.setDownColor(downColor);
		closeModuleButton.setHoverColor(new Color(0.5f, 0f, 0f, 0.1f));
		closeModuleButton.setContentMargin(0f);
	}
	 
	private void initTitle() {
		titleBounds = new Rectangle();
		title = new Label("");
		title.setHorizontalAlignment(HorizontalAlignment.LEFT);
		title.setColor(Color.WHITE);
	}
	
	@Override
	protected void updateComponent() {
		float titleHorizontalMargin = 20f;
		titleBounds.set(getBounds().x, getBounds().y + getBounds().height, getBounds().width, titleHeight);
		
		title.setText(MenuModel.activeModule == null ? "MENU" : MenuModel.activeModule.toString());
		title.setPosition(titleBounds.x + titleHorizontalMargin, titleBounds.y);
		title.setFixedWidth(titleBounds.width - (titleHorizontalMargin * 2));
		title.setFixedHeight(titleBounds.height);
		title.setAlpha(MenuModel.alpha);
		
		titleBackgroundPanel.getBounds().set(titleBounds.x, titleBounds.y, titleBounds.width, titleHeight);
		titleBackgroundPanel.getColor().set(SkinManager.skinColor);
		titleBackgroundPanel.getColor().a = MenuModel.alpha;
		
		float smallXFontAdjustment = 2f * GraphicsUtil.getVerticalDisplayScaleFactor();
		closeModuleButton.setFixedHeight(titleHeight - smallXFontAdjustment);
		closeModuleButton.setFixedWidth(titleHeight);
		closeModuleButton.setPosition(titleBounds.x + titleBounds.width - titleHeight, titleBounds.y + smallXFontAdjustment);
		closeModuleButton.setAlpha(MenuModel.alpha);
		
		float ratio = MathUtil.easeInOut(0f, 1f, MenuModel.openAnimationElapsed);
		menuBackgroundPanel.getBounds().set(getBounds().x, getBounds().y, getBounds().width, (getBounds().height + titleHeight) * ratio);
		menuBackgroundPanel.getColor().set(SkinManager.skinColor);
	}

	@Override
	protected void renderComponent() {
		HoloRenderer.drawPanel(menuBackgroundPanel);
		HoloRenderer.drawPanel(titleBackgroundPanel);
		title.render();
		if (MenuModel.activeModule != null) closeModuleButton.render();
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

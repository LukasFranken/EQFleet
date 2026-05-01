package de.instinct.eqfleet.menu.main;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqfleet.holo.HoloPanel;
import de.instinct.eqfleet.holo.HoloRenderer;
import de.instinct.eqfleet.holo.style.HoloPanelStyle;
import de.instinct.eqfleet.menu.common.components.DefaultButtonFactory;
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
		Modulator.add("holo", glowSizeMod);
		
		RangeModulation reflectionStrengthMod = new RangeModulation("refl", new ValueChangeAction() {
			
			@Override
			public void execute(float value) {
				menuBackgroundPanel.getStyle().setReflectionStrength(value);
			}
			
		}, menuBackgroundPanel.getStyle().getReflectionStrength());
		Modulator.add("holo", reflectionStrengthMod);
		
		RangeModulation backgroundAlphaMod = new RangeModulation("bg-a", new ValueChangeAction() {
			
			@Override
			public void execute(float value) {
				menuBackgroundPanel.getStyle().setFillAlpha(value);
			}
			
		}, menuBackgroundPanel.getStyle().getFillAlpha());
		Modulator.add("holo", backgroundAlphaMod);
	}
	
	private void createCloseModuleButton() {
		closeModuleButton = DefaultButtonFactory.colorButton("<<", new Action() {
			
			@Override
			public void execute() {
				MenuModel.activeModule = null;
				Breadcrumbs.remove();
			}
			
		});
		closeModuleButton.setColor(new Color(0, 0, 0, 0));
		closeModuleButton.getLabel().setColor(Color.WHITE);
		closeModuleButton.getLabel().setType(FontType.LARGE);
		closeModuleButton.setBorder(null);
	}
	 
	private void initTitle() {
		titleBounds = new Rectangle();
		title = new Label("");
		title.setHorizontalAlignment(HorizontalAlignment.LEFT);
		title.setColor(Color.WHITE);
		title.setType(FontType.MEDIUM);
	}
	
	@Override
	protected void updateComponent() {
		float titleHorizontalMargin = 20f;
		titleBounds.set(getBounds().x, getBounds().y + getBounds().height, getBounds().width, titleHeight);
		
		String titleText = "";
		for (String breadcrumb : Breadcrumbs.getBreadcrumbs()) {
			if (titleText.isEmpty()) {
				titleText = breadcrumb;
			} else {
				titleText += " > " + breadcrumb;
			}
		}
		title.setText(titleText);
		title.setPosition(titleBounds.x + titleHorizontalMargin, titleBounds.y);
		title.setFixedWidth(titleBounds.width - (titleHorizontalMargin * 2));
		title.setFixedHeight(titleBounds.height);
		title.setAlpha(MenuModel.alpha);
		
		titleBackgroundPanel.getBounds().set(titleBounds.x, titleBounds.y, titleBounds.width, titleHeight);
		titleBackgroundPanel.getColor().set(SkinManager.skinColor);
		titleBackgroundPanel.getColor().a = MenuModel.alpha;
		
		closeModuleButton.getHoverColor().set(SkinManager.skinColor);
		closeModuleButton.getHoverColor().a = 0.3f * MenuModel.alpha;
		closeModuleButton.getDownColor().set(SkinManager.skinColor);
		closeModuleButton.getDownColor().a = 0.5f * MenuModel.alpha;
		closeModuleButton.setBounds(titleBounds.x + titleBounds.width - 50f, titleBounds.y, 50f, titleBounds.height);
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

package de.instinct.eqfleet.menu.module.profile.model;

import com.badlogic.gdx.graphics.Color;

import de.instinct.eqfleet.game.GameConfig;
import de.instinct.eqfleet.menu.common.components.label.DefaultLabelFactory;
import de.instinct.eqfleet.menu.common.components.label.LabelStackConfiguration;
import de.instinct.eqfleet.menu.module.profile.ProfileModel;
import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.rendering.ui.component.Component;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.subtypes.BoxedRectangularLoadingBar;
import de.instinct.eqlibgdxutils.rendering.ui.container.list.ElementStack;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.Shapes;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQRectangle;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.utility.EQGlowConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class CommanderSection extends Component {
	
	private EQRectangle outlineShape;
	private Label cpBarLabel;
	private BoxedRectangularLoadingBar bar;
	private ElementStack maxCPLabelStack;
	private ElementStack startCPLabelStack;
	private ElementStack cpPerSecLabelStack;
	
	private float inModuleStackMargin = 20f;
	
	public CommanderSection() {
		super();
		
		outlineShape = EQRectangle.builder()
				.bounds(getBounds())
				.color(new Color(SkinManager.skinColor))
				.glowConfig(EQGlowConfig.builder().build())
				.thickness(1f)
				.build();
		
		Border barBorder = new Border();
		barBorder.setColor(GameConfig.teammate1Color);
		barBorder.setSize(2f);
		
		cpBarLabel = new Label("CP");
		cpBarLabel.setColor(GameConfig.teammate1Color);
		cpBarLabel.setType(FontType.SMALL);
		cpBarLabel.setBorder(barBorder);
		cpBarLabel.setFixedHeight(20f);
		
		bar = new BoxedRectangularLoadingBar();
		bar.setBackground(TextureManager.createTexture(new Color(0f, 0f, 0f, 0f)));
		bar.setFixedHeight(20f);
		bar.setBorder(barBorder);
		
		maxCPLabelStack = DefaultLabelFactory.createLabelStack(LabelStackConfiguration.builder()
				.tag("Max CP")
				.value("-")
				.type(FontType.SMALL)
				.build());
		maxCPLabelStack.setFixedHeight(20f);
		
		startCPLabelStack = DefaultLabelFactory.createLabelStack(LabelStackConfiguration.builder()
				.tag("Start CP")
				.value("-")
				.type(FontType.SMALL)
				.build());
		startCPLabelStack.setFixedHeight(20f);
		
		cpPerSecLabelStack = DefaultLabelFactory.createLabelStack(LabelStackConfiguration.builder()
				.tag("CP / sec")
				.value("-")
				.type(FontType.SMALL)
				.build());
		cpPerSecLabelStack.setFixedHeight(20f);
	}
	
	@Override
	public float calculateHeight() {
		return 100f;
	}

	@Override
	public float calculateWidth() {
		return getBounds().width;
	}
	
	@Override
	protected void updateComponent() {
		bar.setSegments((int)ProfileModel.commanderData.getMaxCommandPoints());
		bar.setMaxValue(ProfileModel.commanderData.getMaxCommandPoints());
		bar.setCurrentValue(ProfileModel.commanderData.getStartCommandPoints());
		
		((Label)maxCPLabelStack.getElements().get(1)).setText(StringUtils.format(ProfileModel.commanderData.getMaxCommandPoints(), 0));
		((Label)startCPLabelStack.getElements().get(1)).setText(StringUtils.format(ProfileModel.commanderData.getStartCommandPoints(), 0));
		((Label)cpPerSecLabelStack.getElements().get(1)).setText(StringUtils.format(ProfileModel.commanderData.getCommandPointsGenerationSpeed(), 2));
		
		cpBarLabel.setFixedWidth(20f);
		bar.setFixedWidth(getBounds().width - (inModuleStackMargin * 2) - cpBarLabel.getBounds().width);
		maxCPLabelStack.setFixedWidth(getBounds().width - (inModuleStackMargin * 2));
		startCPLabelStack.setFixedWidth(getBounds().width - (inModuleStackMargin * 2));
		cpPerSecLabelStack.setFixedWidth(getBounds().width - (inModuleStackMargin * 2));
		
		cpBarLabel.setPosition(getBounds().x + inModuleStackMargin, getBounds().y + getBounds().height - 30);
		bar.setPosition(getBounds().x + inModuleStackMargin + cpBarLabel.getBounds().width, getBounds().y + getBounds().height - 30);
		maxCPLabelStack.setPosition(getBounds().x + inModuleStackMargin, getBounds().y + getBounds().height - 55);
		startCPLabelStack.setPosition(getBounds().x + inModuleStackMargin, getBounds().y + getBounds().height - 75);
		cpPerSecLabelStack.setPosition(getBounds().x + inModuleStackMargin, getBounds().y + getBounds().height - 95);
	}
	
	@Override
	protected void renderComponent() {
		if (ProfileModel.commanderData != null) {
			Shapes.draw(outlineShape);
			cpBarLabel.render();
			bar.render();
			maxCPLabelStack.render();
			startCPLabelStack.render();
			cpPerSecLabelStack.render();
		}
	}

	@Override
	public void dispose() {
		cpBarLabel.dispose();
		bar.dispose();
		maxCPLabelStack.dispose();
		startCPLabelStack.dispose();
		cpPerSecLabelStack.dispose();
	}

}

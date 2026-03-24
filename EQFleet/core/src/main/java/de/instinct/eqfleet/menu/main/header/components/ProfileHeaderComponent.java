package de.instinct.eqfleet.menu.main.header.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.api.core.modules.MenuModule;
import de.instinct.api.meta.dto.Resource;
import de.instinct.eqfleet.menu.main.MenuModel;
import de.instinct.eqfleet.menu.main.message.types.OpenModuleMessage;
import de.instinct.eqfleet.menu.module.profile.ProfileModel;
import de.instinct.eqfleet.menu.module.profile.ProfileModuleAPI;
import de.instinct.eqlibgdxutils.InputUtil;
import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.rendering.ui.component.Component;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.image.Image;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.HorizontalAlignment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.subtypes.PlainRectangularLoadingBar;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.Shapes;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQRectangle;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.utility.EQGlowConfig;

public class ProfileHeaderComponent extends Component {
	
	private PlainRectangularLoadingBar expBar;
	
	private Label usernameLabel;
	
	private Label expLabel;
	private Image rankImage;
	private Label creditsLabel;
	private Label creditsCurrencyLabel;
	private Label equilibriumLabel;
	private Label equilibriumCurrencyLabel;
	
	private Rectangle rankBounds;
	private Rectangle nameBounds;
	private Rectangle expBounds;
	
	private EQRectangle nameBorder;
	private EQRectangle rankBorder;
	
	public ProfileHeaderComponent() {
		rankBounds = new Rectangle();
		nameBounds = new Rectangle();
		expBounds = new Rectangle();
		
		expBar = new PlainRectangularLoadingBar();
		expBar.setBar(TextureManager.createTexture(Color.BLUE));
		expBar.setCustomDescriptor("");
		Border barBorder = new Border();
		barBorder.setSize(1f);
		barBorder.setColor(Color.BLUE);
		expBar.setBorder(barBorder);
		
		usernameLabel = new Label("");
		usernameLabel.setColor(Color.LIGHT_GRAY);
		usernameLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
		usernameLabel.setType(FontType.SMALL);
		
		rankImage = new Image(TextureManager.getTexture("ui/image/rank", "recruit1"));
		
		Color creditsColor = new Color(ProfileModuleAPI.getColorForResource(Resource.CREDITS));
		Border creditsBorder = new Border();
		creditsBorder.setSize(1f);
		creditsBorder.setColor(creditsColor);
		creditsCurrencyLabel = new Label("€");
		creditsCurrencyLabel.setColor(creditsColor);
		creditsCurrencyLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
		creditsCurrencyLabel.setStartMargin(3);
		creditsCurrencyLabel.setType(FontType.TINY);
		creditsCurrencyLabel.setBorder(creditsBorder);
		
		creditsLabel = new Label("");
		creditsLabel.setColor(creditsColor);
		creditsLabel.setHorizontalAlignment(HorizontalAlignment.RIGHT);
		creditsLabel.setType(FontType.TINY);
		creditsLabel.setStartMargin(3);
		
		Color equilibriumColor = new Color(ProfileModuleAPI.getColorForResource(Resource.EQUILIBRIUM));
		Border equilibriumBorder = new Border();
		equilibriumBorder.setSize(1f);
		equilibriumBorder.setColor(equilibriumColor);
		equilibriumCurrencyLabel = new Label("E");
		equilibriumCurrencyLabel.setColor(equilibriumColor);
		equilibriumCurrencyLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
		equilibriumCurrencyLabel.setStartMargin(3);
		equilibriumCurrencyLabel.setType(FontType.TINY);
		equilibriumCurrencyLabel.setBorder(equilibriumBorder);
		
		equilibriumLabel = new Label("");
		equilibriumLabel.setColor(equilibriumColor);
		equilibriumLabel.setHorizontalAlignment(HorizontalAlignment.RIGHT);
		equilibriumLabel.setType(FontType.TINY);
		equilibriumLabel.setStartMargin(3);
		
		expLabel = new Label("EXP");
		expLabel.setColor(Color.BLUE);
		expLabel.setType(FontType.TINY);
		expLabel.setHorizontalAlignment(HorizontalAlignment.RIGHT);
		
		nameBorder = EQRectangle.builder()
				.bounds(nameBounds)
				.color(new Color(SkinManager.skinColor))
				.glowConfig(EQGlowConfig.builder().build())
				.thickness(1f)
				.build();
		rankBorder = EQRectangle.builder()
				.bounds(rankBounds)
				.color(new Color(SkinManager.skinColor))
				.glowConfig(EQGlowConfig.builder().build())
				.thickness(1f)
				.build();
		
	}

	@Override
	protected void updateComponent() {
		usernameLabel.setText(ProfileModel.profile.getUsername() != null ? ProfileModel.profile.getUsername() : "???");
		rankImage.setTexture(TextureManager.getTexture("ui/image/rank",  ProfileModel.profile != null ? ProfileModel.profile.getRank().getFileName() : "recruit1"));
		
		expBar.setMaxValue(ProfileModel.profile.getRank().getNextRequiredExp() - ProfileModel.profile.getRank().getRequiredExp());
		expBar.setCurrentValue(ProfileModel.profile.getCurrentExp() - ProfileModel.profile.getRank().getRequiredExp());
		
		creditsLabel.setText(StringUtils.formatBigNumber(ProfileModuleAPI.getResource(Resource.CREDITS)));
		equilibriumLabel.setText(StringUtils.formatBigNumber(ProfileModuleAPI.getResource(Resource.EQUILIBRIUM)));
		
		updateBounds();
		updateAlphas();
		
		if (InputUtil.isClicked() && InputUtil.mouseIsOver(getBounds())) {
			MenuModel.messageQueue.add(OpenModuleMessage.builder().module(MenuModule.PROFILE).build());
		}
	}
	
	private void updateBounds() {
		rankBounds.set(getBounds().x, getBounds().y, 45, 45);
		rankImage.setBounds(rankBounds.x + 5, rankBounds.y + 5, rankBounds.width - 10, rankBounds.height - 10);
		
		expBounds.set(getBounds().x + 50, getBounds().y + 15, getBounds().width - 50, 7);
		expLabel.setBounds(expBounds.x, expBounds.y, 18, expBounds.height);
		expBar.setBounds(expBounds.x + 20, expBounds.y, expBounds.width - 20, expBounds.height);
		
		nameBounds.set(getBounds().x + 50, getBounds().y + 25, getBounds().width - 50, 20);
		usernameLabel.setBounds(nameBounds.x + 5, nameBounds.y, nameBounds.width - 5, nameBounds.height);
		
		float resourceLabelWidth = (getBounds().width - 50) / 2f - 1;
		creditsCurrencyLabel.setBounds(getBounds().x + 50, getBounds().y, resourceLabelWidth, 12);
		creditsLabel.setBounds(getBounds().x + 50, getBounds().y, resourceLabelWidth, 12);
		equilibriumCurrencyLabel.setBounds(getBounds().x + 50 + resourceLabelWidth + 2, getBounds().y, resourceLabelWidth, 12);
		equilibriumLabel.setBounds(getBounds().x + 50 + resourceLabelWidth + 2, getBounds().y, resourceLabelWidth, 12);
	}

	private void updateAlphas() {
		rankImage.setAlpha(getAlpha());
		expBar.setAlpha(getAlpha());
		usernameLabel.setAlpha(getAlpha());
		expLabel.setAlpha(getAlpha());
		creditsCurrencyLabel.setAlpha(getAlpha());
		creditsLabel.setAlpha(getAlpha());
		equilibriumCurrencyLabel.setAlpha(getAlpha());
		equilibriumLabel.setAlpha(getAlpha());
		nameBorder.getColor().a = getAlpha();
		rankBorder.getColor().a = getAlpha();
	}

	@Override
	protected void renderComponent() {
		rankImage.render();
		usernameLabel.render();
		expLabel.render();
		expBar.render();
		creditsCurrencyLabel.render();
		creditsLabel.render();
		if (ProfileModuleAPI.getResource(Resource.EQUILIBRIUM) > 0) {
			equilibriumCurrencyLabel.render();
			equilibriumLabel.render();
		}
		Shapes.draw(nameBorder);
		Shapes.draw(rankBorder);
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
		expBar.dispose();
		usernameLabel.dispose();
		rankImage.dispose();
	}

}

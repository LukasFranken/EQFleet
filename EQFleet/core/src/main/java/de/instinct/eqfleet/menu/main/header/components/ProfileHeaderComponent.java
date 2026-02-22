package de.instinct.eqfleet.menu.main.header.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqfleet.menu.main.MenuModel;
import de.instinct.eqfleet.menu.module.profile.ProfileModel;
import de.instinct.eqlibgdxutils.GraphicsUtil;
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
	
	private Rectangle rankBounds;
	private Rectangle nameBounds;
	private Rectangle expBounds;
	
	private EQRectangle nameBorder;
	private EQRectangle rankBorder;
	
	public void init() {
		expBar = new PlainRectangularLoadingBar();
		expBar.setBar(TextureManager.createTexture(Color.BLUE));
		expBar.setCustomDescriptor("");
		Border barBorder = new Border();
		barBorder.setSize(1f);
		barBorder.setColor(Color.BLUE);
		expBar.setBorder(barBorder);
		
		usernameLabel = new Label(ProfileModel.profile != null && ProfileModel.profile.getUsername() != null ? ProfileModel.profile.getUsername() : "???");
		usernameLabel.setColor(Color.LIGHT_GRAY);
		usernameLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
		
		float margin = 20f;
		Rectangle menuBounds = new Rectangle(margin, margin + 20, GraphicsUtil.screenBounds().width - (margin * 2), GraphicsUtil.screenBounds().height - 150);
		
		rankBounds = new Rectangle(menuBounds.x, menuBounds.y + menuBounds.height + 10, 35, 35);
		nameBounds = new Rectangle(menuBounds.x + 45, menuBounds.y + menuBounds.height + 20, 120, 25);
		expBounds = new Rectangle(menuBounds.x + 65, menuBounds.y + menuBounds.height + 10, 100, 7);
		
		rankImage = new Image(TextureManager.getTexture("ui/image/rank",  ProfileModel.profile != null ? ProfileModel.profile.getRank().getFileName() : "recruit1"));
		rankImage.setBounds(new Rectangle(menuBounds.x + 5, menuBounds.y + menuBounds.height + 15, 25, 25));
		
		expLabel = new Label("EXP");
		expLabel.setColor(Color.BLUE);
		expLabel.setType(FontType.TINY);
		expLabel.setAlpha(MenuModel.alpha);
		expLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
		
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
		rankImage.setAlpha(MenuModel.alpha);
		
		usernameLabel.setBounds(getBounds().x + 50, getBounds().y + getBounds().height + 20, 100, 25);
		usernameLabel.setAlpha(MenuModel.alpha);
		
		expLabel.setBounds(new Rectangle(getBounds().x + 45, getBounds().y + getBounds().height + 10, 20, 7));
		
		expBar.setBounds(expBounds);
		expBar.setMaxValue(ProfileModel.profile.getRank().getNextRequiredExp() - ProfileModel.profile.getRank().getRequiredExp());
		expBar.setCurrentValue(ProfileModel.profile.getCurrentExp() - ProfileModel.profile.getRank().getRequiredExp());
		expBar.setAlpha(MenuModel.alpha);
		
		rankImage.setAlpha(getAlpha());
		usernameLabel.getColor().a =getAlpha();
		expLabel.getColor().a = getAlpha();
		nameBorder.getColor().a = getAlpha();
		rankBorder.getColor().a = getAlpha();
	}
	
	@Override
	protected void renderComponent() {
		rankImage.render();
		usernameLabel.render();
		expLabel.render();
		expBar.render();
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

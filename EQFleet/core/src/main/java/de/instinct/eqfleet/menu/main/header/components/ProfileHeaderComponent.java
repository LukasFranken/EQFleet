package de.instinct.eqfleet.menu.main.header.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqfleet.menu.module.profile.ProfileModel;
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
		
		rankImage = new Image(TextureManager.getTexture("ui/image/rank", "recruit1"));
		
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
		
		updateBounds();
		updateAlphas();
	}
	
	private void updateBounds() {
		rankBounds.set(getBounds().x, getBounds().y, 35, 35);
		rankImage.setBounds(rankBounds.x + 5, rankBounds.y + 5, rankBounds.width - 10, rankBounds.height - 10);
		
		expBounds.set(getBounds().x + 40, getBounds().y, getBounds().width - 40, 7);
		expLabel.setBounds(expBounds.x, expBounds.y, 18, expBounds.height);
		expBar.setBounds(expBounds.x + 20, expBounds.y, expBounds.width - 20, expBounds.height);
		
		nameBounds.set(getBounds().x + 40, getBounds().y + 10, getBounds().width - 40, 25);
		usernameLabel.setBounds(nameBounds.x + 5, nameBounds.y, nameBounds.width - 5, nameBounds.height);
	}

	private void updateAlphas() {
		rankImage.setAlpha(getAlpha());
		expBar.setAlpha(getAlpha());
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

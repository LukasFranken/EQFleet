package de.instinct.eqfleet.menu.module.profile.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqfleet.menu.module.profile.ProfileModel;
import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.rendering.ui.component.Component;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.image.Image;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.HorizontalAlignment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.subtypes.PlainRectangularLoadingBar;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.Shapes;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQRectangle;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.utility.EQGlowConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ExperienceSection extends Component {
	
	private Rectangle nameLabelBounds;
	private Rectangle expBarBounds;
	private Rectangle expLabelBounds;
	
	private Label currentRankNameLabel;
	private Label nextRankNameLabel;
	private Label currentRankEXPLabel;
	private Label nextRankEXPLabel;
	private PlainRectangularLoadingBar expBar;
	private Label experienceGainedLabel;
	
	private Image rankImage;
	private Image nextRankImage;
	
	private float margin = 10f;
	
	private boolean rankImagesEnabled;
	private boolean rankNamesEnabled;
	
	public ExperienceSection() {
		super();
		rankImagesEnabled = true;
		rankNamesEnabled = true;
		currentRankNameLabel = new Label("");
		currentRankNameLabel.setColor(Color.LIGHT_GRAY);
		currentRankNameLabel.setType(FontType.MICRO);
		currentRankNameLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
		nextRankNameLabel = new Label("");
		nextRankNameLabel.setType(FontType.MICRO);
		nextRankNameLabel.setColor(Color.LIGHT_GRAY);
		nextRankNameLabel.setHorizontalAlignment(HorizontalAlignment.RIGHT);
		
		expBar = new PlainRectangularLoadingBar();
		expBar.setBar(TextureManager.createTexture(Color.BLUE));
		expBar.setBackground(TextureManager.createTexture(new Color(0f, 0f, 0f, 0f)));
		expBar.setCustomDescriptor("");
		
		currentRankEXPLabel = new Label("");
		currentRankEXPLabel.setColor(Color.BLUE);
		currentRankEXPLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
		currentRankEXPLabel.setType(FontType.SMALL);
		nextRankEXPLabel = new Label("");
		nextRankEXPLabel.setColor(Color.BLUE);
		nextRankEXPLabel.setHorizontalAlignment(HorizontalAlignment.RIGHT);
		nextRankEXPLabel.setType(FontType.SMALL);
		
		experienceGainedLabel = new Label("");
		experienceGainedLabel.setType(FontType.TINY);
		experienceGainedLabel.setColor(Color.BLUE);
		experienceGainedLabel.setHorizontalAlignment(HorizontalAlignment.CENTER);
	}
	
	public void init(float x, float y, float width) {
		super.setBounds(new Rectangle(x, y, width, calculateHeight()));
		nameLabelBounds = new Rectangle(getBounds().x + (rankImagesEnabled ? 45 : 0), getBounds().y + getBounds().height - 10, getBounds().width - (rankImagesEnabled ? 90 : 0), 10);
		expBarBounds = new Rectangle(nameLabelBounds.x , nameLabelBounds.y - margin - 3, nameLabelBounds.width, 10);
		expLabelBounds = new Rectangle(expBarBounds.x, expBarBounds.y - 3 - margin, expBarBounds.width, 10);
	}
	
	public float getActualHeight() {
		return calculateHeight();
	}
	
	@Override
	public float calculateHeight() {
		return 40f;
	}

	@Override
	public float calculateWidth() {
		return getBounds().width;
	}
	
	@Override
	protected void updateComponent() {
		currentRankNameLabel.setBounds(nameLabelBounds);
		currentRankNameLabel.setText(ProfileModel.profile.getRank().getLabel());
		nextRankNameLabel.setBounds(nameLabelBounds);
		nextRankNameLabel.setText(ProfileModel.profile.getRank().getNextRank().getLabel());
		expBar.setBounds(expBarBounds);
		expBar.setMaxValue(ProfileModel.profile.getRank().getNextRequiredExp() - ProfileModel.profile.getRank().getRequiredExp());
		expBar.setCurrentValue(ProfileModel.profile.getCurrentExp() - ProfileModel.profile.getRank().getRequiredExp());
		currentRankEXPLabel.setBounds(expLabelBounds);
		currentRankEXPLabel.setText(StringUtils.formatBigNumber(ProfileModel.profile.getRank().getRequiredExp(), 1));
		nextRankEXPLabel.setBounds(expLabelBounds);
		nextRankEXPLabel.setText(StringUtils.formatBigNumber(ProfileModel.profile.getRank().getNextRequiredExp(), 1));
		
		rankImage = new Image(TextureManager.getTexture("ui/image/rank", ProfileModel.profile.getRank().getFileName()));
		nextRankImage = new Image(TextureManager.getTexture("ui/image/rank", ProfileModel.profile.getRank().getNextRank().getFileName()));
		rankImage.setBounds(new Rectangle(getBounds().x, expLabelBounds.y, 40, 40));
		nextRankImage.setBounds(new Rectangle(getBounds().x + getBounds().width - 40f, expLabelBounds.y, 40, 40));
		
		experienceGainedLabel.setFixedWidth(getBounds().width);
		experienceGainedLabel.setPosition(getBounds().x, getBounds().y + getActualHeight() - 10);
	}

	@Override
	protected void renderComponent() {
		if (rankNamesEnabled) {
			currentRankNameLabel.render();
			nextRankNameLabel.render();
		}
		if (rankImagesEnabled) {
			rankImage.render();
			nextRankImage.render();
		}
		experienceGainedLabel.render();
		expBar.render();
		Shapes.draw(EQRectangle.builder()
				.bounds(expBarBounds)
				.color(Color.BLUE)
				.glowConfig(EQGlowConfig.builder().build())
				.thickness(1f)
				.build());
		currentRankEXPLabel.render();
		nextRankEXPLabel.render();
	}
	
	@Override
	public void dispose() {
		
	}

	public void setCurrentGainedExperience(long currentExperience) {
		experienceGainedLabel.setText("+" + StringUtils.formatBigNumber(currentExperience, 1) + " EXP");
	}

}

package de.instinct.eqfleet.menu.module.profile.model;

import com.badlogic.gdx.graphics.Color;

import de.instinct.eqfleet.menu.module.profile.ProfileModel;
import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.rendering.ui.component.Component;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.image.Image;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.HorizontalAlignment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.subtypes.PlainRectangularLoadingBar;
import de.instinct.eqlibgdxutils.rendering.ui.core.Border;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ExperienceSection extends Component {
	
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
		expBar.setBarColor(Color.BLUE);
		expBar.setCustomDescriptor("");
		Border expBarBorder = new Border();
		expBarBorder.setColor(Color.BLUE);
		expBarBorder.setSize(1f);
		expBar.setBorder(expBarBorder);
		
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
		
		rankImage = new Image();
		nextRankImage = new Image();
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
		currentRankNameLabel.setBounds(getBounds().x + (rankImagesEnabled ? 45 : 0), getBounds().y + getBounds().height - 10, getBounds().width - (rankImagesEnabled ? 90 : 0), 10);
		currentRankNameLabel.setText(ProfileModel.profile.getRank().getLabel());
		
		nextRankNameLabel.setBounds(getBounds().x + (rankImagesEnabled ? 45 : 0), getBounds().y + getBounds().height - 10, getBounds().width - (rankImagesEnabled ? 90 : 0), 10);
		nextRankNameLabel.setText(ProfileModel.profile.getRank().getNextRank().getLabel());
		
		expBar.setBounds(getBounds().x + (rankImagesEnabled ? 45 : 0), getBounds().y + getBounds().height - 10 - margin - 3, getBounds().width - (rankImagesEnabled ? 90 : 0), 10);
		expBar.setMaxValue(ProfileModel.profile.getRank().getNextRequiredExp() - ProfileModel.profile.getRank().getRequiredExp());
		expBar.setCurrentValue(ProfileModel.profile.getCurrentExp() - ProfileModel.profile.getRank().getRequiredExp());
		
		currentRankEXPLabel.setBounds(getBounds().x + (rankImagesEnabled ? 45 : 0), getBounds().y + getBounds().height - 10 - margin - 3 - 3 - margin, getBounds().width - (rankImagesEnabled ? 90 : 0), 10);
		currentRankEXPLabel.setText(StringUtils.formatBigNumber(ProfileModel.profile.getRank().getRequiredExp(), 1));
		nextRankEXPLabel.setBounds(getBounds().x + (rankImagesEnabled ? 45 : 0), getBounds().y + getBounds().height - 10 - margin - 3 - 3 - margin, getBounds().width - (rankImagesEnabled ? 90 : 0), 10);
		nextRankEXPLabel.setText(StringUtils.formatBigNumber(ProfileModel.profile.getRank().getNextRequiredExp(), 1));
		
		rankImage.updateTexture("ui/image/rank", ProfileModel.profile.getRank().getFileName());
		nextRankImage.updateTexture("ui/image/rank", ProfileModel.profile.getRank().getNextRank().getFileName());
		rankImage.setBounds(getBounds().x, getBounds().y + getBounds().height - 10 - margin - 3 - 3 - margin, 40, 40);
		nextRankImage.setBounds(getBounds().x + getBounds().width - 40f, getBounds().y + getBounds().height - 10 - margin - 3 - 3 - margin, 40, 40);
		
		experienceGainedLabel.setFixedWidth(getBounds().width);
		experienceGainedLabel.setPosition(getBounds().x, getBounds().y + calculateHeight() - 10);
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

package de.instinct.eqfleet.menu.module.profile.model;

import java.util.UUID;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqfleet.menu.module.profile.ProfileModel;
import de.instinct.eqlibgdxutils.rendering.ui.component.Component;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.image.Image;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.HorizontalAlignment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.subtypes.PlainRectangularLoadingBar;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.ComplexShapeType;
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
	
	private Image rankImage;
	private Image nextRankImage;
	
	private float margin = 20f;
	
	private String uuid;
	
	private boolean rankImagesEnabled;
	
	public ExperienceSection() {
		super();
		rankImagesEnabled = true;
		uuid = UUID.randomUUID().toString();
		currentRankNameLabel = new Label("");
		currentRankNameLabel.setColor(Color.LIGHT_GRAY);
		currentRankNameLabel.setType(FontType.SMALL);
		currentRankNameLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
		nextRankNameLabel = new Label("");
		nextRankNameLabel.setType(FontType.SMALL);
		nextRankNameLabel.setColor(Color.LIGHT_GRAY);
		nextRankNameLabel.setHorizontalAlignment(HorizontalAlignment.RIGHT);
		
		expBar = new PlainRectangularLoadingBar();
		expBar.setBar(TextureManager.createTexture(Color.BLUE));
		expBar.setBackground(TextureManager.createTexture(new Color(0f, 0f, 0f, 0f)));
		
		currentRankEXPLabel = new Label("");
		currentRankEXPLabel.setColor(Color.BLUE);
		currentRankEXPLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
		nextRankEXPLabel = new Label("");
		nextRankEXPLabel.setColor(Color.BLUE);
		nextRankEXPLabel.setHorizontalAlignment(HorizontalAlignment.RIGHT);
	}
	
	public void init(float x, float y, float width) {
		super.setBounds(new Rectangle(x, y, width, calculateHeight()));
		if (rankImagesEnabled) {
			nameLabelBounds = new Rectangle(getBounds().x + 45, getBounds().y + getBounds().height - 40, getBounds().width - 90, 40);
		} else {
			nameLabelBounds = new Rectangle(getBounds().x, getBounds().y + getBounds().height - 20, getBounds().width, 20);
		}
		
		expBarBounds = new Rectangle(getBounds().x, nameLabelBounds.y - margin - 10, getBounds().width, 20);
		expLabelBounds = new Rectangle(expBarBounds.x, expBarBounds.y - 10 - margin, getBounds().width, 20);
		TextureManager.createShapeTexture("profile_expOutline" + uuid, ComplexShapeType.ROUNDED_RECTANGLE, expBarBounds, Color.BLUE);
	}
	
	public float getActualHeight() {
		return calculateHeight();
	}
	
	@Override
	public float calculateHeight() {
		if (rankImagesEnabled) {
			return 40 + 20 + 20 + (margin * 2);
		} else {
			return 20 + 20 + 20 + (margin * 2);
		}
	}

	@Override
	public float calculateWidth() {
		return getBounds().width;
	}
	
	@Override
	protected void updateComponent() {
		currentRankNameLabel.setBounds(nameLabelBounds);
		currentRankNameLabel.setText(formatRankLabel(ProfileModel.profile.getRank().getLabel()));
		nextRankNameLabel.setBounds(nameLabelBounds);
		nextRankNameLabel.setText(formatRankLabel(ProfileModel.profile.getRank().getNextRank().getLabel()));
		expBar.setBounds(expBarBounds);
		expBar.setMaxValue(ProfileModel.profile.getRank().getNextRequiredExp() - ProfileModel.profile.getRank().getRequiredExp());
		expBar.setCurrentValue(ProfileModel.profile.getCurrentExp() - ProfileModel.profile.getRank().getRequiredExp());
		expBar.setCustomDescriptor(ProfileModel.profile.getCurrentExp() + "");
		currentRankEXPLabel.setBounds(expLabelBounds);
		currentRankEXPLabel.setText(ProfileModel.profile.getRank().getRequiredExp() + "");
		nextRankEXPLabel.setBounds(expLabelBounds);
		nextRankEXPLabel.setText(ProfileModel.profile.getRank().getNextRequiredExp() + "");
		
		rankImage = new Image(TextureManager.getTexture("ui/image/rank", ProfileModel.profile.getRank().getFileName()));
		rankImage.setBounds(new Rectangle(getBounds().x, nameLabelBounds.y, 40, 40));
		nextRankImage = new Image(TextureManager.getTexture("ui/image/rank", ProfileModel.profile.getRank().getNextRank().getFileName()));
		nextRankImage.setBounds(new Rectangle(getBounds().x + getBounds().width - 40f, nameLabelBounds.y, 40, 40));
	}

	@Override
	protected void renderComponent() {
		currentRankNameLabel.render();
		nextRankNameLabel.render();
		if (rankImagesEnabled) {
			rankImage.render();
			nextRankImage.render();
		}
		expBar.render();
		TextureManager.draw("profile_expOutline" + uuid);
		currentRankEXPLabel.render();
		nextRankEXPLabel.render();
	}
	
	private String formatRankLabel(String label) {
		int maxChars = 12;
		if (label.length() > maxChars) {
			return label.replaceFirst(" ", "\n");
		}
		return label;
	}
	
	@Override
	public void dispose() {
		
	}

}

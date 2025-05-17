package de.instinct.eqfleet.menu.module.main.tab.profile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.api.meta.dto.NameRegisterResponseCode;
import de.instinct.eqfleet.GlobalStaticData;
import de.instinct.eqfleet.menu.common.Renderer;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.textfield.LimitedInputField;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.textfield.model.TextfieldActionHandler;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.textfield.model.inputfilter.UsernameTexfieldInputFilter;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.HorizontalAlignment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.loadingbar.types.rectangular.subtypes.PlainRectangularLoadingBar;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.ComplexShapeType;

public class ProfileTabRenderer extends Renderer {
	
	private Label registrationLabel;
	private Label registrationResponseLabel;
	private ColorButton registrationConfirmButton;
	private LimitedInputField usernameTextField;
	
	private Label usernameLabel;
	
	private Label currentRankNameLabel;
	private Label nextRankNameLabel;
	private Label currentRankEXPLabel;
	private Label nextRankEXPLabel;
	private PlainRectangularLoadingBar expBar;
	
	private float horizontalMargin = 50f;

	@Override
	public void init() {
		registrationLabel = new Label("Choose a unique name");
		registrationResponseLabel = new Label("");
		registrationConfirmButton = new ColorButton("->");
		registrationConfirmButton.setAction(new Action() {
			
			@Override
			public void execute() {
				ProfileTab.register(usernameTextField.getContent());
			}
			
		});
		usernameTextField = new LimitedInputField();
		usernameTextField.setMaxChars(12);
		usernameTextField.setInputFilter(new UsernameTexfieldInputFilter());
		usernameTextField.setPopupMessage("Enter name");
		usernameTextField.setAction(new TextfieldActionHandler() {
			
			@Override
			public void confirmed() {
				if (usernameTextField.getForbiddenCharsMobile() == null) {
					ProfileTab.register(usernameTextField.getContent());
				} else {
					if (usernameTextField.getForbiddenCharsMobile().isEmpty()) {
						registrationResponseLabel.setText("");
						ProfileTab.register(usernameTextField.getContent());
					} else {
						registrationResponseLabel.setText("Invalid characters: " + usernameTextField.getForbiddenCharsMobile());
					}
				}
			}
			
		});
		
		usernameLabel = new Label("");
		usernameLabel.setType(FontType.LARGE);
		usernameLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
		
		Rectangle nameLabelBounds = new Rectangle(horizontalMargin, Gdx.graphics.getHeight() - 210, Gdx.graphics.getWidth() - (horizontalMargin * 2), 20);
		currentRankNameLabel = new Label("");
		currentRankNameLabel.setBounds(nameLabelBounds);
		currentRankNameLabel.setColor(Color.LIGHT_GRAY);
		currentRankNameLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
		nextRankNameLabel = new Label("");
		nextRankNameLabel.setBounds(nameLabelBounds);
		nextRankNameLabel.setColor(Color.LIGHT_GRAY);
		nextRankNameLabel.setHorizontalAlignment(HorizontalAlignment.RIGHT);
		
		Rectangle expBarBounds = new Rectangle(horizontalMargin, Gdx.graphics.getHeight() - 280, Gdx.graphics.getWidth() - (horizontalMargin * 2), 20);
		expBar = new PlainRectangularLoadingBar();
		expBar.setBar(TextureManager.createTexture(Color.BLUE));
		expBar.setBackground(TextureManager.createTexture(new Color(0f, 0f, 0f, 0f)));
		expBar.setBounds(expBarBounds);
		TextureManager.createShapeTexture("profile_expOutline", ComplexShapeType.ROUNDED_RECTANGLE, expBarBounds, Color.BLUE);
		
		Rectangle expLabelBounds = new Rectangle(horizontalMargin, Gdx.graphics.getHeight() - 300, Gdx.graphics.getWidth() - (horizontalMargin * 2), 20);
		currentRankEXPLabel = new Label("");
		currentRankEXPLabel.setBounds(expLabelBounds);
		currentRankEXPLabel.setColor(Color.BLUE);
		currentRankEXPLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
		nextRankEXPLabel = new Label("");
		nextRankEXPLabel.setBounds(expLabelBounds);
		nextRankEXPLabel.setColor(Color.BLUE);
		nextRankEXPLabel.setHorizontalAlignment(HorizontalAlignment.RIGHT);
	}

	@Override
	public void render() {
		if (GlobalStaticData.profile != null) {
			if (GlobalStaticData.profile.getUsername() == null) {
				renderRegistration();
			} else {
				renderProfile();
			}
		}
	}

	private void renderRegistration() {
		registrationLabel.setBounds(new Rectangle(0, Gdx.graphics.getHeight() / 2 + 50, Gdx.graphics.getWidth(), 30));
		registrationLabel.render();
		
		usernameTextField.setBounds(new Rectangle(Gdx.graphics.getWidth() - 176 - 130, Gdx.graphics.getHeight() / 2, 176, 30));
		usernameTextField.render();
		
		registrationConfirmButton.setFixedWidth(30);
		registrationConfirmButton.setFixedHeight(30);
		registrationConfirmButton.setPosition(Gdx.graphics.getWidth() - 130, Gdx.graphics.getHeight() / 2);
		registrationConfirmButton.render();
		
		registrationResponseLabel.setBounds(new Rectangle(0, Gdx.graphics.getHeight() / 2 - 50, Gdx.graphics.getWidth(), 30));
		registrationResponseLabel.render();
	}
	
	private void renderProfile() {
		usernameLabel.setText(GlobalStaticData.profile.getUsername());
		usernameLabel.setBounds(new Rectangle(horizontalMargin, Gdx.graphics.getHeight() - 150, Gdx.graphics.getWidth() - (horizontalMargin * 2), 50));
		usernameLabel.render();
		
		currentRankNameLabel.setText(GlobalStaticData.profile.getRank().getLabel());
		currentRankNameLabel.render();
		nextRankNameLabel.setText(GlobalStaticData.profile.getRank().getNextRank().getLabel());
		nextRankNameLabel.render();
		
		TextureManager.draw(TextureManager.getTexture("ui/image/rank", GlobalStaticData.profile.getRank().getFileName()), new Rectangle(horizontalMargin, Gdx.graphics.getHeight() - 250, 40, 40));
		TextureManager.draw(TextureManager.getTexture("ui/image/rank", GlobalStaticData.profile.getRank().getNextRank().getFileName()), new Rectangle(Gdx.graphics.getWidth() - horizontalMargin - 40, Gdx.graphics.getHeight() - 250, 40, 40));
		
		expBar.setMaxValue(GlobalStaticData.profile.getRank().getNextRequiredExp() - GlobalStaticData.profile.getRank().getRequiredExp());
		expBar.setCurrentValue(GlobalStaticData.profile.getCurrentExp() - GlobalStaticData.profile.getRank().getRequiredExp());
		expBar.setCustomDescriptor(GlobalStaticData.profile.getCurrentExp() + "");
		expBar.render();
		TextureManager.draw("profile_expOutline");
		
		currentRankEXPLabel.setText(GlobalStaticData.profile.getRank().getRequiredExp() + "");
		currentRankEXPLabel.render();
		nextRankEXPLabel.setText(GlobalStaticData.profile.getRank().getNextRequiredExp() + "");
		nextRankEXPLabel.render();
	}

	@Override
	public void dispose() {
		
	}

	public void processNameRegisterResponseCode(NameRegisterResponseCode nameRegisterReponseCode) {
		switch (nameRegisterReponseCode) {
		case BAD_TOKEN:
			registrationResponseLabel.setText("Invalid authkey. Please restart the app.");
			break;
		case USERNAME_TAKEN:
			registrationResponseLabel.setText("Username already taken...");
			break;
		default:
			break;
		}
	}

}

package de.instinct.eqfleet.menu.module.profile;

import com.badlogic.gdx.math.Rectangle;

import de.instinct.api.meta.dto.NameRegisterResponseCode;
import de.instinct.eqfleet.menu.common.architecture.BaseModuleRenderer;
import de.instinct.eqfleet.menu.main.Menu;
import de.instinct.eqfleet.menu.main.MenuModel;
import de.instinct.eqfleet.menu.module.profile.message.RegisterMessage;
import de.instinct.eqfleet.menu.module.profile.model.ExperienceSection;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.textfield.LimitedInputField;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.textfield.model.TextfieldActionHandler;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.textfield.model.inputfilter.UsernameTexfieldInputFilter;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.HorizontalAlignment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;

public class ProfileRenderer extends BaseModuleRenderer {
	
	private Label registrationLabel;
	private Label registrationResponseLabel;
	private ColorButton registrationConfirmButton;
	private LimitedInputField usernameTextField;
	
	private Label usernameLabel;
	
	private float margin = 20f;
	
	private Rectangle registrationLabelBounds;
	private Rectangle usernameTextFieldBounds;
	private Rectangle registrationConfirmButtonBounds;
	private Rectangle registrationResponseLabelBounds;
	
	private Rectangle usernameLabelBounds;
	
	private ExperienceSection experienceSection;

	public ProfileRenderer() {
		registrationLabel = new Label("Choose a unique name");
		registrationResponseLabel = new Label("");
		registrationConfirmButton = new ColorButton("->");
		registrationConfirmButton.setAction(new Action() {
			
			@Override
			public void execute() {
				register(usernameTextField.getContent());
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
					register(usernameTextField.getContent());
				} else {
					if (usernameTextField.getForbiddenCharsMobile().isEmpty()) {
						registrationResponseLabel.setText("");
						register(usernameTextField.getContent());
					} else {
						registrationResponseLabel.setText("Invalid characters: " + usernameTextField.getForbiddenCharsMobile());
					}
				}
			}
			
		});
		
		usernameLabel = new Label("");
		usernameLabel.setType(FontType.LARGE);
		usernameLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
		
		experienceSection = new ExperienceSection();
	}
	
	private void register(String content) {
		Menu.queue(RegisterMessage.builder()
				.username(content)
				.build());
	}

	@Override
	public void render() {
		if (ProfileModel.profile != null) {
			if (ProfileModel.profile.getUsername() == null) {
				renderRegistration();
			} else {
				renderProfile();
			}
		}
	}

	@Override
	public void reload() {
		registrationLabelBounds = new Rectangle(MenuModel.moduleBounds.x, ((MenuModel.moduleBounds.y + MenuModel.moduleBounds.height) / 2) + 50, MenuModel.moduleBounds.width, 30);
		usernameTextFieldBounds = new Rectangle(MenuModel.moduleBounds.x + (MenuModel.moduleBounds.width / 2) - 88 - 15, (MenuModel.moduleBounds.y + MenuModel.moduleBounds.height) / 2, 176, 30);
		registrationConfirmButtonBounds = new Rectangle(MenuModel.moduleBounds.x + (MenuModel.moduleBounds.width / 2) + 88 - 15, (MenuModel.moduleBounds.y + MenuModel.moduleBounds.height) / 2, 30, 30);
		registrationResponseLabelBounds = new Rectangle(MenuModel.moduleBounds.x, ((MenuModel.moduleBounds.y + MenuModel.moduleBounds.height) / 2) - 50, MenuModel.moduleBounds.width, 30);
		
		usernameLabelBounds = new Rectangle(MenuModel.moduleBounds.x + margin, MenuModel.moduleBounds.y + MenuModel.moduleBounds.height - margin - 30, MenuModel.moduleBounds.width - (margin * 2), 30);
		experienceSection.init(MenuModel.moduleBounds.x + margin, usernameLabelBounds.y - experienceSection.getActualHeight() - margin, MenuModel.moduleBounds.width - (margin * 2));
	}

	private void renderRegistration() {
		if (ProfileModel.nameRegisterResponseCode != null) {
			processNameRegisterResponseCode(ProfileModel.nameRegisterResponseCode);
		}
		
		registrationLabel.setBounds(registrationLabelBounds);
		registrationLabel.render();
		
		usernameTextField.setBounds(usernameTextFieldBounds);
		usernameTextField.render();
		
		registrationConfirmButton.setFixedWidth(registrationConfirmButtonBounds.width);
		registrationConfirmButton.setFixedHeight(registrationConfirmButtonBounds.height);
		registrationConfirmButton.setPosition(registrationConfirmButtonBounds.x, registrationConfirmButtonBounds.y);
		registrationConfirmButton.render();
		
		registrationResponseLabel.setBounds(registrationResponseLabelBounds);
		registrationResponseLabel.render();
	}
	
	private void renderProfile() {
		usernameLabel.setText(ProfileModel.profile.getUsername());
		usernameLabel.setBounds(usernameLabelBounds);
		usernameLabel.render();
		
		experienceSection.render();
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

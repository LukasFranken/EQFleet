package de.instinct.eqfleet.menu.module.main.tab.profile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqfleet.menu.common.Renderer;
import de.instinct.eqfleet.net.GlobalStaticData;
import de.instinct.eqfleet.net.meta.dto.NameRegisterResponseCode;
import de.instinct.eqlibgdxutils.generic.Action;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.textfield.LimitedInputField;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.textfield.model.TextfieldActionHandler;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.textfield.model.inputfilter.UsernameTexfieldInputFilter;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontUtil;

public class ProfileTabRenderer extends Renderer {
	
	private Label registrationLabel;
	private Label registrationResponseLabel;
	private ColorButton registrationConfirmButton;
	private LimitedInputField usernameTextField;

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
	}

	@Override
	public void render() {
		if (GlobalStaticData.profile.getUsername() == null) {
			renderRegistration();
		} else {
			renderProfile();
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
		FontUtil.drawLabel("(under construction)", new Rectangle(0, Gdx.graphics.getHeight() / 2 - 50, Gdx.graphics.getWidth(), 30));
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

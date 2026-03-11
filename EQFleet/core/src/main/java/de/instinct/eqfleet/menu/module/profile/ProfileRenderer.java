package de.instinct.eqfleet.menu.module.profile;

import java.util.ArrayList;
import java.util.List;

import de.instinct.api.meta.dto.NameRegisterResponseCode;
import de.instinct.eqfleet.menu.common.architecture.BaseModuleRenderer;
import de.instinct.eqfleet.menu.common.components.DefaultButtonFactory;
import de.instinct.eqfleet.menu.main.MenuModel;
import de.instinct.eqfleet.menu.module.profile.inventory.InventoryRenderer;
import de.instinct.eqfleet.menu.module.profile.message.types.RegisterMessage;
import de.instinct.eqfleet.menu.module.profile.model.TabButtonBar;
import de.instinct.eqfleet.menu.module.profile.model.TabOption;
import de.instinct.eqfleet.menu.module.profile.overview.OverviewRenderer;
import de.instinct.eqfleet.menu.module.profile.statistics.StatisticsRenderer;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.textfield.LimitedInputField;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.textfield.model.TextfieldActionHandler;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.textfield.model.inputfilter.UsernameTexfieldInputFilter;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;

public class ProfileRenderer extends BaseModuleRenderer {
	
	private Label registrationLabel;
	private Label registrationResponseLabel;
	private ColorButton registrationConfirmButton;
	private LimitedInputField usernameTextField;
	
	private TabButtonBar tabButtonBar;
	
	private OverviewRenderer overviewRenderer;
	private StatisticsRenderer statisticsRenderer;
	private InventoryRenderer inventoryRenderer;

	public ProfileRenderer() {
		registrationLabel = new Label("Choose a unique name");
		registrationResponseLabel = new Label("");
		registrationConfirmButton = DefaultButtonFactory.colorButton("->", () -> {
			register(usernameTextField.getContent());
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
		
		List<TabOption> tabOptions = new ArrayList<>();
		tabOptions.add(TabOption.builder()
				.label("Overview")
				.build());
		tabOptions.add(TabOption.builder()
				.label("Statistics")
				.build());
		tabOptions.add(TabOption.builder()
				.label("Inventory")
				.build());
		tabButtonBar = new TabButtonBar(tabOptions);
		
		overviewRenderer = new OverviewRenderer();
		statisticsRenderer = new StatisticsRenderer();
		inventoryRenderer = new InventoryRenderer();
	}
	
	private void register(String content) {
		if (!content.isEmpty()) ProfileModel.messageQueue.add(RegisterMessage.builder()
				.username(content)
				.build());
	}

	@Override
	public void init() {
		overviewRenderer.init();
		statisticsRenderer.init();
		inventoryRenderer.init();
	}
	
	@Override
	public void update() {
		if (ProfileModel.profile != null) {
			if (ProfileModel.profile.getUsername() == null) {
				updateRegistration();
			} else {
				switch (tabButtonBar.getSelectedOption().getLabel()) {
				case "Overview":
					overviewRenderer.update();
					break;
				case "Statistics":
					statisticsRenderer.update();
					break;
				case "Inventory":
					inventoryRenderer.update();
					break;
				}
			}
		}
		overviewRenderer.update();
		statisticsRenderer.update();
		inventoryRenderer.update();
	}
	
	private void updateRegistration() {
		if (ProfileModel.nameRegisterResponseCode != null) {
			processNameRegisterResponseCode(ProfileModel.nameRegisterResponseCode);
		}
		registrationLabel.setBounds(MenuModel.moduleBounds.x, ((MenuModel.moduleBounds.y + MenuModel.moduleBounds.height) / 2) + 50, MenuModel.moduleBounds.width, 30);
		usernameTextField.setBounds(MenuModel.moduleBounds.x + (MenuModel.moduleBounds.width / 2) - 88 - 15, (MenuModel.moduleBounds.y + MenuModel.moduleBounds.height) / 2, 176, 30);
		registrationConfirmButton.setBounds(MenuModel.moduleBounds.x + (MenuModel.moduleBounds.width / 2) + 92 - 15, (MenuModel.moduleBounds.y + MenuModel.moduleBounds.height) / 2, 30, 30);
		registrationResponseLabel.setBounds(MenuModel.moduleBounds.x, ((MenuModel.moduleBounds.y + MenuModel.moduleBounds.height) / 2) - 50, MenuModel.moduleBounds.width, 30);
	}
	
	private void processNameRegisterResponseCode(NameRegisterResponseCode nameRegisterReponseCode) {
		switch (nameRegisterReponseCode) {
		case BAD_TOKEN:
			registrationResponseLabel.setText("Invalid authkey. Please restart the app.");
			usernameTextField.setContent("");
			break;
		case USERNAME_TAKEN:
			registrationResponseLabel.setText("Username already taken...");
			usernameTextField.setContent("");
			break;
		default:
			break;
		}
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

	private void renderRegistration() {
		registrationLabel.render();
		usernameTextField.render();
		registrationConfirmButton.render();
		registrationResponseLabel.render();
	}
	
	private void renderProfile() {
		switch (tabButtonBar.getSelectedOption().getLabel()) {
		case "Overview":
			overviewRenderer.render();
			break;
		case "Statistics":
			statisticsRenderer.render();
			break;
		case "Inventory":
			inventoryRenderer.render();
			break;
		}
		tabButtonBar.setBounds(MenuModel.moduleBounds.x, MenuModel.moduleBounds.y, MenuModel.moduleBounds.width, 40);
		tabButtonBar.render();
	}

	@Override
	public void dispose() {
		overviewRenderer.dispose();
		statisticsRenderer.dispose();
		inventoryRenderer.dispose();
		
		registrationLabel.dispose();
		usernameTextField.dispose();
		registrationConfirmButton.dispose();
		registrationResponseLabel.dispose();
	}

}

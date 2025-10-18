package de.instinct.eqfleet.menu.module.profile;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.api.meta.dto.NameRegisterResponseCode;
import de.instinct.eqfleet.menu.common.architecture.BaseModuleRenderer;
import de.instinct.eqfleet.menu.common.components.DefaultButtonFactory;
import de.instinct.eqfleet.menu.common.components.label.DefaultLabelFactory;
import de.instinct.eqfleet.menu.main.Menu;
import de.instinct.eqfleet.menu.main.MenuModel;
import de.instinct.eqfleet.menu.module.profile.inventory.InventoryRenderer;
import de.instinct.eqfleet.menu.module.profile.message.RegisterMessage;
import de.instinct.eqfleet.menu.module.profile.model.ExperienceSection;
import de.instinct.eqfleet.menu.module.profile.model.TabButtonBar;
import de.instinct.eqfleet.menu.module.profile.model.TabOption;
import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.button.ColorButton;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.textfield.LimitedInputField;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.textfield.model.TextfieldActionHandler;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.textfield.model.inputfilter.UsernameTexfieldInputFilter;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.HorizontalAlignment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.container.list.ElementStack;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;
import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.Shapes;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQRectangle;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.utility.EQGlowConfig;

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
	private Rectangle commanderSectionBounds;
	private Rectangle usernameLabelBounds;
	private Rectangle experienceSectionBounds;
	
	private ExperienceSection experienceSection;
	
	private TabButtonBar tabButtonBar;
	
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
		
		usernameLabel = new Label("");
		usernameLabel.setType(FontType.LARGE);
		usernameLabel.setHorizontalAlignment(HorizontalAlignment.CENTER);
		
		experienceSection = new ExperienceSection();
		
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
		
		inventoryRenderer = new InventoryRenderer();
	}
	
	private void register(String content) {
		if (!content.isEmpty()) Menu.queue(RegisterMessage.builder()
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
		registrationConfirmButtonBounds = new Rectangle(MenuModel.moduleBounds.x + (MenuModel.moduleBounds.width / 2) + 92 - 15, (MenuModel.moduleBounds.y + MenuModel.moduleBounds.height) / 2, 30, 30);
		registrationResponseLabelBounds = new Rectangle(MenuModel.moduleBounds.x, ((MenuModel.moduleBounds.y + MenuModel.moduleBounds.height) / 2) - 50, MenuModel.moduleBounds.width, 30);
		
		usernameLabelBounds = new Rectangle(MenuModel.moduleBounds.x + margin, MenuModel.moduleBounds.y + MenuModel.moduleBounds.height - margin - 30, MenuModel.moduleBounds.width - (margin * 2), 30);
		experienceSectionBounds = new Rectangle(MenuModel.moduleBounds.x + margin, usernameLabelBounds.y - experienceSection.getActualHeight() - margin, MenuModel.moduleBounds.width - (margin * 2), experienceSection.getActualHeight());
		experienceSection.init(experienceSectionBounds.x + 20, experienceSectionBounds.y - 10, experienceSectionBounds.width - 40);
		commanderSectionBounds = new Rectangle(MenuModel.moduleBounds.x + margin, experienceSection.getBounds().y - 110 - margin, MenuModel.moduleBounds.width - (margin * 2), 110);
		inventoryRenderer.reload();
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
		switch (tabButtonBar.getSelectedOption().getLabel()) {
		case "Overview":
			renderOverview();
			break;
		case "Statistics":
			renderStatistics();
			break;
		case "Inventory":
			inventoryRenderer.render();
			break;
		}
		tabButtonBar.setBounds(new Rectangle(MenuModel.moduleBounds.x, MenuModel.moduleBounds.y, MenuModel.moduleBounds.width, 40));
		tabButtonBar.render();
	}

	private void renderOverview() {
		usernameLabel.setText(ProfileModel.profile.getUsername());
		usernameLabel.setBounds(usernameLabelBounds);
		usernameLabel.render();
		
		Shapes.draw(EQRectangle.builder()
				.bounds(experienceSectionBounds)
				.color(new Color(SkinManager.skinColor))
				.glowConfig(EQGlowConfig.builder().build())
				.thickness(2f)
				.build());
		experienceSection.render();
		
		if (ProfileModel.commanderData != null) {
			Shapes.draw(EQRectangle.builder()
					.bounds(commanderSectionBounds)
					.color(new Color(SkinManager.skinColor))
					.glowConfig(EQGlowConfig.builder().build())
					.thickness(2f)
					.build());
			float inModuleStackMargin = 20f;
			ElementStack maxCPLabelStack = DefaultLabelFactory.createLabelStack("Max CP", StringUtils.format(ProfileModel.commanderData.getMaxCommandPoints(), 0), commanderSectionBounds.width - (inModuleStackMargin * 2));
			maxCPLabelStack.setPosition(commanderSectionBounds.x + inModuleStackMargin, commanderSectionBounds.y + commanderSectionBounds.height - 30 - 10);
			maxCPLabelStack.setFixedHeight(30f);
			maxCPLabelStack.render();
			
			ElementStack startCPLabelStack = DefaultLabelFactory.createLabelStack("Start CP", StringUtils.format(ProfileModel.commanderData.getStartCommandPoints(), 0), commanderSectionBounds.width - (inModuleStackMargin * 2));
			startCPLabelStack.setPosition(commanderSectionBounds.x + inModuleStackMargin, commanderSectionBounds.y + commanderSectionBounds.height - 60 - 10);
			startCPLabelStack.setFixedHeight(30f);
			startCPLabelStack.render();
			
			ElementStack cpPerSecLabelStack = DefaultLabelFactory.createLabelStack("CP / sec", StringUtils.format(ProfileModel.commanderData.getCommandPointsGenerationSpeed(), 2), commanderSectionBounds.width - (inModuleStackMargin * 2));
			cpPerSecLabelStack.setPosition(commanderSectionBounds.x + inModuleStackMargin, commanderSectionBounds.y + commanderSectionBounds.height - 90 - 10);
			cpPerSecLabelStack.setFixedHeight(30f);
			cpPerSecLabelStack.render();
		}
	}
	
	private void renderStatistics() {
		
	}

	@Override
	public void dispose() {
		inventoryRenderer.dispose();
	}

	public void processNameRegisterResponseCode(NameRegisterResponseCode nameRegisterReponseCode) {
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

}

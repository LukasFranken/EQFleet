package de.instinct.eqfleet.menu.module.profile.overview;

import de.instinct.eqfleet.menu.common.architecture.BaseModuleRenderer;
import de.instinct.eqfleet.menu.main.MenuModel;
import de.instinct.eqfleet.menu.module.profile.ProfileModel;
import de.instinct.eqfleet.menu.module.profile.model.CommanderSection;
import de.instinct.eqfleet.menu.module.profile.model.ExperienceSection;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.HorizontalAlignment;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.label.Label;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontType;

public class OverviewRenderer extends BaseModuleRenderer {
	
	private float margin = 20f;
	
	private Label usernameLabel;
	private ExperienceSection experienceSection;
	private CommanderSection commanderSection;

	@Override
	public void init() {
		usernameLabel = new Label("");
		usernameLabel.setType(FontType.LARGE);
		usernameLabel.setHorizontalAlignment(HorizontalAlignment.CENTER);
		
		experienceSection = new ExperienceSection();
		commanderSection = new CommanderSection();
	}

	@Override
	public void update() {
		usernameLabel.setText(ProfileModel.profile.getUsername());
		usernameLabel.setBounds(MenuModel.moduleBounds.x + margin, MenuModel.moduleBounds.y + MenuModel.moduleBounds.height - margin - 30, MenuModel.moduleBounds.width - (margin * 2), 30);
		experienceSection.setBounds(MenuModel.moduleBounds.x + margin, usernameLabel.getBounds().y - experienceSection.calculateHeight() - margin, MenuModel.moduleBounds.width - (margin * 2), experienceSection.calculateHeight());
		commanderSection.setBounds(MenuModel.moduleBounds.x + margin, experienceSection.getBounds().y - commanderSection.calculateHeight() - margin, MenuModel.moduleBounds.width - (margin * 2), commanderSection.calculateHeight());
	}

	@Override
	public void render() {
		usernameLabel.render();
		experienceSection.render();
		commanderSection.render();
	}

	@Override
	public void dispose() {
		usernameLabel.dispose();
		experienceSection.dispose();
		commanderSection.dispose();
	}

}

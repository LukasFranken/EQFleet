package de.instinct.eqfleet.menu.module.main;

import de.instinct.eqfleet.menu.MenuTab;
import de.instinct.eqfleet.menu.common.Logic;

public class MainMenuLogic extends Logic {

	@Override
	public void init() {
		MainMenu.currentTab = MenuTab.PROFILE;
	}

	@Override
	public void update() {
		
	}

}

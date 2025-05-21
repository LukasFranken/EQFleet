package de.instinct.eqfleet.menu;

public enum MenuTab {
	
	PLAY("DEPLOY"),
	LOADOUT("LOADOUT"),
	SETTINGS("SETTINGS"),
	PROFILE("PROFILE"),
	INVENTORY("INVENTORY"),
	STATION("STATION");
	
	String label;
	
	MenuTab(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return this.label;
	}

}

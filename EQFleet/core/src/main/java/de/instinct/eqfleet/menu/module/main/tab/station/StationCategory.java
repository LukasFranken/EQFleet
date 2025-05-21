package de.instinct.eqfleet.menu.module.main.tab.station;

public enum StationCategory {
	
	SHOP("SHOP"),
	CASINO("CASINO"),
	WORKSHOP("WORKSHOP");
	
	String label;
	
	StationCategory(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return this.label;
	}

}

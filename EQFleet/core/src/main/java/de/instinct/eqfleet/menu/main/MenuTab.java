package de.instinct.eqfleet.menu.main;

import de.instinct.eqlibgdxutils.rendering.ui.skin.SkinColor;

public enum MenuTab {
	
	FLAGSHIP(SkinColor.RED),
	STATION(SkinColor.CYAN),
	COMMANDER(SkinColor.YELLOW);

	private SkinColor color;
	
	MenuTab(SkinColor color) {
		this.color = color;
	}
	
	public SkinColor getColor() {
		return color;
	}

}

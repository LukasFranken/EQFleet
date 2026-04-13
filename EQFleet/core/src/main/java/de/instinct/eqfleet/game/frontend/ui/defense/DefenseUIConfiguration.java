package de.instinct.eqfleet.game.frontend.ui.defense;

import com.badlogic.gdx.graphics.Color;

import de.instinct.engine.fleet.entity.unit.component.data.types.ShieldType;

public class DefenseUIConfiguration {
	
	public static Color barColor = new Color(0.5f, 0.5f, 0.5f, 1f);
	public static Color armorColor = new Color(1f, 0.5f, 0f, 1f);
	
	public static Color getShieldColor(ShieldType type) {
		switch (type) {
		case GRAVITON:
			return new Color(0f, 0.8f, 0.8f, 1f);
		case PLASMA:
			return new Color(0f, 0.8f, 0.8f, 1f);
		case NULLPOINT:
			return new Color(0.8f, 0f, 0.8f, 1f);
		}
		return null;
	}

}

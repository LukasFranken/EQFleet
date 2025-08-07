package de.instinct.engine.combat;

import de.instinct.engine.model.ship.Defense;

public class DefenseProcessor {
	
	public void updateDefense(Defense defense, float delta) {
		if (defense != null) {
			defense.currentShield += defense.shieldRegenerationSpeed * ((float) delta / 1000f);
			if (defense.currentShield >= defense.shield) {
				defense.currentShield = defense.shield;
			}
		}
	}

}

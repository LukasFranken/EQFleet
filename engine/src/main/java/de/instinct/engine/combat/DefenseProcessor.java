package de.instinct.engine.combat;

import de.instinct.engine.combat.unit.Unit;
import de.instinct.engine.combat.unit.component.Shield;

public class DefenseProcessor {
	
	public void updateDefense(Unit unit, float delta) {
		if (unit.hull.currentStrength < unit.hull.data.strength) {
			unit.hull.currentStrength += unit.hull.data.repairSpeed * (delta / 1000f);
			if (unit.hull.currentStrength > unit.hull.data.strength) {
				unit.hull.currentStrength = unit.hull.data.strength;
			}
		}
		for (Shield shield : unit.shields) {
			if (shield.currentStrength < shield.data.strength) {
				shield.currentStrength += shield.data.generation * (delta / 1000f);
				if (shield.currentStrength > shield.data.strength) {
					shield.currentStrength = shield.data.strength;
				}
			}
		}
	}

}

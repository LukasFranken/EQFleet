package de.instinct.engine.combat;

import de.instinct.engine.combat.unit.Unit;
import de.instinct.engine.combat.unit.component.Shield;
import de.instinct.engine.model.GameState;
import de.instinct.engine.stats.StatCollector;
import de.instinct.engine.stats.model.PlayerStatistic;
import de.instinct.engine.stats.model.unit.UnitStatistic;

public class DefenseProcessor {
	
	public void updateDefense(GameState state, Unit unit, float delta) {
		PlayerStatistic originUnitOwnerStatistic = StatCollector.getPlayer(state.gameUUID, unit.ownerId);
		UnitStatistic unitStat = originUnitOwnerStatistic.getUnit(unit.data.model);
		if (unit.hull.currentStrength < unit.hull.data.strength) {
			float hullRepaired = unit.hull.data.repairSpeed * (delta / 1000f);
			unit.hull.currentStrength += hullRepaired;
			if (unit.hull.currentStrength > unit.hull.data.strength) {
				hullRepaired = unit.hull.data.strength - (unit.hull.currentStrength - hullRepaired);
				unit.hull.currentStrength = unit.hull.data.strength;
			}
			if (unitStat.getHullStatistic() != null) unitStat.getHullStatistic().setHullRepaired(unitStat.getHullStatistic().getHullRepaired() + hullRepaired);
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

package de.instinct.engine.fleet.entity.unit.component;

import de.instinct.engine.fleet.data.FleetGameState;
import de.instinct.engine.fleet.entity.unit.Unit;
import de.instinct.engine.fleet.stats.StatCollector;
import de.instinct.engine.fleet.stats.model.PlayerStatistic;
import de.instinct.engine.fleet.stats.model.unit.UnitStatistic;

public class DefenseProcessor {
	
	public void updateDefense(FleetGameState state, Unit unit, double delta) {
		PlayerStatistic originUnitOwnerStatistic = StatCollector.getPlayer(state.gameUUID, unit.ownerId);
		UnitStatistic unitStat = originUnitOwnerStatistic.getUnit(unit.data.model);
		if (unit.currentHull < unit.data.hullStrength) {
			double hullRepaired = unit.data.hullRepairSpeed * (delta / 1000D);
			unit.currentHull += hullRepaired;
			if (unit.currentHull > unit.data.hullStrength) {
				hullRepaired = unit.data.hullStrength - (unit.currentHull - hullRepaired);
				unit.currentHull = unit.data.hullStrength;
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

package de.instinct.engine.stats.model;

import java.util.List;

import de.instinct.engine.stats.model.unit.ShipStatistic;
import de.instinct.engine.stats.model.unit.TurretStatistic;
import lombok.Data;

@Data
public class PlayerStatistic {
	
	private int playerId;
	private int cpUsed;
	private double resourcesUsed;
	private double atpGained;
	private List<ShipStatistic> shipStatistics;
	private List<TurretStatistic> turretStatistics;
	
	public ShipStatistic getShip(String model) {
		for (ShipStatistic ship : shipStatistics) {
			if (ship.getModel().contentEquals(model)) {
				return ship;
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("###################\n");
		sb.append("#     Player " + playerId + "    #\n");
		sb.append("###################\n");
		sb.append("CP Used: " + cpUsed + "\n");
		sb.append("Resources used: " + resourcesUsed + "\n");
		sb.append("ATP gained: " + atpGained + "\n");
		sb.append("#      SHIPS      #\n");
		for (ShipStatistic ss : shipStatistics) {
			sb.append(ss.toString()).append("\n");
		}
		sb.append("#     TURRETS     #\n");
		for (TurretStatistic ss : turretStatistics) {
			sb.append(ss.toString()).append("\n");
		}
		return sb.toString();
	}

}
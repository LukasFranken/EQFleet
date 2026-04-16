package de.instinct.engine.mining.entity.ship.cargo;

import de.instinct.engine.mining.entity.asteroid.ResourceType;
import de.instinct.engine.mining.entity.ship.MiningPlayerShip;

public class MiningCargoProcessor {
	
	public void addCargo(MiningPlayerShip ship, ResourceType resourceType, float amount) {
		if (ship.cargo.capacity - calculateTotalCargoUsage(ship) <= 0) return;
		if (ship.cargo.capacity - calculateTotalCargoUsage(ship) < amount) {
			amount = ship.cargo.capacity - calculateTotalCargoUsage(ship);
		}
		for (CargoItem item : ship.cargo.items) {
			if (item.resourceType == resourceType) {
				item.amount += amount;
				return;
			}
		}
		CargoItem newItem = new CargoItem();
		newItem.resourceType = resourceType;
		newItem.amount = amount;
		ship.cargo.items.add(newItem);
	}
	
	private float calculateTotalCargoUsage(MiningPlayerShip ship) {
		float weight = 0f;
		for (CargoItem item : ship.cargo.items) {
			weight += item.amount;
		}
		return weight;
	}

}

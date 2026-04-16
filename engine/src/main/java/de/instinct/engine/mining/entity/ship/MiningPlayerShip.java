package de.instinct.engine.mining.entity.ship;

import de.instinct.engine.core.entity.Entity;
import de.instinct.engine.mining.entity.ship.cargo.MiningCargo;
import de.instinct.engine.mining.entity.ship.core.MiningCore;
import de.instinct.engine.mining.entity.ship.thruster.MiningThruster;
import de.instinct.engine.mining.entity.ship.weapon.MiningWeapon;
import lombok.ToString;

@ToString(callSuper = true)
public class MiningPlayerShip extends Entity {
	
	public int ownerId;
	public MiningCore core;
	public MiningWeapon weapon;
	public MiningThruster thruster;
	public MiningCargo cargo;
	public boolean recalled;

}

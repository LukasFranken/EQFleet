package de.instinct.engine.model;

import java.util.List;

import de.instinct.engine.model.ship.components.ShieldData;
import de.instinct.engine.model.ship.components.WeaponData;
import de.instinct.engine.model.ship.components.types.HullType;
import lombok.ToString;

@ToString(callSuper = true)
public class UnitData {
	
	public String model;
	public double resourceCost;
	
	public HullType hullType;
	public double hullStrength;
	public double hullRepairSpeed;
	
	public List<WeaponData> weapons;
	public List<ShieldData> shields;

}

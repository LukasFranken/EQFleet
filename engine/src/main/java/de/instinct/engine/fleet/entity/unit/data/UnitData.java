package de.instinct.engine.fleet.entity.unit.data;

import java.util.List;

import de.instinct.engine.fleet.entity.unit.component.data.ShieldData;
import de.instinct.engine.fleet.entity.unit.component.data.WeaponData;
import de.instinct.engine.fleet.entity.unit.component.data.types.HullType;
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

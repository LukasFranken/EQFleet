package de.instinct.engine.fleet.entity.unit.data;

import java.util.List;

import de.instinct.engine.fleet.entity.unit.component.data.ShieldData;
import de.instinct.engine.fleet.entity.unit.component.data.WeaponData;
import de.instinct.engine.fleet.entity.unit.component.data.types.HullType;
import lombok.ToString;

@ToString(callSuper = true)
public class UnitData {
	
	public String model;
	public float resourceCost;
	
	public HullType hullType;
	public float hullStrength;
	public float hullRepairSpeed;
	
	public List<WeaponData> weapons;
	public List<ShieldData> shields;

}

package de.instinct.engine.model;

import java.util.List;

import de.instinct.engine.model.ship.components.HullData;
import de.instinct.engine.model.ship.components.ShieldData;
import de.instinct.engine.model.ship.components.WeaponData;
import lombok.ToString;

@ToString(callSuper = true)
public class UnitData {
	
	public String model;
	public int cpCost;
	public float resourceCost;
	
	public HullData hull;
	public List<WeaponData> weapons;
	public List<ShieldData> shields;

}

package de.instinct.engine.fleet.entity.unit;

import java.util.List;

import de.instinct.engine.core.entity.Entity;
import de.instinct.engine.fleet.entity.unit.component.Shield;
import de.instinct.engine.fleet.entity.unit.component.Weapon;
import de.instinct.engine.fleet.entity.unit.data.UnitData;
import lombok.ToString;

@ToString(callSuper = true)
public class Unit extends Entity {

	public int originPlanetId;
	public double currentHull;
	public List<Weapon> weapons;
	public List<Shield> shields;
	public UnitData data;

}

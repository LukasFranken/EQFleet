package de.instinct.engine.combat.unit;

import java.util.List;

import de.instinct.engine.combat.unit.component.Hull;
import de.instinct.engine.combat.unit.component.Shield;
import de.instinct.engine.combat.unit.component.Weapon;
import de.instinct.engine.entity.Entity;
import de.instinct.engine.model.UnitData;
import lombok.ToString;

@ToString(callSuper = true)
public class Unit extends Entity {

	public int originPlanetId;
	public Hull hull;
	public List<Weapon> weapons;
	public List<Shield> shields;
	public UnitData data;

}

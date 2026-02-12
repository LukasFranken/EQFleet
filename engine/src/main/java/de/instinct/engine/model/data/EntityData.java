package de.instinct.engine.model.data;

import java.util.List;

import de.instinct.engine.combat.Ship;
import de.instinct.engine.combat.Turret;
import de.instinct.engine.combat.projectile.Projectile;
import de.instinct.engine.model.planet.Planet;
import lombok.ToString;

@ToString
public class EntityData {

	public int entityCounter;
	public List<Planet> planets;
	public List<Ship> ships;
	public List<Turret> turrets;
	public List<Projectile> projectiles;
	
}

package de.instinct.engine.mining.entity.data;

import java.util.List;

import de.instinct.engine.core.entity.data.EntityData;
import de.instinct.engine.mining.entity.asteroid.Asteroid;
import de.instinct.engine.mining.entity.projectile.MiningProjectile;
import de.instinct.engine.mining.entity.ship.MiningPlayerShip;

public class MiningEntityData extends EntityData {
	
	public List<MiningPlayerShip> playerShips;
	public List<MiningProjectile> projectiles;
	public List<Asteroid> asteroids;

}

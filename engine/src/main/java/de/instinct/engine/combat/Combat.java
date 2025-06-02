package de.instinct.engine.combat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.ToString;

@ToString
public class Combat {
	
	public List<Integer> planetIds;
	public List<Ship> ships;
	public List<Projectile> projectiles;
	public long startTime;
	public long elapsedTime;
	
	public Combat clone() {
		Combat clone = new Combat();
		clone.planetIds = this.planetIds.stream()
				.map(Integer::valueOf)
				.collect(Collectors.toCollection(ArrayList::new));
		clone.ships = this.ships.stream()
				.map(Ship::clone)
				.collect(Collectors.toCollection(ArrayList::new));
		clone.projectiles = this.projectiles.stream()
				.map(Projectile::clone)
				.collect(Collectors.toCollection(ArrayList::new));
		clone.startTime = this.startTime;
		clone.elapsedTime = this.elapsedTime;
		return clone;
	}

}

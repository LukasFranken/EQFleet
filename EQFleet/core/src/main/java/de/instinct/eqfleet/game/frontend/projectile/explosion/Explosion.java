package de.instinct.eqfleet.game.frontend.projectile.explosion;

import com.badlogic.gdx.math.Vector2;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Explosion {
	
	private Vector2 position;
	private float radius;
	private float elapsed;

}

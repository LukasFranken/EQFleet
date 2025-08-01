package de.instinct.eqfleet.game.frontend.projectile;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector2;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectileInstance {
	
	private int projectileId;
	private Vector2 lastPosition;
	private float aoeRadius;
	private ModelInstance model;
	private String particlesTag;
	private boolean active;

}

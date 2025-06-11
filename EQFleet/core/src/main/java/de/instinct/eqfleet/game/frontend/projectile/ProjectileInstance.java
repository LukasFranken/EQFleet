package de.instinct.eqfleet.game.frontend.projectile;

import com.badlogic.gdx.graphics.g3d.ModelInstance;

import de.instinct.engine.combat.projectile.Projectile;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectileInstance {
	
	private Projectile data;
	private ModelInstance model;
	private String particlesTag;
	private boolean active;

}

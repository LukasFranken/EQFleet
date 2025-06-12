package de.instinct.eqfleet.game.frontend.projectile;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import de.instinct.engine.combat.projectile.DirectionalProjectile;
import de.instinct.engine.combat.projectile.HomingProjectile;
import de.instinct.engine.combat.projectile.Projectile;
import de.instinct.engine.entity.Entity;
import de.instinct.engine.entity.EntityManager;
import de.instinct.engine.model.GameState;
import de.instinct.engine.model.ship.WeaponType;
import de.instinct.eqlibgdxutils.rendering.model.ModelLoader;
import de.instinct.eqlibgdxutils.rendering.model.ModelRenderer;
import de.instinct.eqlibgdxutils.rendering.particle.ParticleRenderer;

public class ProjectileRenderer {
	
	private List<ProjectileInstance> projectileInstances;
	
	public ProjectileRenderer() {
		projectileInstances = new ArrayList<>();
	}

	public void render(GameState state, PerspectiveCamera camera) {
		for (ProjectileInstance projectileInstance : projectileInstances) {
			projectileInstance.setActive(false);
		}
		List<Projectile> projectilesToRender = new ArrayList<>(state.projectiles);
		for (Projectile projectile : projectilesToRender) {
            
            ProjectileInstance projectileInstance = getProjectileInstance(projectile);
            if (projectileInstance == null) {
            	projectileInstance = instanciateProjectileInstance(projectile, state);
            	projectileInstances.add(projectileInstance);
            }
            
            Entity from = EntityManager.getEntity(state, projectile.id);
    		Vector2 to = new Vector2();
    		if (projectile instanceof DirectionalProjectile) {
    			to = new Vector2(projectile.position).add(((DirectionalProjectile) projectile).direction);
    		}
    		if (projectile instanceof HomingProjectile) {
    			to = EntityManager.getEntity(state, ((HomingProjectile) projectile).targetId).position;
    		}
    		float dx = to.x - from.position.x;
            float dy = to.y - from.position.y;
            float angleDeg = (float) Math.toDegrees(Math.atan2(dy, dx));
            
            Vector3 worldPos = new Vector3(projectile.position.x, projectile.position.y, 0f);
            projectileInstance.getModel().transform.idt();
            projectileInstance.getModel().transform.translate(worldPos);
            projectileInstance.getModel().transform.scale(5f, 5f, 5f);
            projectileInstance.getModel().transform.rotate(Vector3.Z, angleDeg - 90f);

            Color color = Color.GRAY;
            if (projectile.weaponType == WeaponType.LASER) {
            	color = Color.RED;
            }
            if (projectile.weaponType == WeaponType.MISSILE) {
            	color = Color.BROWN;
            }
            if (projectile.weaponType == WeaponType.BEAM) {
            	color = Color.GREEN;
            }
            for (Material material : projectileInstance.getModel().materials) {
                material.set(ColorAttribute.createDiffuse(color));
            }

            ModelRenderer.render(camera, projectileInstance.getModel());
            Vector3 screenPos = camera.project(worldPos);
            if (projectile.weaponType == WeaponType.BEAM) {
            	ParticleRenderer.setEmitterAngle(projectileInstance.getParticlesTag(), angleDeg - 150f, angleDeg - 210f);
            }
            if (projectile.weaponType == WeaponType.MISSILE) {
            	ParticleRenderer.setEmitterAngle(projectileInstance.getParticlesTag(), angleDeg - 170f, angleDeg - 190f);
            }
            ParticleRenderer.renderParticles(projectileInstance.getParticlesTag(), new Vector2(screenPos.x, screenPos.y));
            projectileInstance.setActive(true);
		}
		for (ProjectileInstance projectileInstance : projectileInstances) {
			if (!projectileInstance.isActive()) {
				ParticleRenderer.stop(projectileInstance.getParticlesTag());
			}
		}
		List<ProjectileInstance> toRemove = new ArrayList<>();
		for (ProjectileInstance instance : projectileInstances) {
			if (!instance.isActive()) {
				toRemove.add(instance);
			}
		}
		projectileInstances.removeAll(toRemove);
	}

	private ProjectileInstance getProjectileInstance(Projectile projectile) {
		for (ProjectileInstance instance : projectileInstances) {
			if (instance.getData().id == projectile.id) {
				return instance;
			}
		}
		return null;
	}

	private ProjectileInstance instanciateProjectileInstance(Projectile projectile, GameState state) {
		ModelInstance projectileModel = ModelLoader.instanciate("projectile");
    	String particleType = projectile.weaponType.toString().toLowerCase();
		String particlesTag = particleType + "_" + projectile.id;
		ParticleRenderer.loadParticles(particlesTag, particleType);
		return ProjectileInstance.builder()
				.data(projectile)
				.model(projectileModel)
				.particlesTag(particlesTag)
				.build();
	}

}

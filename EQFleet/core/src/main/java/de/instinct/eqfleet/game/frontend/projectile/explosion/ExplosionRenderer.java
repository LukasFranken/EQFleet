package de.instinct.eqfleet.game.frontend.projectile.explosion;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import de.instinct.eqfleet.audio.AudioManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.Shapes;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQCircle;

public class ExplosionRenderer {
	
	private List<Explosion> explosions;
	private float explosionDuration = 0.2f;
	
	public ExplosionRenderer() {
		this.explosions = new ArrayList<>();
	}
	
	public void renderExplosions(Camera camera) {
		List<Explosion> toRemoveExplosions = new ArrayList<>();
		for (Explosion explosion : explosions) {
			updateExplosion(explosion);
			if (explosion.getElapsed() >= explosionDuration) {
				toRemoveExplosions.add(explosion);
			}
		}
		explosions.removeAll(toRemoveExplosions);
		drawExplosions(camera);
	}
	
	private void updateExplosion(Explosion explosion) {
		explosion.setElapsed(explosion.getElapsed() + Gdx.graphics.getDeltaTime());
	}

	private void drawExplosions(Camera camera) {
		for (Explosion explosion : explosions) {
			Shapes.draw(EQCircle.builder()
					.position(explosion.getPosition())
					.radius(explosion.getRadius())
					.color(new Color(0.3f, 0f, 0f, 1f - (explosion.getElapsed() / explosionDuration)))
					.projectionMatrix(camera.combined)
					.build());
		}
	}

	public void addExplosion(Vector2 position, float radius) {
		explosions.add(Explosion.builder()
				.position(position)
				.radius(radius)
				.build());
		AudioManager.playSfx("explosion");
	}

}

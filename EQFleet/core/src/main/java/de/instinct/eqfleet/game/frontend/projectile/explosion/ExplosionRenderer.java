package de.instinct.eqfleet.game.frontend.projectile.explosion;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import de.instinct.eqfleet.audio.AudioManager;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.ComplexShapeRenderer;

public class ExplosionRenderer {
	
	private List<Explosion> explosions;
	private float explosionDuration = 0.2f;
	private ComplexShapeRenderer shapeRenderer;
	
	public ExplosionRenderer() {
		this.explosions = new ArrayList<>();
		this.shapeRenderer = new ComplexShapeRenderer();
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
		shapeRenderer.setProjectionMatrix(camera.combined);
		Gdx.gl.glEnable(GL20.GL_BLEND);
    	Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		for (Explosion explosion : explosions) {
			shapeRenderer.setColor(new Color(0.3f, 0f, 0f, 1f - (explosion.getElapsed() / explosionDuration)));
			shapeRenderer.circle(explosion.getPosition().x, explosion.getPosition().y, explosion.getRadius());
		}
		shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
	}

	public void addExplosion(Vector2 position, float radius) {
		explosions.add(Explosion.builder()
				.position(position)
				.radius(radius)
				.build());
		AudioManager.playSfx("explosion");
	}

}

package de.instinct.eqlibgdxutils.rendering.particle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class ParticleRenderer {

	private static ParticleLoader particleLoader;
	private static SpriteBatch particleBatch;
	private static Map<String, ParticleAnimation> particleAnimations;
	private static int currentPixels;
	
	private static List<String> activeEffects;

	public static void init() {
		activeEffects = new ArrayList<>();
		particleLoader = new ParticleLoader();
		particleBatch = new SpriteBatch();
		particleAnimations = new HashMap<>();
		calculateCurrentPixels();
		loadDefaultStarEffect();
	}

	private static void calculateCurrentPixels() {
		currentPixels = Gdx.graphics.getWidth() * Gdx.graphics.getHeight();
	}

	private static void loadDefaultStarEffect() {
		loadParticles("stars", "Star", 0.02f);
	}

	public static void loadParticles(String tag, String effectFileName, float particlePerPixel) {
		ParticleAnimation particleAnimation = ParticleAnimation.builder()
				.effectFileName(effectFileName)
				.totalParticles((int)(currentPixels * particlePerPixel))
				.build();
		List<ParticleEffect> particleEffects = particleLoader.loadParticles(particleAnimation);
		particleAnimation.setParticleEffects(particleEffects);
		particleAnimations.put(tag, particleAnimation);
	}
	
	public static void loadParticles(String tag, String effectFileName) {
		ParticleAnimation particleAnimation = ParticleAnimation.builder()
				.effectFileName(effectFileName)
				.build();
		List<ParticleEffect> particleEffects = particleLoader.loadParticles(particleAnimation);
		particleAnimation.setParticleEffects(particleEffects);
		particleAnimations.put(tag, particleAnimation);
	}

	public static void renderParticles(String effectName) {
		ParticleAnimation animation = particleAnimations.get(effectName);
		if (animation != null) {
			renderParticles(animation.getParticleEffects());
		}
	}
	
	public static void renderParticles(String effectName, Vector2 position) {
		ParticleAnimation animation = particleAnimations.get(effectName);
		animation.setPosition(position);
		for (ParticleEffect effect : animation.getParticleEffects()) {
			effect.setPosition(position.x, position.y);
		}
		if (animation != null) renderParticles(animation.getParticleEffects());
	}

	public static void updateParticles() {
		for (Entry<String, ParticleAnimation> entry  : particleAnimations.entrySet()) {
			if (Gdx.graphics.getDeltaTime() < 1) {
				for (ParticleEffect p : entry.getValue().getParticleEffects()) {
					p.update(Gdx.graphics.getDeltaTime());
				}
			}
		}
	}
	
	public static void start(String effectName) {
		ParticleAnimation animation = particleAnimations.get(effectName);
		if (animation != null) {
			activeEffects.add(effectName);
			for (ParticleEffect effect : animation.getParticleEffects()) {
				for (ParticleEmitter emitter : effect.getEmitters()) {
					emitter.start();
				}
			}
		}
	}
	
	public static boolean isStarted(String effectName) {
		return activeEffects.contains(effectName);
	}
	
	public static void stop(String effectName) {
		ParticleAnimation animation = particleAnimations.get(effectName);
		if (animation != null) {
			activeEffects.remove(effectName);
			for (ParticleEffect effect : animation.getParticleEffects()) {
				for (ParticleEmitter emitter : effect.getEmitters()) {
					emitter.allowCompletion();
				}
			}
		}
	}
	
	public static void setEmitterAngle(String effectName, float angleDeg) {
	    ParticleAnimation animation = particleAnimations.get(effectName);
	    if (animation != null) {
	        for (ParticleEffect effect : animation.getParticleEffects()) {
	            for (ParticleEmitter emitter : effect.getEmitters()) {
	                emitter.getAngle().setHigh(angleDeg);
	                emitter.getAngle().setLow(angleDeg);
	            }
	        }
	    }
	}
	
	public static void setEmitterAngle(String effectName, float startAngleDeg, float endAngleDeg) {
	    ParticleAnimation animation = particleAnimations.get(effectName);
	    if (animation != null) {
	        for (ParticleEffect effect : animation.getParticleEffects()) {
	            for (ParticleEmitter emitter : effect.getEmitters()) {
	                emitter.getAngle().setHigh(startAngleDeg);
	                emitter.getAngle().setHighMax(endAngleDeg);
	            }
	        }
	    }
	}
	
	public static void setEmitterVelocity(String effectName, float velocity) {
	    ParticleAnimation animation = particleAnimations.get(effectName);
	    if (animation != null) {
	        for (ParticleEffect effect : animation.getParticleEffects()) {
	            for (ParticleEmitter emitter : effect.getEmitters()) {
	                emitter.getVelocity().setHigh(velocity);
	                emitter.getVelocity().setLow(velocity);
	            }
	        }
	    }
	}
	
	private static void renderParticles(List<ParticleEffect> particleEffectList) {
		particleBatch.begin();
		if (particleEffectList != null) {
			for (ParticleEffect p : particleEffectList) {
				p.draw(particleBatch);
			}
		}
		particleBatch.end();
	}

	public static void dispose() {
		if (particleBatch != null)
		particleBatch.dispose();
	}

}

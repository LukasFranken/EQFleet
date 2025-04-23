package de.instinct.eqlibgdxutils.rendering.particle;

import java.util.List;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Vector2;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParticleAnimation {

	private String effectFileName;
	private List<ParticleEffect> particleEffects;
	private int totalParticles;
	private Vector2 position;

}

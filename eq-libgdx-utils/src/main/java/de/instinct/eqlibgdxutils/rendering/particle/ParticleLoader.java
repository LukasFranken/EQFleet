package de.instinct.eqlibgdxutils.rendering.particle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;

import de.instinct.eqlibgdxutils.GraphicsUtil;

public class ParticleLoader {

	private List<ParticleEffect> particleEffectList;

	public List<ParticleEffect> loadParticles(ParticleAnimation particleAnimation) {
		ParticleEffect templateParticleEffect = new ParticleEffect();
		templateParticleEffect.load(Gdx.files.internal("particle/" + particleAnimation.getEffectFileName() + ".p"), Gdx.files.internal("particle"));
		templateParticleEffect.scaleEffect(1 + ((GraphicsUtil.getHorizontalDisplayScaleFactor() - 1) / 2));
		particleEffectList = new ArrayList<>();
	    if (particleAnimation.getTotalParticles() > 0) {
	    	Random random = new Random();
		    for (int i = 0; i < particleAnimation.getTotalParticles(); i++) {
		        ParticleEffect particleEffect = new ParticleEffect(templateParticleEffect);
		        particleEffect.getEmitters().first().setPosition(
		                random.nextFloat() * Gdx.graphics.getWidth(),
		                random.nextFloat() * Gdx.graphics.getHeight()
		        );
		        particleEffect.start();
		        particleEffectList.add(particleEffect);
		    }
	    } else {
	    	particleEffectList.add(templateParticleEffect);
	    }
	    return particleEffectList;
	}

}

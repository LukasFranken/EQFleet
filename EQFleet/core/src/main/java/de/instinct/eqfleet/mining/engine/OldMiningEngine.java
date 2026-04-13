package de.instinct.eqfleet.mining.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import de.instinct.eqfleet.mining.MiningModel;
import de.instinct.eqfleet.mining.input.MiningInput;
import de.instinct.eqlibgdxutils.MathUtil;

public class OldMiningEngine {
	
	private float movespeed =  100f;
	private float turnspeed = 20f;
	
	private float cooldown = 0f;
	
	public void init() {
		
	}
	
	public void update() {
		/*cooldown = MathUtil.clamp(cooldown - Gdx.graphics.getDeltaTime(), 0, 999);
		float rotationRad = (MiningModel.shipRotation + 90f) * MathUtils.degreesToRadians;
		
		if (MiningInput.up) {
			MiningModel.shipPosition.x += MathUtils.cos(rotationRad) * Gdx.graphics.getDeltaTime() * movespeed;
			MiningModel.shipPosition.y += MathUtils.sin(rotationRad) * Gdx.graphics.getDeltaTime() * movespeed;
		}
		if (MiningInput.down) {
			MiningModel.shipPosition.x -= MathUtils.cos(rotationRad) * Gdx.graphics.getDeltaTime() * movespeed;
			MiningModel.shipPosition.y -= MathUtils.sin(rotationRad) * Gdx.graphics.getDeltaTime() * movespeed;
		}
		if (MiningInput.left) {
			MiningModel.shipRotation += Gdx.graphics.getDeltaTime() * turnspeed;
		}
		if (MiningInput.right) {
			MiningModel.shipRotation -= Gdx.graphics.getDeltaTime() * turnspeed;
		}
		if (MiningInput.shoot) {
			if (cooldown == 0) {
				cooldown = 0.5f;
				System.out.println("shoot");
			}
		}*/
	}

}

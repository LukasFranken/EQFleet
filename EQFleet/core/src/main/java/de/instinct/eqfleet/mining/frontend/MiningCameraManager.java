package de.instinct.eqfleet.mining.frontend;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;

import de.instinct.engine.mining.entity.ship.MiningPlayerShip;
import de.instinct.eqfleet.mining.MiningEngineAPI;
import de.instinct.eqfleet.mining.MiningModel;
import de.instinct.eqlibgdxutils.MathUtil;
import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.debug.modulator.Modulator;
import de.instinct.eqlibgdxutils.debug.modulator.modulation.types.RangeModulation;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.slider.LabelUpdateAction;
import de.instinct.eqlibgdxutils.rendering.ui.component.active.slider.ValueChangeAction;

public class MiningCameraManager {

	public static final Vector3 BASE_CAM_POS = new Vector3(0f, 0f, 4400f);

	private static float BEHIND_DISTANCE = 300f;
	private static float HEIGHT_OFFSET = 125f;
	private static float LOOK_AHEAD = 100f;

	private static final float INTRO_START_HEIGHT = 5000f;
	private static final float INTRO_DURATION = 3f;

	private PerspectiveCamera camera;
	private float animationTime = 0f;
	private boolean animating = false;

	public void init() {
		camera = new PerspectiveCamera(30, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(BASE_CAM_POS);
		camera.lookAt(0f, 0f, 0f);
		camera.up.set(0f, 0f, 1f);
		camera.near = 1f;
		camera.far = 10000f;
		camera.update();

		animationTime = 0f;
		animating = true;

		RangeModulation behindDistanceMod = new RangeModulation("behind-dist", new ValueChangeAction() {
			@Override
			public void execute(float value) {
				BEHIND_DISTANCE = value * 4000f;
			}
		}, BEHIND_DISTANCE / 4000f, new LabelUpdateAction() {
			@Override
			public String getLabelText(float value) {
				return StringUtils.format(value * 4000f, 0);
			}
		});
		Modulator.add("mining - camera", behindDistanceMod);

		RangeModulation heightOffsetMod = new RangeModulation("height-offs", new ValueChangeAction() {
			@Override
			public void execute(float value) {
				HEIGHT_OFFSET = value * 4000f;
			}
		}, HEIGHT_OFFSET / 4000f, new LabelUpdateAction() {
			@Override
			public String getLabelText(float value) {
				return StringUtils.format(value * 4000f, 0);
			}
		});
		Modulator.add("mining - camera", heightOffsetMod);

		RangeModulation lookAheadMod = new RangeModulation("look-ahead", new ValueChangeAction() {
			@Override
			public void execute(float value) {
				LOOK_AHEAD = value * 1000f;
			}
		}, LOOK_AHEAD / 1000f, new LabelUpdateAction() {
			@Override
			public String getLabelText(float value) {
				return StringUtils.format(value * 1000f, 0);
			}
		});
		Modulator.add("mining - camera", lookAheadMod);
	}

	public void update() {
		MiningPlayerShip ship = MiningEngineAPI.getShip(MiningModel.playerId);

		float angleDeg = ship.direction.angleDeg();
		float angleRad = angleDeg * (float) Math.PI / 180f;

		float forwardX = (float) Math.cos(angleRad);
		float forwardY = (float) Math.sin(angleRad);

		float targetPosX = ship.position.x - forwardX * BEHIND_DISTANCE;
		float targetPosY = ship.position.y - forwardY * BEHIND_DISTANCE;
		float targetPosZ = HEIGHT_OFFSET;

		float lookAtX = ship.position.x + forwardX * LOOK_AHEAD;
		float lookAtY = ship.position.y + forwardY * LOOK_AHEAD;

		if (animating) {
			animationTime += Gdx.graphics.getDeltaTime();
			float ratio = Math.min(animationTime / INTRO_DURATION, 1f);

			// Start: directly above the ship, end: behind the ship
			camera.position.x = MathUtil.easeInOut(ship.position.x, targetPosX, ratio);
			camera.position.y = MathUtil.easeInOut(ship.position.y, targetPosY, ratio);
			camera.position.z = MathUtil.easeInOut(INTRO_START_HEIGHT, targetPosZ, ratio);

			if (ratio >= 1f) {
				animating = false;
			}
		} else {
			camera.position.x = targetPosX;
			camera.position.y = targetPosY;
			camera.position.z = targetPosZ;
		}

		camera.up.set(0f, 0f, 1f);
		camera.lookAt(lookAtX, lookAtY, 0f);
		camera.update();
	}

	public PerspectiveCamera getCamera() {
		return camera;
	}
}

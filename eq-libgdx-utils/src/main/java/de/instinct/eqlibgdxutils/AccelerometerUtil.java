package de.instinct.eqlibgdxutils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.math.Vector3;

import de.instinct.eqlibgdxutils.debug.console.Console;
import de.instinct.eqlibgdxutils.debug.metrics.types.VectorMetric;

public class AccelerometerUtil {
	
	private static final String ACCELEROMETER_METRIC_TAG = "acceleration";
	private static float accelerationSmoothing = 0.2f;
	
	private static Vector3 rawAcceleration;
	private static Vector3 smoothedAcceleration;
	
	
	public static void init() {
		if (!Gdx.app.getType().equals(ApplicationType.Desktop)) {
			Console.registerMetric(VectorMetric.builder()
					.tag(ACCELEROMETER_METRIC_TAG)
					.decimals(1)
					.build());
		}
		rawAcceleration = new Vector3();
		smoothedAcceleration = new Vector3();
	}
	
	public static void update() {
		if (!Gdx.app.getType().equals(ApplicationType.Desktop)) {
			rawAcceleration.set(Gdx.input.getAccelerometerX(), Gdx.input.getAccelerometerY(), Gdx.input.getAccelerometerZ());
			smoothedAcceleration.lerp(rawAcceleration, accelerationSmoothing);
			Console.updateMetric(ACCELEROMETER_METRIC_TAG, rawAcceleration);
		}
	}
	
	public static Vector3 getAcceleration() {
		return smoothedAcceleration;
	}
	
	public static Vector3 getRawAcceleration() {
		return rawAcceleration;
	}

}

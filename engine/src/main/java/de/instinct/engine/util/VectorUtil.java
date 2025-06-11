package de.instinct.engine.util;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class VectorUtil {
	
	public static Vector3 getGroundCollisionFromDirection(Vector3 position, Vector3 direction) {
		return new Vector3(position.x - direction.x * (position.y / direction.y), 0, position.z - direction.z * (position.y / direction.y));
	}
	
	public static Vector3 getGroundCollisionFromPoint(Vector3 cameraPosition, Vector3 nearPlanePoint) {
		return getGroundCollisionFromDirection(cameraPosition, new Vector3(cameraPosition.x-nearPlanePoint.x, cameraPosition.y-nearPlanePoint.y, cameraPosition.z-nearPlanePoint.z));
	}

	public static float dst(Vector2 position, Vector2 position2) {
		return (float) Math.sqrt(Math.pow(position.x - position2.x, 2) + Math.pow(position.y - position2.y, 2));
	}

	public static Vector2 getTargetPosition(Vector2 origin, Vector2 target, float distance) {
		return new Vector2(
			origin.x + (target.x - origin.x) * (distance / dst(origin, target)),
			origin.y + (target.y - origin.y) * (distance / dst(origin, target))
		);
	}
	
	public static Vector2 getDirectionalTargetPosition(Vector2 origin, Vector2 direction, float distance) {
		return new Vector2(
			origin.x + direction.x * distance,
			origin.y + direction.y * distance
		);
	}

	public static Vector2 getDirection(Vector2 origin, Vector2 target) {
		Vector2 direction = new Vector2(target.x - origin.x, target.y - origin.y);
		return direction.nor();
	}

}

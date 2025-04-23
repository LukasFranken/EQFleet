package de.instinct.eqlibgdxutils;

import com.badlogic.gdx.math.Vector3;

public class VectorUtil {
	
	public static Vector3 getGroundCollisionFromDirection(Vector3 position, Vector3 direction) {
		return new Vector3(position.x - direction.x * (position.y / direction.y), 0, position.z - direction.z * (position.y / direction.y));
	}
	
	public static Vector3 getGroundCollisionFromPoint(Vector3 cameraPosition, Vector3 nearPlanePoint) {
		return getGroundCollisionFromDirection(cameraPosition, new Vector3(cameraPosition.x-nearPlanePoint.x, cameraPosition.y-nearPlanePoint.y, cameraPosition.z-nearPlanePoint.z));
	}

}

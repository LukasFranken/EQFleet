package de.instinct.eqlibgdxutils;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class MathUtil {

	public static float linear(float min, float max, float ratio) {
		if (min < max) {
			return MathUtils.clamp(min + (ratio * (max - min)), min, max);
		} else {
			return MathUtils.clamp(min - (ratio * (min - max)), max, min);
		}
	}

	private static float growthRate = 3f;
	public static float easeInOut(float min, float max, float ratio) {
		ratio = MathUtils.clamp(ratio, 0, 1);
		float curvedRatio;
		if (ratio < 0.5f) {
			curvedRatio = (float) Math.pow(ratio * 2, growthRate) / 2;
		} else {
			curvedRatio = 1 - (float) Math.pow(2 - ratio * 2, growthRate) / 2;
		}
		return MathUtils.clamp(min + (curvedRatio * (max - min)), Math.min(min, max), Math.max(min, max));
	}
	
	public static float clamp(float value, float limit1, float limit2) {
		if (limit1 < limit2) {
			return MathUtils.clamp(value, limit1, limit2);
		} else {
			return MathUtils.clamp(value, limit2, limit1);
		}
	}
	
	public static float diff(float value1, float value2) {
		if (value1 > value2) {
			return value1 - value2;
		}
		if (value1 < value2) {
			return value2 - value1;
		}
		return 0;
	}
	
	public static Vector2 translate(Vector2 point, Rectangle fromBounds, Rectangle toBounds) {
		float xRatio = (point.x - fromBounds.x) / fromBounds.width;
		float x = toBounds.x + (xRatio * toBounds.width);
		float yRatio = (point.y - fromBounds.y) / fromBounds.height;
		float y = toBounds.y + (yRatio * toBounds.height);
		return new Vector2(x, y);
	}

}

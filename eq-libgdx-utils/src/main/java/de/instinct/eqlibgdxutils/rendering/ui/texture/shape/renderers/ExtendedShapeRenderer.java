package de.instinct.eqlibgdxutils.rendering.ui.texture.shape.renderers;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class ExtendedShapeRenderer extends ShapeRenderer {
	
	public void roundRectangle(Rectangle rect, float thickness) {
		begin(ShapeRenderer.ShapeType.Filled);
		float x = rect.x;
		float y = rect.y;
		float w = rect.width;
		float h = rect.height;
		float r = thickness;

		float cx0 = x + r, 	   cy0 = y + r;
		float cx1 = x + w - r, cy1 = y + r;
		float cx2 = x + w - r, cy2 = y + h - r;
		float cx3 = x + r, 	   cy3 = y + h - r;

		arc(cx0, cy0, r, 180f, 90f);
		arc(cx1, cy1, r, 270f, 90f);
		arc(cx2, cy2, r, 0f, 90f);
		arc(cx3, cy3, r, 90f, 90f);

		super.rect(x + r, y, w - (r * 2), r);
		super.rect(x, y + r, r, h - (r * 2));
		super.rect(x + w - r, y + r, r, h - (r * 2));
		super.rect(x + r, y + h - r, w - (r * 2), r);
		end();
	}
	
	public void filledRoundRectangle(Rectangle rect) {
		if (rect.width > 2) {
			roundRectangle(rect, 2f);
		} else {
			roundRectangle(rect, 1f);
		}
		if (rect.width > 4) {
			begin(ShapeRenderer.ShapeType.Filled);
			super.rect(rect.x + 2, rect.y + 2, rect.width - 4, rect.height - 4);
			end();
		}
	}
	
	public void cleanArc(Vector2 position, float innerRadius, float outerRadius, float startAngle, float degrees) {
		begin(ShapeRenderer.ShapeType.Filled);
	    int segments = calculateSegments(outerRadius, degrees);

	    for (int i = 0; i < segments; i++) {
	    	float angle = startAngle + (i * degrees) / segments;
	    	float nextAngle = startAngle + ((i + 1) * degrees) / segments;
	    	float angleRad = (float)Math.toRadians(angle);
	    	float nextAngleRad = (float)Math.toRadians(nextAngle);

	        // Outer points
	    	float x1 = position.x + MathUtils.cos(angleRad) * outerRadius;
	    	float y1 = position.y + MathUtils.sin(angleRad) * outerRadius;
	    	float x2 = position.x + MathUtils.cos(nextAngleRad) * outerRadius;
	    	float y2 = position.y + MathUtils.sin(nextAngleRad) * outerRadius;

	        // Inner points
	    	float x3 = position.x + MathUtils.cos(nextAngleRad) * innerRadius;
	    	float y3 = position.y + MathUtils.sin(nextAngleRad) * innerRadius;
	    	float x4 = position.x + MathUtils.cos(angleRad) * innerRadius;
	        float y4 = position.y + MathUtils.sin(angleRad) * innerRadius;

	        super.triangle(x1, y1, x2, y2, x3, y3);
	        super.triangle(x1, y1, x3, y3, x4, y4);
	    }
	    end();
	}

	private static int calculateSegments(double radius, double degrees) {
	    return Math.max(1, (int)(6 * (double)Math.cbrt(radius) * (degrees / 360.0f)));
	}

	public void partialRect(Rectangle bounds, float missingWidth, float missingXOffset, float thickness) {
		begin(ShapeRenderer.ShapeType.Filled);
		float x = bounds.x;
		float y = bounds.y;
		float w = bounds.width;
		float h = bounds.height;
		float r = thickness;

		super.rect(x, y, w, r);
		super.rect(x, y, r, h);
		super.rect(x + w - r, y, r, h);
		
		super.rect(x, y + h - r, missingXOffset, r);
		super.rect(x + missingXOffset + missingWidth, y + h - r, w - (missingXOffset + missingWidth), r);
		end();
	}

	public void rect(Rectangle bounds, float thickness) {
		begin(ShapeRenderer.ShapeType.Filled);
		if (thickness > 0) {
			rect(bounds.x, bounds.y, bounds.width, thickness);
			rect(bounds.x, bounds.y, thickness, bounds.height);
			rect(bounds.x + bounds.width - thickness, bounds.y, thickness, bounds.height);
			rect(bounds.x, bounds.y + bounds.height - thickness, bounds.width, thickness);
		} else {
			rect(bounds.x, bounds.y, bounds.width, bounds.height);
		}
		end();
	}

	public void circle(Vector2 position, float radius) {
		begin(ShapeRenderer.ShapeType.Filled);
		circle(position.x, position.y, radius);
		end();
	}

}

package de.instinct.eqlibgdxutils.rendering.ui.texture.shape;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class ComplexShapeRenderer extends ShapeRenderer {

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
	
	public void cleanArc(float x, float y, float radius, float start, float degrees) {
		int segments = (int) (6 * (float) Math.cbrt(radius) * (degrees / 360.0f));
		if (segments <= 0)
			throw new IllegalArgumentException("segments must be > 0.");
		float colorBits = super.getColor().toFloatBits();
		float theta = (2 * MathUtils.PI * (degrees / 360.0f)) / segments;
		float cos = MathUtils.cos(theta);
		float sin = MathUtils.sin(theta);
		float cx = radius * MathUtils.cos(start * MathUtils.degreesToRadians);
		float cy = radius * MathUtils.sin(start * MathUtils.degreesToRadians);

		for (int i = 0; i < segments; i++) {
			super.getRenderer().color(colorBits);
			super.getRenderer().vertex(x + cx, y + cy, 0);
			float temp = cx;
			cx = cos * cx - sin * cy;
			cy = sin * temp + cos * cy;
			super.getRenderer().color(colorBits);
			super.getRenderer().vertex(x + cx, y + cy, 0);
		}
	}

}


package de.instinct.eqfleet.mining.frontend;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.Shapes;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQCircle;

public class CustomJoystick {

    private final EQCircle backgroundShape;
    private final EQCircle knobShape;
    private final Vector2 backgroundPosition;
    private final Vector2 knobPosition;
    private final Vector2 touchStart;
    private final float radius;

    private boolean isTouched;

    public CustomJoystick(float x, float y, float size) {
    	backgroundShape = EQCircle.builder()
    			.color(new Color(Color.GRAY))
    			.build();
		knobShape = EQCircle.builder()
				.color(new Color(Color.DARK_GRAY))
				.build();
        backgroundPosition = new Vector2(x, y);
        knobPosition = new Vector2(x, y);
        touchStart = new Vector2();
        radius = size / 2;
        isTouched = false;
    }

    public void update() {
        if (Gdx.input.isTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

            if (!isTouched) {
                if (backgroundPosition.dst(touchX, touchY) <= radius) {
                    isTouched = true;
                    touchStart.set(touchX, touchY);
                }
            }

            if (isTouched) {
                Vector2 direction = new Vector2(touchX - backgroundPosition.x, touchY - backgroundPosition.y);
                if (direction.len() > radius) {
                    direction.setLength(radius);
                }
                knobPosition.set(backgroundPosition.x + direction.x, backgroundPosition.y + direction.y);
            }
        } else {
            isTouched = false;
            knobPosition.set(backgroundPosition);
        }
    }

    public Vector2 getDirection() {
        if (isTouched) {
            return new Vector2(knobPosition.x - backgroundPosition.x, knobPosition.y - backgroundPosition.y).nor();
        }
        return Vector2.Zero;
    }

    public void render() {
    	backgroundShape.setPosition(backgroundPosition);
    	backgroundShape.setRadius(radius);
    	Shapes.draw(backgroundShape);
    	System.out.println("BG Position: " + backgroundShape.getPosition());
    	knobShape.setPosition(knobPosition);
    	knobShape.setRadius(radius / 2);
    	Shapes.draw(knobShape);
    }

    public void dispose() {
        
    }
}

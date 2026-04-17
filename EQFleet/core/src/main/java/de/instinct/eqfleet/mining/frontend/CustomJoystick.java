
package de.instinct.eqfleet.mining.frontend;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import de.instinct.eqlibgdxutils.MultitouchInputUtil;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.Shapes;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQCircle;

public class CustomJoystick {

    private final EQCircle backgroundShape;
    private final EQCircle knobShape;
    private final Vector2 backgroundPosition;
    private final Vector2 knobPosition;
    private final float radius;

    private int activePointer = -1;

    public CustomJoystick(float x, float y, float size) {
    	backgroundShape = EQCircle.builder()
    			.color(new Color(0.7f, 0.7f, 0.7f, 0.5f))
    			.build();
		knobShape = EQCircle.builder()
				.color(new Color(Color.DARK_GRAY))
				.build();
        backgroundPosition = new Vector2(x, y);
        knobPosition = new Vector2(x, y);
        radius = size / 2;
    }

    public void update() {
        if (activePointer == -1) {
            for (int i = 0; i < 10; i++) {
                if (MultitouchInputUtil.isTouched(i)) {
                    Vector2 touchPos = MultitouchInputUtil.getVirtualTouchPosition(i);
                    if (backgroundPosition.dst(touchPos) <= radius) {
                        activePointer = i;
                        break;
                    }
                }
            }
        }

        if (activePointer != -1) {
            if (MultitouchInputUtil.isTouched(activePointer)) {
                Vector2 touchPos = MultitouchInputUtil.getVirtualTouchPosition(activePointer);
                Vector2 direction = new Vector2(touchPos.x - backgroundPosition.x, touchPos.y - backgroundPosition.y);
                if (direction.len() > radius) {
                    direction.setLength(radius);
                }
                knobPosition.set(backgroundPosition.x + direction.x, backgroundPosition.y + direction.y);
            } else {
                activePointer = -1;
                knobPosition.set(backgroundPosition);
            }
        }
    }

    public Vector2 getDirection() {
        if (activePointer != -1) {
            return new Vector2(knobPosition.x - backgroundPosition.x, knobPosition.y - backgroundPosition.y).nor();
        }
        return Vector2.Zero;
    }

    public void render() {
        backgroundShape.setPosition(backgroundPosition);
        backgroundShape.setRadius(radius);
        Shapes.draw(backgroundShape);

        knobShape.setPosition(knobPosition);
        knobShape.setRadius(radius / 2);
        Shapes.draw(knobShape);
    }

    public void dispose() {
        
    }
}

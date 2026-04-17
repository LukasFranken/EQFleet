package de.instinct.eqlibgdxutils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;
import java.util.Map;

public class MultitouchInputUtil {

    private static final int MAX_TOUCH_POINTS = 4;
    private static final Map<Integer, TouchState> touchStates = new HashMap<>();

    public static void update() {
        for (int i = 0; i < MAX_TOUCH_POINTS; i++) {
            if (Gdx.input.isTouched(i)) {
                if (!touchStates.containsKey(i)) {
                    touchStates.put(i, new TouchState());
                }
                TouchState state = touchStates.get(i);
                state.update(Gdx.input.getX(i), Gdx.graphics.getHeight() - Gdx.input.getY(i), true);
            } else {
                if (touchStates.containsKey(i)) {
                    touchStates.get(i).update(0, 0, false);
                }
            }
        }
    }

    public static boolean isTouched(int pointer) {
        return touchStates.containsKey(pointer) && touchStates.get(pointer).isTouched();
    }

    public static boolean isPressed(int pointer) {
        return touchStates.containsKey(pointer) && touchStates.get(pointer).isPressed();
    }

    public static boolean isReleased(int pointer) {
        return touchStates.containsKey(pointer) && touchStates.get(pointer).isReleased();
    }

    public static Vector2 getTouchPosition(int pointer) {
        if (touchStates.containsKey(pointer)) {
            return touchStates.get(pointer).getPosition();
        }
        return null;
    }
    
    public static Vector2 getVirtualTouchPosition(int pointer) {
        if (touchStates.containsKey(pointer)) {
            return touchStates.get(pointer).getVirtualPosition();
        }
        return null;
    }

    private static class TouchState {
        private final Vector2 position = new Vector2();
        private final Vector2 virtualPosition = new Vector2();
        private boolean touched;
        private boolean pressed;
        private boolean released;

        public void update(float x, float y, boolean isTouched) {
            if (isTouched) {
                if (!touched) {
                    pressed = true;
                } else {
                    pressed = false;
                }
                touched = true;
                released = false;
                position.set(x, y);
                virtualPosition.set(x, y);
                GraphicsUtil.translateToVirtual(virtualPosition);
            } else {
                if (touched) {
                    released = true;
                } else {
                    released = false;
                }
                touched = false;
                pressed = false;
            }
        }

        public boolean isTouched() {
            return touched;
        }

        public boolean isPressed() {
            return pressed;
        }

        public boolean isReleased() {
            return released;
        }

        public Vector2 getPosition() {
            return position;
        }
        
        public Vector2 getVirtualPosition() {
            return virtualPosition;
        }
    }
}

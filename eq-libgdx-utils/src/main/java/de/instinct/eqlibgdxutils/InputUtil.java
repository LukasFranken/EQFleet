package de.instinct.eqlibgdxutils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import de.instinct.eqlibgdxutils.debug.console.Console;

public class InputUtil {
	
	private static boolean touched;
	private static boolean pressed;
	private static boolean released;
	
	public static void update() {
		if (Gdx.input.isTouched()) {
			pressed = true;
			released = false;
		} else {
			if (pressed) {
				pressed = false;
				released = true;
			}
			if (!touched && released) {
				touched = true;
				released = false;
			} else {
				touched = false;
			}
		}
	}
	
	public static boolean mouseIsOver(Rectangle rectangle) {
		return rectangle.contains(getMouseX(), getMouseY());
	}

	public static boolean isClicked() {
		return touched && !Console.isActive();
	}
	
	public static boolean isPressed() {
		return pressed && !Console.isActive();
	}
	
	public static boolean isPressedConsole() {
		return pressed;
	}
	
	public static boolean isClickedConsole() {
		return touched;
	}
	
	public static boolean rightMouseIsClicked() {
		return Gdx.input.isButtonJustPressed(Buttons.RIGHT);
	}
	
	public static int getMouseX() {
		return Gdx.input.getX();
	}
	
	public static int getMouseY() {
		return Gdx.graphics.getHeight() - Gdx.input.getY();
	}

	public static Vector2 getMousePosition() {
		return new Vector2(getMouseX(), getMouseY());
	}
	
	public static Vector2 getNormalizedMousePosition() {
		Rectangle normalizedScreenBounds = GraphicsUtil.scaleFactorDeducted(new Rectangle(0, 0, getMouseX(), getMouseY()));
		return new Vector2(normalizedScreenBounds.width, normalizedScreenBounds.height);
	}

}

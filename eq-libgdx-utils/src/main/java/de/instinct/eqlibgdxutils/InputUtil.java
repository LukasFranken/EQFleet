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
		} else {
			pressed = false;
			released = true;
		}
		if (!touched && released) {
			if (pressed) {
				touched = true;
				released = false;
			}
		} else {
			touched = false;
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

}

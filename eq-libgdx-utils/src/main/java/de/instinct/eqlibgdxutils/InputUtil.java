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
	private static boolean justPressed;
	
	private static Vector2 mousePosition = new Vector2();
	private static Vector2 virtualMousePosition = new Vector2();
	
	public static void update() {
		if (Gdx.input.isTouched()) {
			if (!pressed) {
				justPressed = true;
			} else {
				justPressed = false;
			}
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
		Vector2 virtualMousePos = getVirtualMousePosition();
		return rectangle.contains(virtualMousePos.x, virtualMousePos.y);
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
	
	public static boolean isJustPressed() {
		return justPressed;
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
		mousePosition.set(getMouseX(), getMouseY());
		return mousePosition;
	}
	
	public static Vector2 getVirtualMousePosition() {
		virtualMousePosition.set(GraphicsUtil.translateToVirtual(getMousePosition()));
		return virtualMousePosition;
	}

}

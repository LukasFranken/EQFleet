package de.instinct.eqlibgdxutils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class InputUtil {
	
	public static boolean mouseIsOver(Rectangle rectangle) {
		return rectangle.contains(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
	}

	public static boolean leftMouseIsClicked() {
		return Gdx.input.isButtonJustPressed(Buttons.LEFT);
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

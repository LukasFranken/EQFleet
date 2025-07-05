package de.instinct.eqlibgdxutils;

import java.nio.IntBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.BufferUtils;

public class ViewportUtil {
	
	private static final IntBuffer previousViewport = BufferUtils.newIntBuffer(16);
	
	public static void apply(Rectangle bounds) {
		float displayScaleFactor = GraphicsUtil.getDisplayScaleFactor();

		previousViewport.clear();
		Gdx.gl.glGetIntegerv(GL20.GL_VIEWPORT, previousViewport);

		float scaledX = bounds.x * displayScaleFactor;
		float scaledY = bounds.y * displayScaleFactor;
		float scaledWidth = bounds.width * displayScaleFactor;
		float scaledHeight = bounds.height * displayScaleFactor;

		Gdx.gl.glViewport((int) scaledX, (int) scaledY, (int) scaledWidth, (int) scaledHeight);
		Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);
		Gdx.gl.glScissor((int) scaledX, (int) scaledY, (int) scaledWidth, (int) scaledHeight);
	}

	public static void restore() {
		Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);
		Gdx.gl.glViewport(previousViewport.get(0), previousViewport.get(1), previousViewport.get(2),
				previousViewport.get(3));
	}

}

package de.instinct.eqlibgdxutils;

import java.nio.IntBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.BufferUtils;

public class ViewportUtil {
	
	private static final IntBuffer previousViewport = BufferUtils.newIntBuffer(16);
	
	public static void apply(Rectangle bounds) {
		previousViewport.clear();
		Gdx.gl.glGetIntegerv(GL20.GL_VIEWPORT, previousViewport);
		Gdx.gl.glViewport((int) bounds.x, (int) bounds.y, (int) bounds.width, (int) bounds.height);
		Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);
		Gdx.gl.glScissor((int) bounds.x, (int) bounds.y, (int) bounds.width, (int) bounds.height);
	}

	public static void restore() {
		Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);
		Gdx.gl.glViewport(previousViewport.get(0), previousViewport.get(1), previousViewport.get(2),
				previousViewport.get(3));
	}

}

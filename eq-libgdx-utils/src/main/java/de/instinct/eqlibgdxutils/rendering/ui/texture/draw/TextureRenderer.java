package de.instinct.eqlibgdxutils.rendering.ui.texture.draw;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class TextureRenderer {

	private SpriteBatch batch;

	public TextureRenderer() {
		batch = new SpriteBatch();
	}

	public void draw(Texture texture, Rectangle bounds, TextureDrawMode drawMode) {
		if (texture != null) {
			batch.begin();
			switch (drawMode) {
			case LIGHT:
				batch.setColor(1, 1, 1, 1);
				batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height);
				batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
				batch.setColor(1, 1, 1, 0.5f);
				batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height);
				batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
				batch.setColor(1, 1, 1, 1);
				break;

			case NORMAL:
				batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height);
				break;

			case DARK:
				batch.setColor(0.5f, 0.5f, 0.5f, 1);
				batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height);
				batch.setColor(1, 1, 1, 1);
				break;

			case VERY_DARK:
				batch.setColor(0.15f, 0.15f, 0.15f, 1);
				batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height);
				batch.setColor(1, 1, 1, 1);
				break;
			}
			batch.end();
		}
	}

	public void draw(Texture texture, Rectangle rectangle, float alpha) {
		batch.begin();
		if (batch.isDrawing() && texture != null) {
			batch.setColor(1f, 1f, 1f, alpha);
			batch.draw(texture, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
			batch.setColor(1, 1, 1, 1);
		}
		batch.end();
	}

	public void dispose() {
		batch.dispose();
	}

}

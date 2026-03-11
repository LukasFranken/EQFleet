package de.instinct.eqlibgdxutils.rendering.ui.texture.draw;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqlibgdxutils.GraphicsUtil;

public class TextureRenderer {

	private SpriteBatch batch;
	private Rectangle workingBounds;

	public TextureRenderer() {
		batch = new SpriteBatch();
		workingBounds = new Rectangle();
	}

	public void draw(Texture texture, Rectangle virtualBounds, TextureDrawMode drawMode) {
		if (texture != null) {
			workingBounds.set(virtualBounds);
			GraphicsUtil.translateToPhysical(workingBounds);
			batch.begin();
			switch (drawMode) {
			case LIGHT:
				batch.setColor(1, 1, 1, 1);
				batch.draw(texture, workingBounds.x, workingBounds.y, workingBounds.width, workingBounds.height);
				batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
				batch.setColor(1, 1, 1, 0.5f);
				batch.draw(texture, workingBounds.x, workingBounds.y, workingBounds.width, workingBounds.height);
				batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
				batch.setColor(1, 1, 1, 1);
				break;

			case NORMAL:
				batch.draw(texture, workingBounds.x, workingBounds.y, workingBounds.width, workingBounds.height);
				break;

			case DARK:
				batch.setColor(0.5f, 0.5f, 0.5f, 1);
				batch.draw(texture, workingBounds.x, workingBounds.y, workingBounds.width, workingBounds.height);
				batch.setColor(1, 1, 1, 1);
				break;

			case VERY_DARK:
				batch.setColor(0.15f, 0.15f, 0.15f, 1);
				batch.draw(texture, workingBounds.x, workingBounds.y, workingBounds.width, workingBounds.height);
				batch.setColor(1, 1, 1, 1);
				break;
			}
			batch.end();
		}
	}

	public void draw(Texture texture, Rectangle virtualBounds, float alpha) {
		workingBounds.set(virtualBounds);
		GraphicsUtil.translateToPhysical(workingBounds);
		batch.begin();
		if (batch.isDrawing() && texture != null) {
			batch.setColor(1f, 1f, 1f, alpha);
			batch.draw(texture, workingBounds.x, workingBounds.y, workingBounds.width, workingBounds.height);
			batch.setColor(1, 1, 1, 1);
		}
		batch.end();
	}

	public void dispose() {
		batch.dispose();
	}

}

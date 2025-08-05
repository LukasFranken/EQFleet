package de.instinct.eqlibgdxutils.rendering.grid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import de.instinct.eqlibgdxutils.GraphicsUtil;

public class GridRenderer {

	private final ShapeRenderer shapeRenderer;

	private GridConfiguration config;
	
	private float alpha = 1f;

	public GridRenderer(GridConfiguration config) {
		this.shapeRenderer = new ShapeRenderer();
		this.config = config;
	}

	public void drawGrid(Camera camera) {
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glLineWidth(config.getLineThickness() * GraphicsUtil.getHorizontalDisplayScaleFactor());

		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		Color finalColor = new Color(config.getLineColor());
		finalColor.a *= alpha;
		shapeRenderer.setColor(finalColor);

		float gridZ = 0f;
		float gridPlaneDistance = Math.abs(camera.position.z - gridZ);
		float aspectRatio = camera.viewportWidth / camera.viewportHeight;

		if (camera instanceof PerspectiveCamera) {
			PerspectiveCamera pCam = (PerspectiveCamera) camera;
			float halfHeight = (float) (gridPlaneDistance * Math.tan(Math.toRadians(pCam.fieldOfView * 0.5f)));
			float halfWidth = halfHeight * aspectRatio;

			float left = camera.position.x - halfWidth;
			float right = camera.position.x + halfWidth;
			float bottom = camera.position.y - halfHeight;
			float top = camera.position.y + halfHeight;

			float startX = (float) Math.floor(left / config.getStep()) * config.getStep();
			float startY = (float) Math.floor(bottom / config.getStep()) * config.getStep();

			for (float x = startX; x <= right; x += config.getStep()) {
				shapeRenderer.line(x, bottom, gridZ, x, top, gridZ);
			}

			for (float y = startY; y <= top; y += config.getStep()) {
				shapeRenderer.line(left, y, gridZ, right, y, gridZ);
			}
		} else {
			float left = camera.position.x - camera.viewportWidth / 2f;
			float right = camera.position.x + camera.viewportWidth / 2f;
			float bottom = camera.position.y - camera.viewportHeight / 2f;
			float top = camera.position.y + camera.viewportHeight / 2f;

			float startX = (float) Math.floor(left / config.getStep()) * config.getStep();
			float startY = (float) Math.floor(bottom / config.getStep()) * config.getStep();

			for (float x = startX; x <= right; x += config.getStep()) {
				shapeRenderer.line(x, bottom, gridZ, x, top, gridZ);
			}

			for (float y = startY; y <= top; y += config.getStep()) {
				shapeRenderer.line(left, y, gridZ, right, y, gridZ);
			}
		}

		shapeRenderer.end();

		Gdx.gl.glLineWidth(1f);
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}
	
	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	public void dispose() {
		if (shapeRenderer != null)
			shapeRenderer.dispose();
	}
}

package de.instinct.eqlibgdxutils.rendering.grid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.Shapes;
import de.instinct.eqlibgdxutils.rendering.ui.texture.shape.configs.shapes.EQRectangle;

public class GridRenderer {

	private final ShapeRenderer shapeRenderer;

	private GridConfiguration config;
	
	private Rectangle workingBounds;
	private EQRectangle workingShape;
	
	private float alpha = 1f;
	private Color finalColor = new Color();

	private static final float GRID_Z = 0f;

	public GridRenderer(GridConfiguration config) {
		this.shapeRenderer = new ShapeRenderer();
		this.config = config;
		
		workingBounds = new Rectangle();
		workingShape = EQRectangle.builder()
				.color(config.getLineColor())
				.filled(true)
				.build();
	}

	public void drawGrid(Camera camera) {
		if (config.getStep() <= 0) throw new IllegalStateException("Grid step must be greater than 0");
		if (camera.position.z == Float.POSITIVE_INFINITY) throw new IllegalStateException("Camera position z cannot be infinite");
		if (camera.position.z <= 0) throw new IllegalStateException("Camera position z cannot be 0 or negative");
		
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glLineWidth(config.getLineThickness() * GraphicsUtil.getHorizontalDisplayScaleFactor());

		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		finalColor.set(config.getLineColor());
		finalColor.a *= alpha;
		shapeRenderer.setColor(finalColor);

		float left, right, bottom, top;

		if (camera instanceof PerspectiveCamera) {
			Frustum frustum = camera.frustum;
			Vector3[] pts = frustum.planePoints;

			float minX = Float.MAX_VALUE, maxX = -Float.MAX_VALUE;
			float minY = Float.MAX_VALUE, maxY = -Float.MAX_VALUE;
			boolean anyHit = false;

			for (int i = 0; i < 8; i++) {
				Vector3 dir = new Vector3(pts[i]).sub(camera.position).nor();
				Ray ray = new Ray(camera.position, dir);
				Vector3 intersection = new Vector3();
				if (intersectRayPlane(ray, intersection)) {
					minX = Math.min(minX, intersection.x);
					maxX = Math.max(maxX, intersection.x);
					minY = Math.min(minY, intersection.y);
					maxY = Math.max(maxY, intersection.y);
					anyHit = true;
				}
			}

			if (!anyHit) {
				shapeRenderer.end();
				Gdx.gl.glLineWidth(1f);
				Gdx.gl.glDisable(GL20.GL_BLEND);
				return;
			}

			left = minX - config.getStep();
			right = maxX + config.getStep();
			bottom = minY - config.getStep();
			top = maxY + config.getStep();
		} else {
			left = camera.position.x - camera.viewportWidth / 2f;
			right = camera.position.x + camera.viewportWidth / 2f;
			bottom = camera.position.y - camera.viewportHeight / 2f;
			top = camera.position.y + camera.viewportHeight / 2f;
		}

		float startX = (float) Math.floor(left / config.getStep()) * config.getStep();
		float startY = (float) Math.floor(bottom / config.getStep()) * config.getStep();

		for (float x = startX; x <= right; x += config.getStep()) {
			shapeRenderer.line(x, bottom, GRID_Z, x, top, GRID_Z);
		}

		for (float y = startY; y <= top; y += config.getStep()) {
			shapeRenderer.line(left, y, GRID_Z, right, y, GRID_Z);
		}

		shapeRenderer.end();

		Gdx.gl.glLineWidth(1f);
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}

	private boolean intersectRayPlane(Ray ray, Vector3 out) {
		float denom = ray.direction.z;
		if (Math.abs(denom) < 1e-8f) return false;
		float t = (GRID_Z - ray.origin.z) / denom;
		if (t < 0) return false;
		out.set(ray.origin).mulAdd(ray.direction, t);
		return true;
	}
	
	public void drawGrid() {
		for (float x = 0; x <= GraphicsUtil.screenBounds().getWidth(); x += config.getStep()) {
			workingShape.setBounds(workingBounds.set(x, 0, config.getLineThickness(), GraphicsUtil.screenBounds().getHeight()));
			Shapes.draw(workingShape);
		}

		for (float y = 0; y <= GraphicsUtil.screenBounds().getHeight(); y += config.getStep()) {
			workingShape.setBounds(workingBounds.set(0, y, GraphicsUtil.screenBounds().getWidth(), config.getLineThickness()));
			Shapes.draw(workingShape);
		}
	}
	
	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	public void dispose() {
		if (shapeRenderer != null)
			shapeRenderer.dispose();
	}
}

package de.instinct.eqlibgdxutils.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class GridRenderer {

    private final ShapeRenderer shapeRenderer;
    private final Color lineColor;
    private final int gridRadius = 2000;
    private final int step = 50;
    private final float lineThickness = 1f;

    public GridRenderer() {
        this.shapeRenderer = new ShapeRenderer();
        this.lineColor = new Color(0f, 0.5f, 0f, 0.2f);
    }

    public void drawGrid(Camera camera) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glLineWidth(lineThickness);

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(lineColor);

        float left = camera.position.x - gridRadius;
        float right = camera.position.x + gridRadius;
        float bottom = camera.position.y - gridRadius;
        float top = camera.position.y + gridRadius;

        for (float x = left; x <= right; x += step) {
            shapeRenderer.line(x, bottom, 0f, x, top, 0f);
        }

        for (float y = bottom; y <= top; y += step) {
            shapeRenderer.line(left, y, 0f, right, y, 0f);
        }

        shapeRenderer.end();

        Gdx.gl.glLineWidth(1f);
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void dispose() {
        if (shapeRenderer != null) shapeRenderer.dispose();
    }
}

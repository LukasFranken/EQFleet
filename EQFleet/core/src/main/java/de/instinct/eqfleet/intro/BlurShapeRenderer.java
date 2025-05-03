package de.instinct.eqfleet.intro;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class BlurShapeRenderer {
	
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final FrameBuffer fboA;
    private final FrameBuffer fboB;
    private final ShaderProgram blurShaderH;
    private final ShaderProgram blurShaderV;
    private final float screenWidth;
    private final float screenHeight;

    public BlurShapeRenderer() {
        this.screenWidth = Gdx.graphics.getWidth();
        this.screenHeight = Gdx.graphics.getHeight();
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();

        String vert = Gdx.files.internal("shader/blur/blur.vs").readString();
        String frag = Gdx.files.internal("shader/blur/blur.fs").readString();
        blurShaderH = new ShaderProgram(vert, frag);
        blurShaderV = new ShaderProgram(vert, frag);
        if (!blurShaderH.isCompiled() || !blurShaderV.isCompiled()) {
            Gdx.app.error("Shader", "Error compiling blur shaders:\n" +
                blurShaderH.getLog() + "\n" + blurShaderV.getLog());
        }

        fboA = new FrameBuffer(Pixmap.Format.RGBA8888, (int)screenWidth, (int)screenHeight, false);
        fboB = new FrameBuffer(Pixmap.Format.RGBA8888, (int)screenWidth, (int)screenHeight, false);
        fboA.getColorBufferTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
        fboB.getColorBufferTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
    }

    public void drawBluredRectangle(Rectangle bounds, float lineWidth, float blurRadius) {
        // 1) Render outline into FBO A
        fboA.begin();
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1f, 1f, 1f, 1f);
        drawOutline(shapeRenderer, bounds, lineWidth);
        shapeRenderer.end();
        fboA.end();

        // 2) Horizontal blur: FBO A -> FBO B
        fboB.begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setShader(blurShaderH);
        blurShaderH.bind();
        blurShaderH.setUniformf("u_direction", 1f, 0f);
        blurShaderH.setUniformf("u_radius", blurRadius);
        batch.begin();
        batch.draw(fboA.getColorBufferTexture(), 0, 0, screenWidth, screenHeight);
        batch.end();
        fboB.end();

        // 3) Vertical blur: FBO B -> FBO A
        fboA.begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setShader(blurShaderV);
        blurShaderV.bind();
        blurShaderV.setUniformf("u_direction", 0f, 1f);
        blurShaderV.setUniformf("u_radius", blurRadius);
        batch.begin();
        batch.draw(fboB.getColorBufferTexture(), 0, 0, screenWidth, screenHeight);
        batch.end();
        fboA.end();

        // 4) Draw blurred texture onto screen with additive blending
        Gdx.gl.glEnable(GL20.GL_BLEND);
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        batch.setShader(null);
        batch.begin();
        batch.draw(fboA.getColorBufferTexture(), 0, 0, screenWidth, screenHeight);
        batch.end();
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // 5) Draw sharp outline on top
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1f, 1f, 1f, 1f);
        drawOutline(shapeRenderer, bounds, lineWidth);
        shapeRenderer.end();
    }

    /** Helper to draw a rectangle outline with specified line width */
    private void drawOutline(ShapeRenderer sr, Rectangle bounds, float lineWidth) {
        float x = bounds.x;
        float y = bounds.y;
        float w = bounds.width;
        float h = bounds.height;
        sr.rectLine(x, y, x + w, y, lineWidth);
        sr.rectLine(x + w, y, x + w, y + h, lineWidth);
        sr.rectLine(x + w, y + h, x, y + h, lineWidth);
        sr.rectLine(x, y + h, x, y, lineWidth);
    }

    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        fboA.dispose();
        fboB.dispose();
        blurShaderH.dispose();
        blurShaderV.dispose();
    }
}
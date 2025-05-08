package de.instinct.eqfleet.intro;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class BlurShapeRenderer {
	
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final FrameBuffer fbo;
    private final ShaderProgram blurShader;
    private final float screenWidth;
    private final float screenHeight;
    
    private static final float BLUR = 0.0002f;

    public BlurShapeRenderer() {
        this.screenWidth = Gdx.graphics.getWidth();
        this.screenHeight = Gdx.graphics.getHeight();
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();

        String vert = Gdx.files.internal("shader/blur/blur.vs").readString();
        String frag = Gdx.files.internal("shader/blur/blur.fs").readString();
        blurShader = new ShaderProgram(vert, frag);
        if (!blurShader.isCompiled()) {
            Gdx.app.error("Shader", "Error compiling blur shaders:\n" + blurShader.getLog());
        }

        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, (int)screenWidth, (int)screenHeight, false);
        fbo.getColorBufferTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
    }

    public void drawBlurredBox(Rectangle bounds, float blurRadius, Color fillColor) {
        fbo.begin();
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(fillColor);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        shapeRenderer.end();
        fbo.end();

        TextureRegion region = new TextureRegion(fbo.getColorBufferTexture());
        region.flip(false, true);

        batch.setShader(blurShader);
        batch.begin();
        blurShader.setUniformf("blurRadius", BLUR);
        blurShader.setUniformf("darkshift", 1.0f);
        batch.draw(region, bounds.x, bounds.y, bounds.width, bounds.height);
        batch.end();
        batch.setShader(null);
    }

    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        fbo.dispose();
        blurShader.dispose();
    }
}
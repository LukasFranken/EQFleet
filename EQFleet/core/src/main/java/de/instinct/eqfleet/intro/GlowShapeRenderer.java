package de.instinct.eqfleet.intro;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;

public class GlowShapeRenderer {
    private final SpriteBatch   batch;
    private final ComplexShapeRenderer shapes;
    private final FrameBuffer   fboA, fboB;
    private final ShaderProgram blurShader;
    
    private final float glow = 1f;
    private final float radius = 50f;
    private final float dropoff = 1f;

    public GlowShapeRenderer() {
        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();

        batch  = new SpriteBatch();
        shapes = new ComplexShapeRenderer();

        ShaderProgram.pedantic = false;
        String vs = Gdx.files.internal("shader/blur/blur.vs").readString();
        String fs = Gdx.files.internal("shader/blur/blur.fs").readString();
        blurShader = new ShaderProgram(vs, fs);
        if (!blurShader.isCompiled()) {
            throw new RuntimeException("Blur shader failed to compile:\n" + blurShader.getLog());
        }
        blurShader.bind();
        blurShader.setUniformf("u_radius", radius);
        blurShader.setUniformf("u_dropoff", dropoff);
        float texelX = 1f / Gdx.graphics.getWidth();
        float texelY = 1f / Gdx.graphics.getHeight();
        blurShader.setUniformf("u_texelSize", texelX, texelY);
        fboA = new FrameBuffer(Pixmap.Format.RGBA8888, w, h, false);
        fboB = new FrameBuffer(Pixmap.Format.RGBA8888, w, h, false);
        
        for (FrameBuffer f : new FrameBuffer[]{fboA, fboB}) {
            Texture t = f.getColorBufferTexture();
            t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
            t.setWrap(TextureWrap.ClampToEdge, TextureWrap.ClampToEdge);
        }
    }

    public void setProjectionMatrix(Matrix4 proj) {
        batch.setProjectionMatrix(proj);
        shapes.setProjectionMatrix(proj);
    }

    public Texture drawBlurredRectangle(Rectangle bounds, Color color) {
        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();

        drawBaseRect(bounds, color);
        
        fboB.begin();
        Gdx.gl.glClearColor(0, 0, 0, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        batch.setShader(blurShader);
        batch.begin();
        blurShader.setUniformf("u_strength", glow);
        batch.draw(fboA.getColorBufferTexture(), 0, 0, w, h);
        batch.end();
        batch.setShader(null);
        fboB.end();
        
        Texture glowTex = fboB.getColorBufferTexture();
        glowTex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        glowTex.setWrap(TextureWrap.ClampToEdge, TextureWrap.ClampToEdge);
        return glowTex;
    }

    private void drawBaseRect(Rectangle bounds, Color color) {
    	fboA.begin();
        Gdx.gl.glClearColor(0, 0, 0, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        shapes.begin(ShapeRenderer.ShapeType.Line);
        shapes.setColor(color);
        shapes.roundRectangle(bounds);
        shapes.end();
        fboA.end();
	}

	public void dispose() {
        batch.dispose();
        shapes.dispose();
        fboA.dispose();
        fboB.dispose();
        blurShader.dispose();
    }
}

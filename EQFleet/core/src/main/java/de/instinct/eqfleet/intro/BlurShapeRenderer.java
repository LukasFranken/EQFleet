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

public class BlurShapeRenderer {
    private final SpriteBatch   batch;
    private final ShapeRenderer shapes;
    private final FrameBuffer   fboA, fboB;
    private final ShaderProgram blurShader;

    public BlurShapeRenderer() {
        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();

        batch  = new SpriteBatch();
        shapes = new ShapeRenderer();

        ShaderProgram.pedantic = false;
        String vs = Gdx.files.internal("shader/blur/blur.vs").readString();
        String fs = Gdx.files.internal("shader/blur/blur.fs").readString();
        blurShader = new ShaderProgram(vs, fs);
        if (!blurShader.isCompiled()) {
            throw new RuntimeException("Blur shader failed to compile:\n" + blurShader.getLog());
        }

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

    public void drawBlurredRectangle(Rectangle bounds, float blurRadius) {
        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();

        fboA.begin();
        Gdx.gl.glClearColor(0, 0, 0, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        shapes.begin(ShapeRenderer.ShapeType.Line);
        shapes.setColor(Color.WHITE);
        shapes.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        shapes.end();
        fboA.end();

        blurShader.bind();
        blurShader.setUniformf("dir",        1f, 0f);
        blurShader.setUniformf("resolution", w);
        blurShader.setUniformf("radius",     blurRadius);
        batch.setShader(blurShader);
        fboB.begin();
        batch.begin();
        Gdx.gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MAG_FILTER, GL20.GL_LINEAR);
        batch.draw(fboA.getColorBufferTexture(), 0, 0, w, h);
        batch.end();
        fboB.end();
        batch.setShader(null);

        blurShader.bind();
        blurShader.setUniformf("dir",        0f, 1f);
        blurShader.setUniformf("resolution", h);
        blurShader.setUniformf("radius",     blurRadius);
        batch.setShader(blurShader);
        fboA.begin();
        batch.begin();
        Gdx.gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MAG_FILTER, GL20.GL_LINEAR);
        batch.draw(fboB.getColorBufferTexture(), 0, 0, w, h);
        batch.end();
        fboA.end();
        batch.setShader(null);

        float bleed = blurRadius * 4f;
        float dx = bounds.x - bleed;
        float dy = bounds.y - bleed;
        float dw = bounds.width  + bleed * 2f;
        float dh = bounds.height + bleed * 2f;

        float u0 = dx       / w, v0 = dy       / h;
        float u1 = (dx+dw) / w, v1 = (dy+dh) / h;

        batch.begin();
        batch.draw(
            fboA.getColorBufferTexture(),
            dx, dy,
            dw, dh,
            u0, v0, u1, v1 
        );
        batch.end();
    }

    public void dispose() {
        batch.dispose();
        shapes.dispose();
        fboA.dispose();
        fboB.dispose();
        blurShader.dispose();
    }
}

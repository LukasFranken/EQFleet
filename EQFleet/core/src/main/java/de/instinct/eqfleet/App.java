package de.instinct.eqfleet;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.utils.ScreenUtils;

import de.instinct.eqfleet.game.Game;
import de.instinct.eqfleet.game.backend.audio.AudioManager;
import de.instinct.eqfleet.intro.Intro;
import de.instinct.eqfleet.menu.Menu;
import de.instinct.eqfleet.net.WebManager;
import de.instinct.eqlibgdxutils.CursorUtil;
import de.instinct.eqlibgdxutils.PreferenceUtil;
import de.instinct.eqlibgdxutils.debug.DebugUtil;
import de.instinct.eqlibgdxutils.debug.logging.Logger;
import de.instinct.eqlibgdxutils.debug.metrics.DoubleMetric;
import de.instinct.eqlibgdxutils.rendering.model.ModelRenderer;
import de.instinct.eqlibgdxutils.rendering.particle.ParticleRenderer;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontUtil;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;

public class App extends ApplicationAdapter {
    
    private DoubleMetric fpsMetric;
    
    private static final String VERSION = "0.0.17";
    private static final String LOGTAG = "APP";

    @Override
    public void create() {
    	Logger.log(LOGTAG, "Welcome to EQFLEET v" + VERSION);
    	AudioManager.init();
    	FontUtil.init();
    	Gdx.input.setInputProcessor(new InputMultiplexer());
    	CursorUtil.createCursor();
    	PreferenceUtil.init("EQFleet");
    	WebManager.init();
        initializeDebugger();
        TextureManager.init();
        Intro.init();
        Game.init();
        Menu.init();
        ParticleRenderer.init();
        ModelRenderer.init();
    }

    private void initializeDebugger() {
    	DebugUtil.init();
    	if (GlobalStaticData.mode == ApplicationMode.DEV) {
    		DebugUtil.toggle();
    	}
        fpsMetric = DoubleMetric.builder()
        		.decimals(2)
        		.tag("fps")
        		.build();
        DebugUtil.register(fpsMetric);
	}

	@Override
    public void render() {
		ParticleRenderer.updateParticles();
        ScreenUtils.clear(0f, 0f, 0f, 1f);
        ParticleRenderer.renderParticles("stars");
        AudioManager.update();
        Intro.render();
        Menu.render();
        Game.render();
        updateMetrics();
    }

	private long lastFrameTimestamp;
    private void updateMetrics() {
    	fpsMetric.setValue(1000D / (double)(System.currentTimeMillis() - lastFrameTimestamp));
        DebugUtil.update(fpsMetric);
        DebugUtil.render();
        lastFrameTimestamp = System.currentTimeMillis();
	}

	@Override
    public void dispose() {
        DebugUtil.dispose();
        Menu.dispose();
        Game.dispose();
        TextureManager.dispose();
        AudioManager.dispose();
        Logger.log(LOGTAG, "EQFLEET TERMINATED");
    }
    
}
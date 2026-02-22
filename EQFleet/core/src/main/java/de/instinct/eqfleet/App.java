package de.instinct.eqfleet;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.utils.ScreenUtils;

import de.instinct.eqfleet.audio.AudioManager;
import de.instinct.eqfleet.language.LanguageManager;
import de.instinct.eqfleet.net.WebManager;
import de.instinct.eqfleet.scene.SceneManager;
import de.instinct.eqfleet.scene.SceneType;
import de.instinct.eqlibgdxutils.InputUtil;
import de.instinct.eqlibgdxutils.debug.console.Console;
import de.instinct.eqlibgdxutils.debug.logging.ConsoleColor;
import de.instinct.eqlibgdxutils.debug.logging.Logger;
import de.instinct.eqlibgdxutils.debug.metrics.types.NumberMetric;
import de.instinct.eqlibgdxutils.debug.profiler.Profiler;
import de.instinct.eqlibgdxutils.rendering.particle.ParticleRenderer;
import de.instinct.eqlibgdxutils.rendering.ui.popup.PopupRenderer;

public class App extends ApplicationAdapter {

    public static final String VERSION = "0.1.14";
    private final String LOGTAG = "APP";

    private boolean halted;

    @Override
    public void create() {
    	Logger.log(LOGTAG, "Welcome to EQFLEET v" + VERSION, ConsoleColor.YELLOW);
    	LibraryManager.init();
    	AudioManager.init();
    	LanguageManager.init();
    	Gdx.input.setInputProcessor(new InputMultiplexer());
    	WebManager.init();
    	SceneManager.init();
        Console.registerMetric(NumberMetric.builder()
        		.decimals(2)
        		.tag("this_frame_time_MS")
        		.build());
        GlobalStaticData.backgroundParticles = !PreferenceManager.load("bgparticles").contentEquals("false");
        GlobalStaticData.showDebugGrid = PreferenceManager.load("debuggrid").contentEquals("true");
        SceneManager.changeTo(SceneType.INTRO);
        Logger.log(LOGTAG, "Initialization completed", ConsoleColor.YELLOW);
    }
	
	@Override
    public void render() {
		long startNanoTime = System.nanoTime();
		InputUtil.update();
		ScreenUtils.clear(0f, 0f, 0f, 1f);
		try {
			if (!halted) {
				Profiler.startFrame("APP");
				AudioManager.update();
				Profiler.checkpoint("APP", "AudioManager");
				if (GlobalStaticData.backgroundParticles) {
					ParticleRenderer.updateParticles();
			        ParticleRenderer.renderParticles("stars");
			        Profiler.checkpoint("APP", "Particles");
				}
				SceneManager.update();
				Profiler.checkpoint("APP", "SceneUpdate");
				SceneManager.render();
				Profiler.checkpoint("APP", "SceneRender");
		        PopupRenderer.render();
		        Profiler.checkpoint("APP", "PopupRenderer");
		        Profiler.endFrame("APP");
			}
		} catch (Exception e) {
			Logger.log(LOGTAG, e);
			halted = true;
		}
        long endNanoTime = System.nanoTime();
        double deltaTime = (endNanoTime - startNanoTime) / 1_000_000.0;
        Console.updateMetric("this_frame_time_MS", deltaTime);
        Console.render();
    }

	@Override
    public void dispose() {
		SceneManager.dispose();
		WebManager.dispose();
        AudioManager.dispose();
        LibraryManager.dispose();
        Logger.log(LOGTAG, "EQFLEET TERMINATED", ConsoleColor.YELLOW);
    }

}

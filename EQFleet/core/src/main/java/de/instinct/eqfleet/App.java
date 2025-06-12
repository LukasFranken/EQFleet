package de.instinct.eqfleet;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.utils.ScreenUtils;

import de.instinct.eqfleet.game.Game;
import de.instinct.eqfleet.game.backend.audio.AudioManager;
import de.instinct.eqfleet.intro.Intro;
import de.instinct.eqfleet.menu.main.Menu;
import de.instinct.eqfleet.net.WebManager;
import de.instinct.eqlibgdxutils.CursorUtil;
import de.instinct.eqlibgdxutils.InputUtil;
import de.instinct.eqlibgdxutils.PreferenceUtil;
import de.instinct.eqlibgdxutils.debug.console.Console;
import de.instinct.eqlibgdxutils.debug.logging.ConsoleColor;
import de.instinct.eqlibgdxutils.debug.logging.Logger;
import de.instinct.eqlibgdxutils.debug.metrics.DoubleMetric;
import de.instinct.eqlibgdxutils.rendering.model.ModelRenderer;
import de.instinct.eqlibgdxutils.rendering.particle.ParticleRenderer;
import de.instinct.eqlibgdxutils.rendering.ui.font.FontUtil;
import de.instinct.eqlibgdxutils.rendering.ui.popup.PopupRenderer;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;

public class App extends ApplicationAdapter {
	
    public static final String VERSION = "0.0.22";
    private static final String LOGTAG = "APP";
    
    private static boolean halted;

    @Override
    public void create() {
    	Logger.log(LOGTAG, "Welcome to EQFLEET v" + VERSION, ConsoleColor.YELLOW);
    	TextureManager.init();
        TextureManager.setDefaultGlowRadius(2f);
    	Console.init();
    	AudioManager.init();
    	FontUtil.init();
    	Gdx.input.setInputProcessor(new InputMultiplexer());
    	CursorUtil.createCursor();
    	PreferenceUtil.init("EQFleet");
    	WebManager.init();
        Intro.init();
        Menu.init();
        Game.init();
        ParticleRenderer.init();
        ModelRenderer.init();
        
        Console.registerMetric(DoubleMetric.builder()
        		.decimals(2)
        		.tag("this_frame_time_MS")
        		.build());
    }

	@Override
    public void render() {
		long startNanoTime = System.nanoTime();
		
		try {
			InputUtil.update();
			if (!halted) {
				AudioManager.update();
				ScreenUtils.clear(0f, 0f, 0f, 1f);
				ParticleRenderer.updateParticles();
		        ParticleRenderer.renderParticles("stars");
		        Intro.render();
		        Menu.render();
		        Game.render();
		        PopupRenderer.render();
			}
		} catch (Exception e) {
			Logger.log("APP", e.getMessage(), ConsoleColor.RED);
			e.printStackTrace();
			halted = true;
		}
        
        long endNanoTime = System.nanoTime();
        Console.updateMetric("this_frame_time_MS", (endNanoTime - startNanoTime) / 1_000_000.0);
        Console.render();
    }

	@Override
    public void dispose() {
        Game.dispose();
        Menu.dispose();
        TextureManager.dispose();
        AudioManager.dispose();
        ModelRenderer.dispose();
        WebManager.dispose();
        Console.dispose();
        Logger.log(LOGTAG, "EQFLEET TERMINATED", ConsoleColor.YELLOW);
    }
    
}
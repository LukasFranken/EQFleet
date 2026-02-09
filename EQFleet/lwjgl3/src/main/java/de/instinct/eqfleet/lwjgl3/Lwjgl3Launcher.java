package de.instinct.eqfleet.lwjgl3;

import com.badlogic.gdx.Graphics.Monitor;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.glutils.HdpiMode;
import com.badlogic.gdx.math.Vector2;

import de.instinct.eqfleet.App;

public class Lwjgl3Launcher {
	
	private static Vector2 windowSize = new Vector2(400, 900);
	//private static Vector2 windowSize = new Vector2(500, 1125);
	//private static Vector2 windowSize = new Vector2(600, 1350);
	
    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return;
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new App(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("Equilibrium");
        configuration.setForegroundFPS(60);
        configuration.setResizable(false);
        configuration.setWindowedMode((int)windowSize.x, (int)windowSize.y);
        configuration.setHdpiMode(HdpiMode.Pixels);
        configuration.setBackBufferConfig(8, 8, 8, 8, 16, 8, 4);
        configuration.setWindowIcon("eq_128.png", "eq_64.png", "eq_32.png", "eq_16.png");
        
        int targetMonitorIndex = 2;
        Monitor[] monitors = Lwjgl3ApplicationConfiguration.getMonitors();
        if (monitors != null && monitors.length > 0) {
            int idx = Math.max(0, Math.min(targetMonitorIndex, monitors.length - 1));
            int targetX = monitors[idx].virtualX + Lwjgl3ApplicationConfiguration.getDisplayMode(monitors[idx]).width / 2 - (int)windowSize.x / 2;
            int targetY = monitors[idx].virtualY + Lwjgl3ApplicationConfiguration.getDisplayMode(monitors[idx]).height / 2 - (int)windowSize.y / 2;
            configuration.setWindowPosition(targetX, targetY);
        }
        
        return configuration;
    }
    
}
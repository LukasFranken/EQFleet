package de.instinct.eqfleet.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import de.instinct.eqfleet.App;

public class Lwjgl3Launcher {
	
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
        configuration.setWindowedMode(400, 900);
        //configuration.setWindowedMode(600, 1350);
        configuration.setBackBufferConfig(8, 8, 8, 8, 16, 8, 4);
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
        return configuration;
    }
    
}
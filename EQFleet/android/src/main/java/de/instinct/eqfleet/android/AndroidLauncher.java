package de.instinct.eqfleet.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.math.Vector2;

import de.instinct.eqfleet.App;

/** Launches the Android application. */
public class AndroidLauncher extends AndroidApplication {
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
        configuration.useImmersiveMode = false;
        initialize(new App(), configuration);
    }
}

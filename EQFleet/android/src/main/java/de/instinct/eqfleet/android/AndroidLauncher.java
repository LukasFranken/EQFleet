package de.instinct.eqfleet.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.math.Vector2;

import de.instinct.eqfleet.App;
import de.instinct.eqfleet.status.BatteryStatus;

/** Launches the Android application. */
public class AndroidLauncher extends AndroidApplication {
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
        configuration.useImmersiveMode = false;
        initialize(new App(new BatteryStatus() {
			
			@Override
			public float percentage() {
				IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		        Intent batteryStatus = this.registerReceiver(null, filter);
		        if (batteryStatus == null) return -1;
		        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
		        if (level == -1 || scale == -1) return -1;
		        return level / (float) scale;
			}
			
		}), configuration);
    }
}

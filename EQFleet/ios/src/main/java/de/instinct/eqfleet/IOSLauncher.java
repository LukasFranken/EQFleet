package de.instinct.eqfleet;

import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIDevice;
import org.robovm.apple.uikit.UIDeviceBatteryState;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;
import com.badlogic.gdx.graphics.glutils.HdpiMode;

import de.instinct.eqfleet.status.BatteryStatus;

public class IOSLauncher extends IOSApplication.Delegate {

	@Override
	protected IOSApplication createApplication() {
		IOSApplicationConfiguration configuration = new IOSApplicationConfiguration();
		configuration.orientationLandscape = false;
		configuration.hdpiMode = HdpiMode.Pixels;
		configuration.audioDeviceBufferSize = 8192;
		configuration.audioDeviceBufferCount = 15;
		configuration.preferredFramesPerSecond = 120;
		UIDevice device = UIDevice.getCurrentDevice();
        device.setBatteryMonitoringEnabled(true);
		return new IOSApplication(new App(new BatteryStatus() {
			
			@Override
			public float percentage() {
		        float level = device.getBatteryLevel();
		        if (level < 0) return -1;
		        return level;
			}

			@Override
			public boolean isCharging() {
				return device.getBatteryState() == UIDeviceBatteryState.Charging;
			}
			
		}), configuration);
	}

	public static void main(String[] argv) {
		NSAutoreleasePool pool = new NSAutoreleasePool();
		UIApplication.main(argv, null, IOSLauncher.class);
		pool.close();
	}

}
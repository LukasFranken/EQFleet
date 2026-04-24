package de.instinct.eqfleet.status;

import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqfleet.status.element.BatteryStatusComponent;
import de.instinct.eqfleet.status.element.ClockStatusComponent;
import de.instinct.eqfleet.status.element.FPSStatusComponent;
import de.instinct.eqfleet.status.element.PingStatusComponent;
import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.PlatformUtil;

public class StatusRenderer {
	
	private static Rectangle statusBarBounds;
	
	private static ClockStatusComponent clock;
	private static FPSStatusComponent fps;
	private static PingStatusComponent ping;
	private static BatteryStatusComponent battery;
	
	public static void init() {
		statusBarBounds = new Rectangle();
		clock = new ClockStatusComponent();
		fps = new FPSStatusComponent();
		ping = new PingStatusComponent();
		battery = new BatteryStatusComponent();
	}
	
	public static void render() {
		if (PlatformUtil.isIOS()) {
			statusBarBounds.set(30, GraphicsUtil.screenBounds().height - 32, GraphicsUtil.screenBounds().width - 60, 12);
		}
		if (PlatformUtil.isAndroid()) {
			statusBarBounds.set(30, GraphicsUtil.screenBounds().height - 32, GraphicsUtil.screenBounds().width - 60, 12);
		}
		if (PlatformUtil.isDesktop()) {
			statusBarBounds.set(10, GraphicsUtil.screenBounds().height - 20, GraphicsUtil.screenBounds().width - 10, 20);
		}
		clock.setBounds(statusBarBounds.x, statusBarBounds.y, 30, statusBarBounds.height);
		clock.render();
		ping.setBounds(statusBarBounds.x + 50, statusBarBounds.y, 20, statusBarBounds.height);
		ping.render();
		fps.setBounds(statusBarBounds.x + statusBarBounds.width - 70, statusBarBounds.y, 20, statusBarBounds.height);
		fps.render();
		if (StatusModel.batteryStatus.percentage() != -1) {
			battery.setBounds(statusBarBounds.x + statusBarBounds.width - 30, statusBarBounds.y, 30, statusBarBounds.height);
			battery.render();
		};
	}

}
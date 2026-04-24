package de.instinct.eqlibgdxutils;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

public class PlatformUtil {
	
	public static boolean isMobile() {
		return Gdx.app.getType() == Application.ApplicationType.Android || Gdx.app.getType() == Application.ApplicationType.iOS;
	}
	
	public static boolean isIOS() {
		return Gdx.app.getType() == Application.ApplicationType.iOS;
	}
	
	public static boolean isAndroid() {
		return Gdx.app.getType() == Application.ApplicationType.Android;
	}
	
	public static boolean isDesktop() {
		return Gdx.app.getType() == Application.ApplicationType.Desktop;
	}

}

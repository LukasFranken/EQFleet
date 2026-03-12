package de.instinct.eqlibgdxutils;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

public class PlatformUtil {
	
	public static boolean isMobile() {
		return Gdx.app.getType() == Application.ApplicationType.Android || Gdx.app.getType() == Application.ApplicationType.iOS;
	}

}

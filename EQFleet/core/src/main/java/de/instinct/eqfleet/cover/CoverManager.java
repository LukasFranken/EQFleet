package de.instinct.eqfleet.cover;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqfleet.audio.AudioManager;
import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.MathUtil;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.image.Image;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;

public class CoverManager {
	
	private static float elapsed;
	private static boolean active;
	
	private static Image coverImage;
	
	public static void start() {
		active = true;
		AudioManager.playMusic("infinite_future_short", false);
		AudioManager.queueMusic("eqspace2");
		
		load();
	}
	
	private static void load() {
		coverImage = new Image(TextureManager.getTexture("ui/image", "eq_logo_full_rect"));
		coverImage.setBounds(new Rectangle(50, GraphicsUtil.screenBounds().height / 2 - ((GraphicsUtil.screenBounds().width - 100) / 2), GraphicsUtil.screenBounds().width - 100, GraphicsUtil.screenBounds().width - 100));
	}
	
	public static void update() {
		if (active) {
			elapsed += Gdx.graphics.getDeltaTime();
			render();
			if (elapsed > 59f) {
				finish();
			}
		}
	}

	private static void render() {
		coverImage.setAlpha(0f);
		if (elapsed > 8f) {
			coverImage.setAlpha(MathUtil.easeInOut(0f, 1f, (elapsed - 8f) / 1.5f));
			if (elapsed > 12f) {
				coverImage.setAlpha(MathUtil.easeInOut(1f, 0f, (elapsed - 13f) / 1.5f));
			}
		}
		coverImage.render();
	}
	
	private static void finish() {
		active = false;
	}

	public static int getDuration() {
		return 59_000;
	}

}

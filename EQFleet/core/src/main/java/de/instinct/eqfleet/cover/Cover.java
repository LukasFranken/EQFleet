package de.instinct.eqfleet.cover;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;

import de.instinct.eqfleet.audio.AudioManager;
import de.instinct.eqfleet.scene.Scene;
import de.instinct.eqfleet.scene.SceneManager;
import de.instinct.eqfleet.scene.SceneType;
import de.instinct.eqlibgdxutils.GraphicsUtil;
import de.instinct.eqlibgdxutils.MathUtil;
import de.instinct.eqlibgdxutils.rendering.ui.component.passive.image.Image;
import de.instinct.eqlibgdxutils.rendering.ui.texture.TextureManager;

public class Cover extends Scene {
	
	private final int TOTAL_DURATION = 50_000;
	
	private float elapsed;
	
	private static Image coverImage;
	
	@Override
	public void init() {
		coverImage = new Image(TextureManager.getTexture("ui/image", "eq_logo_full_rect"));
		coverImage.setBounds(new Rectangle(50, GraphicsUtil.screenBounds().height / 2 - ((GraphicsUtil.screenBounds().width - 100) / 2), GraphicsUtil.screenBounds().width - 100, GraphicsUtil.screenBounds().width - 100));
	}
	
	@Override
	public void open() {
		elapsed = 0f;
		AudioManager.playMusic("to_the_stars_short", false);
		AudioManager.queueMusic("eqspace2");
	}
	
	@Override
	public void update() {
		elapsed += Gdx.graphics.getDeltaTime();
		if (elapsed > TOTAL_DURATION / 1000) {
			finish();
		}
	}

	private void finish() {
		SceneManager.changeTo(SceneType.MENU);
	}

	@Override
	public void render() {
		coverImage.setAlpha(0f);
		if (elapsed > 45f) {
			coverImage.setAlpha(MathUtil.easeInOut(0f, 1f, (elapsed - 45f) / 1.5f));
			if (elapsed > 49f) {
				coverImage.setAlpha(MathUtil.easeInOut(1f, 0f, (elapsed - 49f) / 1.5f));
			}
		}
		coverImage.render();
	}
	
	@Override
	public void close() {
		elapsed = 0f;
	}

	@Override
	public void dispose() {
		coverImage.dispose();
	}

}

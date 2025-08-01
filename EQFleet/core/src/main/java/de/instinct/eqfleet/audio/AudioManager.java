package de.instinct.eqfleet.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

import de.instinct.eqlibgdxutils.MathUtil;
import de.instinct.eqlibgdxutils.generic.cache.Cache;
import de.instinct.eqlibgdxutils.generic.cache.model.LoadSequence;

public class AudioManager {
	
	private static Music currentMusic;
	private static Music queuedInMusic;
	private static Cache<Music> voices;
	
	private static float targetMusicVolume = 0.1f;
	private static final float swapDuration = 3f;
	private static float currentSwapElapsed = 0f;
	
	public static void init() {
		voices = new Cache<>(new LoadSequence<Music>() {

			@Override
			public Music execute(String tag) {
				return Gdx.audio.newMusic(Gdx.files.internal("audio/voice/" + tag + ".mp3"));
			}
			
		});
	}
	
	public static void playMusic(String tag, boolean loop) {
		if (currentMusic == null) {
			currentMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/music/" + tag + ".mp3"));
			currentMusic.setVolume(targetMusicVolume);
			currentMusic.setLooping(loop);
			currentMusic.play();
		} else {
			queuedInMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/music/" + tag + ".mp3"));
			queuedInMusic.setVolume(0f);
			queuedInMusic.setLooping(loop);
			queuedInMusic.play();
		}
	}
	
	public static void stop() {
		if (currentMusic != null && currentMusic.isPlaying()) {
			currentMusic.stop();
			currentMusic = null;
		}
	}
	
	public static void update() {
		if (queuedInMusic != null && currentMusic != null) {
			float currentVolume = MathUtil.linear(0f, targetMusicVolume, currentSwapElapsed / swapDuration);
			queuedInMusic.setVolume(currentVolume);
			currentMusic.setVolume(targetMusicVolume - currentVolume);
			if (currentSwapElapsed >= swapDuration) {
				currentMusic.stop();
				currentMusic = queuedInMusic;
				queuedInMusic = null;
			}
			currentSwapElapsed += Gdx.graphics.getDeltaTime();
		}
	}
	
	public static void playVoice(String tag) {
		Music voice  = voices.get(tag);
		if (voice != null) {
			voice.setVolume(1);
			voice.setLooping(false);
			voice.play();
		}
	}
	
	public static void dispose() {
		
	}

}

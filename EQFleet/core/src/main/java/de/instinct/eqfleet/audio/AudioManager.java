package de.instinct.eqfleet.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import de.instinct.eqlibgdxutils.MathUtil;
import de.instinct.eqlibgdxutils.debug.logging.ConsoleColor;
import de.instinct.eqlibgdxutils.debug.logging.Logger;
import de.instinct.eqlibgdxutils.generic.cache.Cache;
import de.instinct.eqlibgdxutils.generic.cache.model.LoadSequence;

public class AudioManager {
	
	private static final String LOGTAG = "AUDIO";

	private static Music currentMusic;
	private static Music queuedInMusic;
	private static Cache<Music> voices;
	private static Cache<Sound> sfxs;

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
		sfxs = new Cache<>(new LoadSequence<Sound>() {

			@Override
			public Sound execute(String tag) {
				return Gdx.audio.newSound(Gdx.files.internal("audio/sfx/" + tag + ".mp3"));
			}

		});
	}

	public static void playMusic(String tag, boolean loop) {
		Logger.log(LOGTAG, "Loading music: " + tag, ConsoleColor.YELLOW);
		if (currentMusic == null) {
			currentMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/music/" + tag + ".mp3"));
			currentMusic.setVolume(targetMusicVolume);
			currentMusic.setLooping(loop);
			currentMusic.play();
		} else {
			queuedInMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/music/" + tag + ".mp3"));
			queuedInMusic.setVolume(0f);
			queuedInMusic.setLooping(loop);
			try {
				queuedInMusic.play();
			} catch (Exception e) {
				Gdx.app.error("AudioManager", "Failed to play queued music: " + tag, e);
				Logger.log(LOGTAG, "Failed to play queued music: " + tag, ConsoleColor.YELLOW);
			}
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
				currentMusic.dispose();
				currentMusic = queuedInMusic;
				queuedInMusic = null;
			}
			currentSwapElapsed += Gdx.graphics.getDeltaTime();
		}
	}

	public static void playVoice(String tag) {
		Music voice = voices.get(tag);
		if (voice != null) {
			voice.setVolume(1f);
			voice.setLooping(false);
			voice.play();
		}
	}

	public static void playSfx(String tag) {
		Sound sfx = sfxs.get(tag);
		if (sfx != null) {
			sfx.play(0.1f);
		}
	}

	public static void stopAllSfx() {
		for (Sound sfx : sfxs.getAllLoadedElements()) sfx.stop();
	}
	
	public static void stopAllVoices() {
		for (Music voice : voices.getAllLoadedElements()) voice.stop();
	}

	public static void dispose() {
		for (Sound sfx : sfxs.getAllLoadedElements()) sfx.dispose();
		for (Music voice : voices.getAllLoadedElements()) voice.dispose();
	}

}

package de.instinct.eqfleet.audio;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import de.instinct.eqfleet.PreferenceManager;
import de.instinct.eqlibgdxutils.MathUtil;
import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.debug.logging.ConsoleColor;
import de.instinct.eqlibgdxutils.debug.logging.Logger;
import de.instinct.eqlibgdxutils.generic.cache.Cache;
import de.instinct.eqlibgdxutils.generic.cache.model.LoadSequence;

public class AudioManager {
	
	private static final String LOGTAG = "AUDIO";

	private static Music currentMusic;
	private static Music queuedInMusic;
	private static Music currentVoice;
	private static List<String> availableRadioTracks;
	private static Cache<Music> voices;
	private static Cache<Sound> sfxs;

	private static float targetMusicVolume = 0.5f;
	private static final float swapDuration = 5f;
	private static float currentSwapElapsed = 0f;
	
	private static float userMusicVolume = 0.5f;
	private static float userVoiceVolume = 0.5f;
	private static float userSfxVolume = 0.5f;
	
	private static boolean radioMode;

	public static void init() {
		availableRadioTracks = new ArrayList<>();
		availableRadioTracks.add("eqspace1");
		availableRadioTracks.add("eqspace2");
		availableRadioTracks.add("eqspace3");
		availableRadioTracks.add("eqspace4");
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
		String musicVolumePrefString = PreferenceManager.load("musicvolume");
		if (!musicVolumePrefString.isEmpty()) {
			userMusicVolume = Float.parseFloat(musicVolumePrefString);
		}
		String voiceVolumePrefString = PreferenceManager.load("voicevolume");
		if (!voiceVolumePrefString.isEmpty()) {
			userVoiceVolume = Float.parseFloat(voiceVolumePrefString);
		}
		String sfxVolumePrefString = PreferenceManager.load("sfxvolume");
		if (!sfxVolumePrefString.isEmpty()) {
			userSfxVolume = Float.parseFloat(sfxVolumePrefString);
		}
	}

	public static void playMusic(String tag, boolean loop) {
		Logger.log(LOGTAG, "Loading music: " + tag, ConsoleColor.YELLOW);
		if (currentMusic == null) {
			currentMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/music/" + tag + ".mp3"));
			currentMusic.setVolume(targetMusicVolume * userMusicVolume);
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
	
	public static void startRadio() {
		radioMode = true;
	}

	public static void stop() {
		if (currentMusic != null && currentMusic.isPlaying()) {
			currentMusic.stop();
			currentMusic = null;
		}
	}

	public static void update() {
		if (radioMode) {
			if (currentMusic == null || !currentMusic.isPlaying()) {
				String tag = availableRadioTracks.get(new Random().nextInt(4));
				currentMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/music/" + tag + ".mp3"));
				currentMusic.setVolume(targetMusicVolume * userMusicVolume);
				currentMusic.setLooping(false);
				currentMusic.play();
				Logger.log(LOGTAG, "Loading music: " + tag, ConsoleColor.YELLOW);
			}
		} else {
			if (queuedInMusic != null && currentMusic != null) {
				float currentVolume = MathUtil.linear(0f, targetMusicVolume * userMusicVolume, currentSwapElapsed / swapDuration);
				queuedInMusic.setVolume(currentVolume);
				currentMusic.setVolume((targetMusicVolume * userMusicVolume) - currentVolume);
				if (currentSwapElapsed >= swapDuration) {
					currentMusic.stop();
					currentMusic.dispose();
					currentMusic = queuedInMusic;
					queuedInMusic = null;
				}
				currentSwapElapsed += Gdx.graphics.getDeltaTime();
			}
		}
	}

	public static void playVoice(String tag) {
		Music voice = voices.get(tag);
		if (voice != null) {
			if (currentVoice != null) {
				currentVoice.stop();
			}
			voice.setVolume(1f * userVoiceVolume);
			voice.setLooping(false);
			voice.play();
			currentVoice = voice;
		}
	}

	public static void playSfx(String tag) {
		Sound sfx = sfxs.get(tag);
		if (sfx != null) {
			sfx.play(0.5f * userSfxVolume);
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

	public static void updateUserMusicVolume(float newValue) {
		userMusicVolume = newValue;
		if (currentMusic != null) {
			currentMusic.setVolume(targetMusicVolume * userMusicVolume);
		}
	}
	
	public static void updateUserVoiceVolume(float newValue) {
		userVoiceVolume = newValue;
	}
	
	public static void updateUserSfxVolume(float newValue) {
		userSfxVolume = newValue;
	}

	public static float getUserMusicVolume() {
		return userMusicVolume;
	}
	
	public static float getUserVoiceVolume() {
		return userVoiceVolume;
	}
	
	public static float getUserSfxVolume() {
		return userSfxVolume;
	}

	public static void saveUserMusicVolume(float currentValue) {
		PreferenceManager.save("musicvolume", StringUtils.format(currentValue, 2));
	}
	
	public static void saveUserVoiceVolume(float currentValue) {
		PreferenceManager.save("voicevolume", StringUtils.format(currentValue, 2));
	}
	
	public static void saveUserSfxVolume(float currentValue) {
		PreferenceManager.save("sfxvolume", StringUtils.format(currentValue, 2));
	}

}

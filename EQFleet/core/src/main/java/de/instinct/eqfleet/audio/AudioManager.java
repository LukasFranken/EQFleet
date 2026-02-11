package de.instinct.eqfleet.audio;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;

import de.instinct.eqfleet.PreferenceManager;
import de.instinct.eqlibgdxutils.MathUtil;
import de.instinct.eqlibgdxutils.StringUtils;
import de.instinct.eqlibgdxutils.debug.logging.ConsoleColor;
import de.instinct.eqlibgdxutils.debug.logging.Logger;
import de.instinct.eqlibgdxutils.generic.cache.Cache;
import de.instinct.eqlibgdxutils.generic.cache.model.LoadSequence;
import de.instinct.eqlibgdxutils.net.ObjectJSONMapper;

public class AudioManager {

	private static final String LOGTAG = "AUDIO";

	private static Music currentMusic;
	private static Music transitionedInMusic;
	private static Music queuedInMusic;
	private static Music currentVoice;

	private static List<String> availableRadioTracks;
	private static List<String> availableNonRadioTracks;
	private static Map<String, AudioMetaData> voiceMetaDatas;

	private static Cache<Music> voices;
	private static Cache<Sound> sfxs;
	private static Cache<Music> musics;

	private static float targetMusicVolume = 0.4f;
	private static final float swapDuration = 5f;
	private static float currentSwapElapsed = 0f;

	private static float userMusicVolume = 0.5f;
	private static float userVoiceVolume = 0.5f;
	private static float userSfxVolume = 0.5f;

	private static boolean radioMode;

	private static final Random RNG = new Random();
	private static int lastPlayedRadioTrackIdx = -1;

	public static void init() {
		voiceMetaDatas = new HashMap<>();
		availableRadioTracks = new ArrayList<>();
		availableRadioTracks.add("eqspace1");
		availableRadioTracks.add("eqspace2");
		availableRadioTracks.add("eqspace3");
		availableRadioTracks.add("eqspace4");
		availableRadioTracks.add("infinite_future");
		availableRadioTracks.add("to_the_stars");
		availableRadioTracks.add("to_the_stars_funk");
		availableRadioTracks.add("to_the_stars_ambient");
		availableRadioTracks.add("to_the_stars_disco");
		availableRadioTracks.add("neon_horizon_ambient");
		availableRadioTracks.add("to_the_stars_70s");
		availableRadioTracks.add("to_the_stars_synth");
		
		availableNonRadioTracks = new ArrayList<>();
		availableNonRadioTracks.add("to_the_stars_short");

		musics = new Cache<>(new LoadSequence<Music>() {
			@Override
			public Music execute(String tag) {
				return Gdx.audio.newMusic(Gdx.files.internal("audio/music/" + tag + ".mp3"));
			}
		});

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
		if (!musicVolumePrefString.isEmpty()) userMusicVolume = Float.parseFloat(musicVolumePrefString);

		String voiceVolumePrefString = PreferenceManager.load("voicevolume");
		if (!voiceVolumePrefString.isEmpty()) userVoiceVolume = Float.parseFloat(voiceVolumePrefString);

		String sfxVolumePrefString = PreferenceManager.load("sfxvolume");
		if (!sfxVolumePrefString.isEmpty()) userSfxVolume = Float.parseFloat(sfxVolumePrefString);

		for (String tag : availableRadioTracks) {
			musics.get(tag);
		}
		
		for (String tag : availableNonRadioTracks) {
			musics.get(tag);
		}
	}

	public static void playMusic(String tag, boolean loop) {
		Logger.log(LOGTAG, "Loading music: " + tag, ConsoleColor.YELLOW);

		Music next = musics.get(tag);
		if (next == null) return;

		next.setLooping(loop);

		if (currentMusic == null) {
			currentMusic = next;
			currentMusic.setVolume(targetMusicVolume * userMusicVolume);
			currentMusic.play();
			Logger.log(LOGTAG, "Playing new music: " + tag, ConsoleColor.YELLOW);
			return;
		}

		transitionedInMusic = next;
		transitionedInMusic.setVolume(0f);

		currentSwapElapsed = 0f;

		try {
			transitionedInMusic.play();
			Logger.log(LOGTAG, "Playing queued music: " + tag, ConsoleColor.YELLOW);
		} catch (Exception e) {
			Gdx.app.error("AudioManager", "Failed to play queued music: " + tag, e);
			Logger.log(LOGTAG, "Failed to play queued music: " + tag, ConsoleColor.YELLOW);
		}
	}
	
	public static void queueMusic(String tag) {
		Logger.log(LOGTAG, "Queuing music: " + tag, ConsoleColor.YELLOW);

		Music next = musics.get(tag);
		if (next == null) return;

		queuedInMusic = next;
		queuedInMusic.setVolume(0f);
	}

	public static void startRadio() {
		radioMode = true;
	}

	public static void stop() {
		radioMode = false;
		if (transitionedInMusic != null) {
			transitionedInMusic.stop();
			transitionedInMusic = null;
		}
		if (currentMusic != null) {
			currentMusic.stop();
			currentMusic = null;
		}
		currentSwapElapsed = 0f;
	}

	public static void update() {
		if (radioMode) {
			if (currentMusic == null || !currentMusic.isPlaying()) {
				Music next = null;
				
				if (queuedInMusic != null) {
					next = queuedInMusic;
					queuedInMusic = null;
					Logger.log(LOGTAG, "Updating music to queued in", ConsoleColor.YELLOW);
				} else {
					int idx = -1;
					do {
						idx = RNG.nextInt(availableRadioTracks.size());
						if (availableRadioTracks.size() == 1) break;
					} while (idx == lastPlayedRadioTrackIdx);
					String tag = availableRadioTracks.get(idx);
					lastPlayedRadioTrackIdx = idx;
					next = musics.get(tag);
					Logger.log(LOGTAG, "Updating music to random: " + tag, ConsoleColor.YELLOW);
				}
				
				if (next == null) return;

				currentMusic = next;
				currentMusic.setVolume(targetMusicVolume * userMusicVolume);
				currentMusic.setLooping(false);
				currentMusic.play();

				
			}
		}

		if (transitionedInMusic != null && currentMusic != null) {
			float ratioQueued = (currentSwapElapsed - (swapDuration / 2)) / (swapDuration / 2f);
			float queuedVolume = MathUtil.linear(0f, targetMusicVolume * userMusicVolume, ratioQueued);
			float ratioCurrent = (currentSwapElapsed / swapDuration) * 2f;
			float currentVolume = MathUtil.linear(targetMusicVolume * userMusicVolume, 0f, ratioCurrent);
			transitionedInMusic.setVolume(queuedVolume);
			currentMusic.setVolume(currentVolume);

			if (currentSwapElapsed >= swapDuration) {
				currentMusic.stop();
				currentMusic = transitionedInMusic;
				transitionedInMusic = null;
				currentSwapElapsed = 0f;
			}

			currentSwapElapsed += Gdx.graphics.getDeltaTime();
		}
	}

	public static void playVoice(String category, String tag) {
		String categoryPath = category;
		if (!category.contentEquals("")) categoryPath += "/";
		Music voice = voices.get(categoryPath + tag);
		if (voice != null) {
			if (currentVoice != null) currentVoice.stop();
			voice.setVolume(1f * userVoiceVolume * (category.contains("tutorial") ? 0.7f : 1f));
			voice.setLooping(false);
			voice.play();
			currentVoice = voice;
		}
	}
	
	public static float getVoiceDuration(String category, String tag) {
		AudioMetaData metaData = voiceMetaDatas.get(category);
		if (!voiceMetaDatas.containsKey(category)) {
			metaData = loadVoiceMetaData(category);
		}
		if (metaData == null) {
			Logger.log(LOGTAG, "Failed to load metadata for voice category: " + category, ConsoleColor.RED);
			return 0f;
		}
		if (!metaData.getDurations().containsKey(tag)) {
			Logger.log(LOGTAG, "Failed to find duration for voice tag: " + tag + " in category: " + category, ConsoleColor.RED);
			return 0f;
		}
		return metaData.getDurations().get(tag);
	}

	private static AudioMetaData loadVoiceMetaData(String category) {
		String categoryPath = "audio/voice/" + category;
		if (!category.contentEquals("")) categoryPath += "/";
		FileHandle fh = Gdx.files.internal(categoryPath + "metadata.json");
		AudioMetaData metaData = ObjectJSONMapper.mapJSON(new String(fh.readBytes(), StandardCharsets.UTF_8), AudioMetaData.class);
		voiceMetaDatas.put(categoryPath, metaData);
		return metaData;
	}

	public static void playSfx(String tag) {
		Sound sfx = sfxs.get(tag);
		float volumeRng = 0.8f + (RNG.nextFloat() * 0.4f);
		float pitchRng = 0.8f + (RNG.nextFloat() * 0.4f);
		float panRng = 0.8f + (RNG.nextFloat() * 0.4f);
		if (sfx != null) sfx.play(0.5f * userSfxVolume * volumeRng, 1f * pitchRng, 1f * panRng);
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
		for (Music music : musics.getAllLoadedElements()) music.dispose();
	}

	public static void updateUserMusicVolume(float newValue) {
		userMusicVolume = newValue;
		if (currentMusic != null) currentMusic.setVolume(targetMusicVolume * userMusicVolume);
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

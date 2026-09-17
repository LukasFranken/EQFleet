package de.instinct.eqlibgdxutils.audio;

import java.util.ArrayList;
import java.util.List;
import games.rednblack.miniaudio.MASound;
import games.rednblack.miniaudio.MiniAudio;

/** A reusable bank of independent voices, allowing the same effect to overlap. */
final class MiniAudioClip {
    private final MiniAudio engine;
    private final String path;
    private final List<MASound> voices = new ArrayList<>();

    MiniAudioClip(MiniAudio engine, String path) {
        this.engine = engine;
        this.path = path;
    }

    void play(float volume) { play(volume, 1f, 0f); }

    void play(float volume, float pitch, float pan) {
        MASound sound = null;
        for (int i = 0; i < voices.size(); i++) {
            if (!voices.get(i).isPlaying()) { sound = voices.get(i); break; }
        }
        if (sound == null) {
            sound = engine.createSound(path, (short) (MASound.Flags.MA_SOUND_FLAG_DECODE
                | MASound.Flags.MA_SOUND_FLAG_NO_SPATIALIZATION), null);
            voices.add(sound);
        }
        sound.stop();
        sound.seekTo(0f);
        sound.setVolume(volume);
        sound.setPitch(pitch);
        sound.setPan(Math.max(-1f, Math.min(1f, pan)));
        sound.play();
    }

    void stop() { for (MASound sound : voices) sound.stop(); }
    void dispose() {
        for (MASound sound : voices) sound.dispose();
        voices.clear();
    }
}

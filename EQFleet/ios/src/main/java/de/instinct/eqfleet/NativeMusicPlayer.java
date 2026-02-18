package de.instinct.eqfleet;

import org.robovm.apple.avfoundation.AVAudioPlayer;
import org.robovm.apple.foundation.NSBundle;
import org.robovm.apple.foundation.NSErrorException;
import org.robovm.apple.foundation.NSURL;

public class NativeMusicPlayer {

    private AVAudioPlayer player;

    public void load(String fileName) throws NSErrorException {
        NSURL url = NSBundle.getMainBundle().findResourceURL("audio/music/" + fileName, "wav");
        player = new AVAudioPlayer(url);
        player.prepareToPlay();
    }

    public void play(boolean loop) {
        if (player == null) return;
        player.setNumberOfLoops(loop ? -1 : 0);
        player.play();
    }

    public void stop() {
        if (player != null) player.stop();
    }

    public void setVolume(float volume) {
        if (player != null) player.setVolume(volume);
    }

    public boolean isPlaying() {
        return player != null && player.isPlaying();
    }
}
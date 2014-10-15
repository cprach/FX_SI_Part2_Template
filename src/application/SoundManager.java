/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

package application;

import java.net.URL;
import javafx.scene.media.AudioClip;

/**
 *
 * @author rcolley
 */
public class SoundManager {
    
    private URL shootSoundURL = getClass().getResource("laser.mp3");
    private URL explosionSoundURL = getClass().getResource("explosion.mp3");
    private URL endGameSoundURL = getClass().getResource("endgame.mp3");
    private URL alienProjectileSoundURL = getClass().getResource("alienprojectile.mp3");
    
    private AudioClip shootClip = new AudioClip(shootSoundURL.toString());
    private AudioClip explosionClip = new AudioClip(explosionSoundURL.toString());
    private AudioClip endGameClip = new AudioClip(endGameSoundURL.toString());
    private AudioClip alienProjectileSoundClip = new AudioClip(alienProjectileSoundURL.toString());
    
    enum soundNames {
        shoot,explosion,endGame,alienProjectile;
    }
    public void stopAllSounds() {
        for (soundNames e : soundNames.values()) {
            getSoundName(e).stop();
        }
    }
    public void playSound(soundNames s) {
        getSoundName(s).play();
    }
    public void stopSound(soundNames s) {
        getSoundName(s).stop();
    }
    
    public AudioClip getSoundName(soundNames s) {
        switch (s) {
            case shoot:
                return shootClip;
            case explosion:
                return explosionClip;
            case endGame:
                return endGameClip;
            case alienProjectile:
                return alienProjectileSoundClip;
        }
        return null;
    }
    
    
}

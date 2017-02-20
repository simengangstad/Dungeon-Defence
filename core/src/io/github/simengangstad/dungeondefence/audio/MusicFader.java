package io.github.simengangstad.dungeondefence.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

/**
 * Created by simengangstad on 06/09/16.
 */
public class MusicFader {

    private static boolean fading = false;

    private static float timer = 0.0f;

    public static void fade(Music music1, Music music2, float fadeOutTime, float fadeInTime, float volume) {

        if (!fading && timer == 0.0f) {

            fading = true;
        }

        timer += Gdx.graphics.getDeltaTime();

        if (timer < fadeOutTime) {

            float fadeOutTimer = Math.abs(timer - fadeOutTime);

            music1.setVolume(volume * (fadeOutTimer / fadeOutTime));
        }
        else if (timer > fadeOutTime && timer <= fadeOutTime + fadeInTime) {

            music2.setVolume(volume * ((timer - fadeOutTime)/fadeInTime));
        }
        else {

            fading = false;
        }
    }
}

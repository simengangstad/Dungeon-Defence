package io.github.simengangstad.defendthecaves.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Queue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by simengangstad on 16/09/16.
 *
 * A music player that switches between modes dependent on the user's wish.
 */
public class Jukebox {

    private HashMap<String, ArrayList<Music>> music = new HashMap<>();

    private String currentGroup = null;

    private Music previousSong, currentSong;

    private Queue<Music> queue = new Queue<>();
    private Queue<String> groupQueue = new Queue<>();

    private float timeToNextSong = 0.0f;

    private float fadeTime = 10.0f;

    public void setFadeTime(float fadeTime) {

        this.fadeTime = fadeTime;
    }

    public void addMusicToGroup(String group, Music track) {

        ArrayList<Music> tracks = music.get(group);

        if (tracks == null) {

            tracks = new ArrayList<>();

            music.put(group, tracks);
        }

        tracks.add(track);

        track.setOnCompletionListener(completionListener);
    }

    private Music.OnCompletionListener completionListener = (music) -> {

        if (queue.size == 0) {

            constructShuffleListFromGroup(currentGroup);
        }

        fetchNextSong();
    };

    public void constructShuffleListFromGroup(String group) {

        if (music.get(group) == null) {

            throw new RuntimeException("No tracks from group: " + group);
        }

        currentGroup = group;
        Collections.shuffle(music.get(currentGroup));

        queue.clear();
        music.get(currentGroup).forEach((track) -> queue.addLast(track));
        currentSong = queue.removeFirst();

        groupQueue.clear();
        music.get(currentGroup).forEach((track) -> groupQueue.addLast(currentGroup));
        currentGroup = groupQueue.removeFirst();

        System.out.println("Playing new track from group: " + currentGroup);
    }

    /**
     * States that Jukebox will switch to a song of the given group within the specified time.
     */
    public void requestSongFromGroup(String group) {

        ArrayList tracks = music.get(group);

        if (tracks == null) {

            throw new RuntimeException("No tracks from group: " + group);
        }

        queue.addFirst((Music) tracks.get(MathUtils.random(tracks.size() - 1)));
        groupQueue.addFirst(group);

        timeToNextSong = fadeTime;

        System.out.println("Adding track from " + group + " group to queue.");
    }

    public void stop() {

        if (currentSong == null) {

            throw new RuntimeException("No song set to play! Construct a playlist first!");
        }

        currentSong.stop();
    }

    public void play() {

        if (currentSong == null) {

            throw new RuntimeException("No song set to play! Construct a playlist first!");
        }

        currentSong.play();
    }

    private void fetchNextSong() {

        System.out.println(currentSong.getVolume());

        currentSong.stop();
        currentSong.setVolume(1.0f);
        previousSong = currentSong;
        previousSong.setPosition(0.0f);
        currentSong = queue.removeFirst();
        currentSong.play();
        currentGroup = groupQueue.removeFirst();

        System.out.println("Playing new track from group: " + currentGroup);
    }

    public String getCurrentGroup() {

        return currentGroup;
    }

    public String getNextGroup() {

        return groupQueue.first();
    }

    public void tick() {

        if (currentSong == null) {

            throw new RuntimeException("No song set to play! Construct a playlist first!");
        }

        if (0 < timeToNextSong) {

            timeToNextSong -= Gdx.graphics.getDeltaTime();

            float volume = timeToNextSong / fadeTime;

            if (volume < 0.0f) {

                volume = 0.0f;
            }

            currentSong.setVolume(volume);
        }
        else if (timeToNextSong < 0) {

            timeToNextSong = 0.0f;

            fetchNextSong();
        }
    }

    public void dispose() {

        music.forEach((group, list) -> list.forEach(Music::dispose));
    }
}
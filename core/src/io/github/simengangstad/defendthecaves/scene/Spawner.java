package io.github.simengangstad.defendthecaves.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;

/**
 * @author simengangstad
 * @since 12/12/15
 */
public class Spawner<T> {

    private final T[] items;

    public Spawner(T[] items) {

        this.items = items;
    }

    /**
     * Spawns a given amount of items with a set interval between the spawns in milliseconds.
     *
     * @param amount The amount of items.
     * @param intervalMills The interval between the spawns in milliseconds.
     * @param callback The callback function which get called as new item is spawned.
     */
    public void spawn(int amount, int intervalMills, SpawnCallback callback) {

        new Thread(() -> {

            for (int i = 0; i < amount; i++) {

                if (0 < i) {

                    try {

                        Thread.sleep(intervalMills);
                    }
                    catch (InterruptedException interruptedException) {

                        System.err.println(interruptedException.getStackTrace());
                    }
                }

                Gdx.app.postRunnable(() -> callback.respond(items[MathUtils.random(items.length - 1)]));
            }
        }).start();
    }

    /**
     * @author simengangstad
     * @since 12/12/15
     */
    public interface SpawnCallback<T> {

        void respond(T t);
    }
}

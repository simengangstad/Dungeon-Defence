package io.github.simengangstad.defendthecaves.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.scene.entities.Player;
import io.github.simengangstad.defendthecaves.scene.gui.ProgressBar;

/**
 * @author simengangstad
 * @since 12/12/15
 */
public class Barrier {

    public final Vector2 position;

    /**
     * The time it takes one enemy to demolish a barrier. This gets reduced up by the amounts of enemies of course.
     * time = secondsToDemolishBarrier / enemies
     */
    public final float TimeToDemolishBarrier = 9.0f;

    private float state = TimeToDemolishBarrier;

    public ProgressBar progressBar = new ProgressBar((int) TimeToDemolishBarrier);

    public float lastState = TimeToDemolishBarrier;

    private Player player;
    private Map map;

    public Barrier(Vector2 position, Map map, Player player) {

        this.position = position;
        this.player = player;
        this.map = map;

        progressBar.setStyle(new ProgressBar.ProgressBarStyle(Game.UISkin.getDrawable("speech-bubble"), Game.UISkin.getDrawable("progress2")));
        progressBar.setVisible(false);
    }

    public boolean isBlocked() {

        return state == TimeToDemolishBarrier;
    }

    public float getState() {

        return state;
    }

    public void setState (float state) {

        if (state < 0) {

            state = 0;
        }

        if (TimeToDemolishBarrier < state) {

            state = TimeToDemolishBarrier;
        }

        progressBar.value = state;
        this.state = state;
    }

    public void updateState(float delta) {

        setState(state + delta);
    }

    public void tick() {

        if (progressBar.isVisible()) {

            Vector3 vector = Game.vector3Pool.obtain();

            vector.set(position.x * Map.TileSizeInPixelsInWorldSpace, position.y * Map.TileSizeInPixelsInWorldSpace, 0.0f);

            player.camera.project(vector, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            // vertical
            if (map.isSolid((int) position.x - 1, (int) position.y) && map.isSolid((int) position.x + 1, (int) position.y)) {

                vector.x += Map.TileSizeInPixelsInWorldSpace / 2.0f - progressBar.getWidth() / 2.0f;

                // Up
                if (map.isSolid((int) position.x, (int) position.y - 1)) {

                    vector.y += Map.TileSizeInPixelsInWorldSpace / 2.0f - progressBar.getHeight() / 2.0f;
                }
                // Down
                else {

                    vector.y += Map.TileSizeInPixelsInWorldSpace / 2.0f - progressBar.getHeight() / 2.0f;
                }
            }
            // horizontal
            else {

                vector.y += Map.TileSizeInPixelsInWorldSpace / 2.0f - progressBar.getHeight() / 2.0f;

                // Right
                if (map.isSolid((int) position.x - 1, (int) position.y)) {

                    vector.x += Map.TileSizeInPixelsInWorldSpace / 2.0f - progressBar.getWidth() / 2.0f;
                }
                // Left
                else {

                    vector.x += Map.TileSizeInPixelsInWorldSpace / 2.0f - progressBar.getWidth() / 2.0f;
                }
            }

            progressBar.setPosition(vector.x, vector.y);

            Game.vector3Pool.free(vector);
        }
    }
}

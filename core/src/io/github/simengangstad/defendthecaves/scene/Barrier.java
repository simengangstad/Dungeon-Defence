package io.github.simengangstad.defendthecaves.scene;

import com.badlogic.gdx.math.Vector2;

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
    public final float TimeToDemolishBarrier = 10;

    private float state = TimeToDemolishBarrier;

    public Barrier(Vector2 position) {

        this.position = position;
    }


    public float getState() {

        return state;
    }

    public void updateState(float delta) {

        state += delta;

        if (state < 0) {

            state = 0;
        }

        if (TimeToDemolishBarrier < state) {

            state = TimeToDemolishBarrier;
        }
    }
}

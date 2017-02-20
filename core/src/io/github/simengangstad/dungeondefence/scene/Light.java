package io.github.simengangstad.dungeondefence.scene;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * @author simengangstad
 * @since 25/02/16
 */
public class Light {


    public final Vector2 position;

    public final Vector3 colour;

    public final float range;

    public boolean flicker = true;

    public boolean enabled = true;

    public Light(Vector2 position, Vector3 colour, float range) {

        this.position = position;
        this.colour = colour;
        this.range = range;
    }
}

package io.github.simengangstad.defendthecaves.scene;

import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.defendthecaves.components.Drawable;
import io.github.simengangstad.defendthecaves.components.GameObject;

/**
 * @author simengangstad
 * @since 28/11/15
 */
public abstract class Entity extends GameObject implements Drawable {

    // Todo: expand

    public Entity(Vector2 position, Vector2 size) {

        super(position, size);
    }
}

package io.github.simengangstad.defendthecaves.components;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import io.github.simengangstad.defendthecaves.scene.Scene;

/**
 * @author simengangstad
 * @since 15/11/15
 */
public abstract class GameObject implements Disposable {

    /**
     * The position (centre) and size (diameter on both axes).
     */
    private final Vector2 position, size;

    public Scene host;

    /**
     * Initializes the game object.
     */
    public GameObject(Vector2 position, Vector2 size) {

        this.position = position;
        this.size = size;
    }

    /**
     * @return The position of the game object.
     */
    public Vector2 getPosition() {

        return position;
    }

    /**
     * @return The size of the game object.
     */
    public Vector2 getSize() {

        return size;
    }

    /**
     * Updates the state of the game object.
     */
    public abstract void tick();
}

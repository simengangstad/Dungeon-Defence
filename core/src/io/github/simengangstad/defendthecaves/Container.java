package io.github.simengangstad.defendthecaves;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Disposable;
import io.github.simengangstad.defendthecaves.components.Drawable;
import io.github.simengangstad.defendthecaves.components.GameObject;

import java.util.ArrayList;

/**
 * @author simengangstad
 * @since 15/11/15
 */
public class Container implements Disposable {

    /**
     * The batch responsable for drawing.
     */
    protected SpriteBatch batch = new SpriteBatch();

    /**
     * The game objects in the container.
     */
    protected ArrayList<GameObject> gameObjects = new ArrayList<>();

    /**
     * Adds a game object to the container.
     */
    public void addGameObject(GameObject gameObject) {

        gameObjects.add(gameObject);

        gameObject.host = this;
    }

    public void setProjectionMatrix(Matrix4 projectionMatrix) {

        batch.setProjectionMatrix(projectionMatrix);
    }

    public void tick() {

        batch.begin();

        gameObjects.forEach(gameObject -> {

            gameObject.tick();

            if (gameObject instanceof Drawable) {

                ((Drawable) gameObject).draw(batch, gameObject.getPosition(), gameObject.getSize());
            }

        });

        batch.end();
    }

    @Override
    public void dispose() {

        batch.dispose();

        gameObjects.forEach((gameObject -> gameObject.dispose()));
    }
}

package io.github.simengangstad.defendthecaves;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Disposable;
import io.github.simengangstad.defendthecaves.components.Drawable;
import io.github.simengangstad.defendthecaves.components.GameObject;
import io.github.simengangstad.defendthecaves.scene.Scene;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author simengangstad
 * @since 15/11/15
 */
public class Container implements Disposable, InputProcessor {

    /**
     * The batch responsable for drawing.
     */
    protected SpriteBatch batch = new SpriteBatch();

    /**
     * The game objects in the container.
     */
    protected List<GameObject> gameObjects = new ArrayList<>();

    /**
     * Game objects requested to be added to the main array. This is to prevent modifying the array list whilst
     * in a tick.
     */
    protected List<GameObject> buffer = new ArrayList<>(), removeBuffer = new ArrayList<>();

    /**
     * Adds a game object to the container.
     */
    public void addGameObject(GameObject gameObject) {

        buffer.add(gameObject);

        gameObject.host = this;
    }

    public void removeGameObject(GameObject gameObject) {

        removeBuffer.add(gameObject);
    }

    public void setProjectionMatrix(Matrix4 projectionMatrix) {

        batch.setProjectionMatrix(projectionMatrix);
    }

    public Matrix4 getProjectionMatrix() {

        return batch.getProjectionMatrix();
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    public void tick() {

        if (!buffer.isEmpty()) {

            gameObjects.addAll(buffer);

            buffer.clear();
        }

        if (!removeBuffer.isEmpty()) {

            gameObjects.removeAll(removeBuffer);

            removeBuffer.clear();
        }

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

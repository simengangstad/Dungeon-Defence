package io.github.simengangstad.dungeondefence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import io.github.simengangstad.dungeondefence.scene.Map;
import io.github.simengangstad.dungeondefence.scene.gui.Pointer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author simengangstad
 * @since 15/11/15
 */
public class Container implements Disposable, InputProcessor {

    /**
     * The batch responsable for drawing.
     */
    public SpriteBatch batch = new SpriteBatch();

    /**
     * The input multiplexer dealing with the various inputs and delegating them.
     */
    protected InputMultiplexer inputMultiplexer = new InputMultiplexer();

    /**
     * Stage responsable for UI layout.
     */
    public Stage stage = new Stage();

    /**
     * The game objects in the container.
     */
    protected ArrayList<GameObject> gameObjects = new ArrayList<GameObject>();

    /**
     * Game objects requested to be added to the main array. This is to prevent modifying the array list whilst
     * in a tick.
     */
    protected ArrayList<GameObject> buffer = new ArrayList<GameObject>(), removeBuffer = new ArrayList<GameObject>();

    /**
     * The custom mouse cursor.
     */
    protected Pointer pointer = new Pointer();

    /**
     * Drawn behind every game object and actor.
     */
    protected Texture background = null;

    /**
     * Initializes the container.
     */
    public Container() {

        Gdx.input.setInputProcessor(inputMultiplexer);

        addInputProcessor(this);
        addInputProcessor(stage);

        pointer.size.set(Map.TileSizeInPixelsInWorldSpace / 2.0f, Map.TileSizeInPixelsInWorldSpace / 2.0f);
    }

    public void addInputProcessor(InputProcessor inputProcessor) {

        inputMultiplexer.addProcessor(inputProcessor);
    }

    /**
     * Adds a game object to the container.
     */
    public void addGameObject(GameObject gameObject) {

        buffer.add(gameObject);

        gameObject.host = this;
        gameObject.create();
    }

    public void removeGameObject(GameObject gameObject) {

        removeBuffer.add(gameObject);
    }

    public List<GameObject> getGameObjects() {

        return gameObjects;
    }

    public void setProjectionMatrix(Matrix4 projectionMatrix) {

        batch.setProjectionMatrix(projectionMatrix);
    }

    public Matrix4 getProjectionMatrix() {

        return batch.getProjectionMatrix();
    }

    public void resize(int width, int height) {

        stage.getViewport().update(width, height, true);
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

    /**
     * Adds and removes the gameobjects in the buffers.
     */
    public void clearBuffers() {

        if (!buffer.isEmpty()) {

            gameObjects.addAll(buffer);

            buffer.clear();
        }

        if (!removeBuffer.isEmpty()) {

            gameObjects.removeAll(removeBuffer);

            removeBuffer.clear();
        }
    }

    public void tick() {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        clearBuffers();

        batch.begin();

        if (background != null) batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        for (GameObject gameObject : gameObjects) {

            gameObject.tick();
            gameObject.draw(batch);
        }

        batch.end();

        stage.draw();
    }

    @Override
    public void dispose() {

        batch.dispose();
        stage.dispose();

        for (GameObject gameObject : gameObjects) {

            gameObject.dispose();
        }
    }
}

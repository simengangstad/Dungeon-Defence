package io.github.simengangstad.defendthecaves;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

/**
 * @author bruker
 * @since 28/03/2016
 */
public class Test extends ApplicationAdapter {

    @Override
    public void create() {

        super.create();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
    public void render() {

        super.render();

        // TODO: Bug on Windows 10... Mouse is locked while polling.
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {


        }
    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}

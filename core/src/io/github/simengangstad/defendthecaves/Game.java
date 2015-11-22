package io.github.simengangstad.defendthecaves;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import io.github.simengangstad.defendthecaves.components.Scene;

public class Game extends ApplicationAdapter {

    public static Texture spriteSheet;

    private Scene scene;

    @Override
	public void create () {

        //MathUtils.random.setSeed(123);

        spriteSheet = new Texture("assets/spritesheet.png");

        Player player = new Player(new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        scene = new Scene(new Map(player.getSize()), player);
    }

    @Override
    public void resize(int width, int height) {

        scene.updateMatrices();
    }

    @Override
	public void render () {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Gdx.graphics.setTitle("FPS: " + Gdx.graphics.getFramesPerSecond());

        scene.tick();
    }

    @Override
    public void dispose() {

        scene.dispose();
    }
}

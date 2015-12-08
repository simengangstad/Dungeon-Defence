package io.github.simengangstad.defendthecaves;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.simengangstad.defendthecaves.scene.Scene;
import io.github.simengangstad.defendthecaves.scene.entities.Player;

public class Game extends ApplicationAdapter {

    public static Texture spriteSheet;

    private Scene scene;

    public static boolean DebubDraw = false;

    public static TextureRegion debugDrawTexture;

    @Override
	public void create () {

        spriteSheet = new Texture("assets/spritesheet.png");

        spriteSheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        debugDrawTexture = new TextureRegion(spriteSheet, 48, 0, 16, 16);

        Player player = new Player(new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        scene = new Scene(new Map(player.getSize()), player);

        player.host = scene;
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

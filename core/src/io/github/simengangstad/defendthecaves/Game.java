package io.github.simengangstad.defendthecaves;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.simengangstad.defendthecaves.scene.Map;
import io.github.simengangstad.defendthecaves.scene.Scene;
import io.github.simengangstad.defendthecaves.scene.entities.Player;

public class Game extends ApplicationAdapter {

    public static Texture spriteSheet;
    public static Texture guiSheet;

    /**
     * The tile size of the movable entity sprites in the sprite sheet.
     */
    public static final int SizeOfTileInPixelsInSpritesheet = 16;

    private Scene scene;

    public static boolean DebubDraw = false;

    public static TextureRegion debugDrawTexture;

    @Override
	public void create () {

        spriteSheet = new Texture("assets/spritesheet.png");
        guiSheet = new Texture("assets/gui_sheet.png");

        debugDrawTexture = new TextureRegion(spriteSheet, 48, 80, 16, 16);

        Player player = new Player(new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        scene = new Scene(player);

        Gdx.input.setCursorCatched(true);
        Gdx.input.setCursorPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
    }

    @Override
    public void resize(int width, int height) {

        scene.updateMatrices();
    }

    @Override
	public void render () {

        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Gdx.graphics.setTitle("FPS: " + Gdx.graphics.getFramesPerSecond());

        scene.tick();
    }

    @Override
    public void dispose() {

        scene.dispose();
    }
}

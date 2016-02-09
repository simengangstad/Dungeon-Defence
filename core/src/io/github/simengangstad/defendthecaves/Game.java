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

    public static Texture SpriteSheet;
    public static Texture GUISheet;

    public static Texture PlayerStationary;
    public static Texture PlayerMoving;

    public static Texture SnakeStationary;
    public static Texture SnakeMoving;
    public static Texture SnakeBiting;

    public static Texture OrcStationary;
    public static Texture OrcMoving;

    public static Texture CaterpillarStationary;
    public static Texture CaterpillarMoving;
    public static Texture CaterpillarAttacking;

    public static final int PotionSize = 60;
    public static final int EntitySize = 80;


    /**
     * The tile size of the movable entity sprites in the sprite sheet.
     */
    public static final int SizeOfTileInPixelsInSpritesheet = 16;

    private Scene scene;

    public static boolean DebubDraw = false;

    public static TextureRegion debugDrawTexture;

    @Override
	public void create () {

        SpriteSheet = new Texture("assets/spritesheet.png");
        GUISheet = new Texture("assets/gui_sheet.png");

        PlayerStationary = new Texture("assets/animations/PlayerStationary.png");
        PlayerMoving = new Texture("assets/animations/PlayerWalking.png");

        SnakeStationary = new Texture("assets/animations/SnakeStationary.png");
        SnakeMoving = new Texture("assets/animations/SnakeMoving.png");
        SnakeBiting = new Texture("assets/animations/SnakeAttacking.png");

        OrcStationary = new Texture("assets/animations/OrcStationary.png");
        OrcMoving = new Texture("assets/animations/OrcMoving.png");

        CaterpillarStationary = new Texture("assets/animations/CaterpillarStationary.png");
        CaterpillarMoving = new Texture("assets/animations/CaterpillarMoving.png");
        CaterpillarAttacking = new Texture("assets/animations/CaterpillarAttacking.png");


        debugDrawTexture = new TextureRegion(SpriteSheet, 48, 80, 16, 16);

        Player player = new Player(new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        scene = new Scene(player);

        Gdx.input.setInputProcessor(scene);
        //Gdx.input.setCursorCatched(true);
        //Gdx.input.setCursorPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
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

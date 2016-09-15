package io.github.simengangstad.defendthecaves;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Pool;
import io.github.simengangstad.defendthecaves.scene.Item;
import io.github.simengangstad.defendthecaves.scene.Scene;
import io.github.simengangstad.defendthecaves.scene.crafting.CraftableItemsView;
import io.github.simengangstad.defendthecaves.scene.crafting.Inventory;
import io.github.simengangstad.defendthecaves.scene.entities.Caterpillar;
import io.github.simengangstad.defendthecaves.scene.entities.Orc;
import io.github.simengangstad.defendthecaves.scene.entities.Player;
import io.github.simengangstad.defendthecaves.scene.entities.Snake;
import io.github.simengangstad.defendthecaves.scene.items.Axe;
import io.github.simengangstad.defendthecaves.scene.items.Crossbow;
import io.github.simengangstad.defendthecaves.scene.items.Potion;
import io.github.simengangstad.defendthecaves.scene.items.Rock;
import io.github.simengangstad.defendthecaves.startscreen.StartScreen;

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

    public static Texture Torch;

    public static final int EntitySize = 80;
    public static final int ItemSize = (EntitySize / 2);

    public static Skin UISkin;

    public static Pool<Vector2> vector2Pool = new Pool<Vector2>() {

        @Override
        protected Vector2 newObject() {

            return new Vector2();
        }
    };

    public static Pool<Vector3> vector3Pool = new Pool<Vector3>() {

        @Override
        protected Vector3 newObject() {

            return new Vector3();
        }
    };

    /**
     * The tile size of the movable entity sprites in the sprite sheet.
     */
    public static final int SizeOfTileInPixelsInSpritesheet = 16;

    public static Container container;

    public static boolean Debug = false;

    public static TextureRegion debugDrawTexture;

    @Override
	public void create () {

        init();

        container = new StartScreen();

        Gdx.input.setCursorCatched(false);
        Gdx.input.setCursorPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
    }

    private void init() {

        System.out.println("OpenGL context: " + Gdx.gl.glGetString(Gdx.gl.GL_VERSION));

        SpriteSheet = new Texture("assets/spritesheet.png");
        GUISheet = new Texture("assets/gui/uiskin.png");

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

        Torch = new Texture("assets/animations/Torch.png");

        debugDrawTexture = new TextureRegion(SpriteSheet, 48, 80, 16, 16);

        UISkin = new Skin(Gdx.files.internal("assets/gui/uiskin.json"));

    }

    @Override
    public void resize(int width, int height) {

        container.resize(width, height);
    }

    @Override
	public void render () {

        Gdx.graphics.setTitle("FPS: " + Gdx.graphics.getFramesPerSecond());

        container.tick();
    }

    @Override
    public void dispose() {

        SpriteSheet.dispose();
        GUISheet.dispose();
        PlayerStationary.dispose();
        PlayerMoving.dispose();

        SnakeStationary.dispose();
        SnakeMoving.dispose();
        SnakeBiting.dispose();

        OrcStationary.dispose();
        OrcMoving.dispose();

        CaterpillarStationary.dispose();
        CaterpillarMoving.dispose();
        CaterpillarAttacking.dispose();

        container.dispose();

        CraftableItemsView.CraftSound.dispose();
        CraftableItemsView.ErrorSound.dispose();
        Inventory.Trashing.dispose();

        for (Sound sound : Orc.roar) {

            sound.dispose();
        }

        Caterpillar.Spitting.dispose();
        Caterpillar.Hiss.dispose();

        Snake.bite.dispose();
        Snake.hiss.dispose();

        Axe.Hit.dispose();
        Axe.Swing.dispose();

        Item.throwSound.dispose();

        for (Sound sound : Player.Walking) {

            sound.dispose();
        }

        Player.Rocks.dispose();

        Potion.Breaking.dispose();
        Potion.Drinking.dispose();
        Potion.Burp.dispose();

        Crossbow.Fire.dispose();
    }
}

package io.github.simengangstad.dungeondefence;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Pool;
import io.github.simengangstad.dungeondefence.scene.Explosion;
import io.github.simengangstad.dungeondefence.scene.Item;
import io.github.simengangstad.dungeondefence.scene.crafting.CraftableItemsView;
import io.github.simengangstad.dungeondefence.scene.crafting.Inventory;
import io.github.simengangstad.dungeondefence.scene.entities.Caterpillar;
import io.github.simengangstad.dungeondefence.scene.entities.Orc;
import io.github.simengangstad.dungeondefence.scene.entities.Player;
import io.github.simengangstad.dungeondefence.scene.entities.Snake;
import io.github.simengangstad.dungeondefence.scene.items.Axe;
import io.github.simengangstad.dungeondefence.scene.items.Crossbow;
import io.github.simengangstad.dungeondefence.scene.items.Potion;

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

    public static Texture Background;

    public static final int EntitySize = 80;
    public static final int ItemSize = (EntitySize / 2);

    public static Skin UISkin;
    public static Label.LabelStyle LabelStyle8;
    public static Label.LabelStyle LabelStyle12;
    public static Label.LabelStyle LabelStyle16;
    public static Label.LabelStyle LabelStyle32;


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

    public static Container tmpContainer;

    public static Container container;

    public static boolean Debug = false;

    public static TextureRegion debugDrawTexture;

    public static boolean PlaySound = false;

    @Override
	public void create () {

        Gdx.app.setLogLevel(Application.LOG_INFO);

        init();

        Preferences prefs = Gdx.app.getPreferences("prefs");

        if (!prefs.contains("music")) {

            prefs.putBoolean("music", true);
        }

        if (!prefs.contains("sfx")) {

            prefs.putBoolean("sfx", true);
        }

        prefs.flush();

        PlaySound = prefs.getBoolean("sfx");

        Gdx.input.setCursorCatched(true);
        Gdx.input.setCursorPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);

        container = new StartScreen();
    }

    public static void swapContainer(Container current, Container next) {

        next.inputMultiplexer.clear();

        Gdx.input.setInputProcessor(next.inputMultiplexer);

        next.addInputProcessor(next);
        next.addInputProcessor(next.stage);

        Game.container = next;
        Game.tmpContainer = current;
    }

    private void init() {

        Gdx.app.log("OpenGL", "OpenGL context: " + Gdx.gl.glGetString(GL20.GL_VERSION));

        SpriteSheet = new Texture(Gdx.files.internal("images/spritesheet.png"));
        GUISheet = new Texture(Gdx.files.internal("gui/uiskin.png"));

        PlayerStationary = new Texture(Gdx.files.internal("animations/PlayerStationary.png"));
        PlayerMoving = new Texture(Gdx.files.internal("animations/PlayerWalking.png"));

        SnakeStationary = new Texture(Gdx.files.internal("animations/SnakeStationary.png"));
        SnakeMoving = new Texture(Gdx.files.internal("animations/SnakeMoving.png"));
        SnakeBiting = new Texture(Gdx.files.internal("animations/SnakeAttacking.png"));

        OrcStationary = new Texture(Gdx.files.internal("animations/OrcStationary.png"));
        OrcMoving = new Texture(Gdx.files.internal("animations/OrcMoving.png"));

        CaterpillarStationary = new Texture(Gdx.files.internal("animations/CaterpillarStationary.png"));
        CaterpillarMoving = new Texture(Gdx.files.internal("animations/CaterpillarMoving.png"));
        CaterpillarAttacking = new Texture(Gdx.files.internal("animations/CaterpillarAttacking.png"));

        Background = new Texture(Gdx.files.internal("images/background.png"));

        Torch = new Texture(Gdx.files.internal("animations/Torch.png"));

        debugDrawTexture = new TextureRegion(SpriteSheet, 48, 80, 16, 16);

        UISkin = new Skin(Gdx.files.internal("gui/uiskin.json"));

        LabelStyle8 = new Label.LabelStyle(new BitmapFont(Gdx.files.internal("gui/gothic_8.fnt")), Color.WHITE);
        LabelStyle12 = new Label.LabelStyle(new BitmapFont(Gdx.files.internal("gui/gothic_12.fnt")), Color.WHITE);
        LabelStyle16 = new Label.LabelStyle(new BitmapFont(Gdx.files.internal("gui/gothic_16.fnt")), Color.WHITE);
        LabelStyle32 = new Label.LabelStyle(new BitmapFont(Gdx.files.internal("gui/gothic_32.fnt")), Color.WHITE);

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

        Explosion.ExplosionSound.dispose();
    }
}

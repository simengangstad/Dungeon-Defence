package io.github.simengangstad.defendthecaves;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.simengangstad.defendthecaves.components.Drawable;
import io.github.simengangstad.defendthecaves.components.GameObject;

/**
 * @author simengangstad
 * @since 10/11/15
 */
public class Player extends GameObject implements Drawable {

    /**
     * The camera of the player.
     */
    public final Camera camera;

    /**
     * The speed of the player in pixels per second.
     */
    public int speed = 300;

    /**
     * Reference to the drawable texture region of the player.
     */
    private final TextureRegion textureRegion;

    /**
     * The map the player is located in.
     */
    private Map map;

    /**
     * Vector used for calculating direction.
     */
    private Vector2 tmpDirection = new Vector2();

    /**
     * The texture for the stationary animation.
     */
    private Texture stationaryTexture;

    /**
     * The animation when the player is standing still.
     */
    private Animation stationaryAnimation;

    /**
     * The texture for the walking animations.
     */
    private Texture walkingTexture;

    /**
     * The animation when the player is moving.
     */
    private Animation walkingAnimation;

    /**
     * If the player is facing right.
     */
    private boolean facingRight = true;

    /**
     * If the player is going backwards. Used for playing the walking loop backwards.
     */
    private boolean goingBackwards = false;

    /**
     * The current animation playing.
     */
    private Animation currentAnimation;

    /**
     * The current texture region.
     */
    private TextureRegion currentTextureRegion;

    /**
     * The state time.
     */
    private float stateTime = 0.0f;

    /**
     * If the player is attacking or not.
     */
    private boolean attacking = false;

    /**
     * The tile size of the player sprites in the sprite sheet.
     */
    private final int TileSize = 16;

    /**
     * Initializes the player with a camera.
     */
    public Player(Camera camera) {

        super(new Vector2(), new Vector2(80.0f, 80.0f));

        this.camera = camera;

        textureRegion = new TextureRegion(Game.spriteSheet, 8, TileSize, 2, 2);

        walkingTexture = new Texture("assets/animations/PlayerWalking.png");

        Array<TextureRegion> walkingTextureRegions = new Array<>();

        for (int i = 0; i < walkingTexture.getWidth() / TileSize; i++) {

            walkingTextureRegions.add(new TextureRegion(walkingTexture, i * TileSize, 0, TileSize, TileSize));
        }

        walkingAnimation = new Animation(0.075f, walkingTextureRegions);


        stationaryTexture = new Texture("assets/animations/PlayerStationary.png");

        Array<TextureRegion> stationaryTextureRegions = new Array<>();

        for (int i = 0; i < stationaryTexture.getWidth() / TileSize; i++) {

            stationaryTextureRegions.add(new TextureRegion(stationaryTexture, i * TileSize, 0, TileSize, TileSize));
        }

        stationaryAnimation = new Animation(0.2f, stationaryTextureRegions);

        currentTextureRegion = stationaryAnimation.getKeyFrame(stateTime, true);
    }

    /**
     * Sets the map the player resolves its collision against.
     */
    public void setMap(Map map) {

        this.map = map;
    }

    @Override
    public void tick() {

        if (map == null) {

            throw new RuntimeException("Need an assigned map in order to resolve collisions.");
        }

        tmpDirection.set(0.0f, 0.0f);

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {

            tmpDirection.add(0.0f, 1.0f);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S)) {

            tmpDirection.add(0.0f, -1.0f);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {

            tmpDirection.add(-1.0f, 0.0f);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {

            tmpDirection.add(1.0f, 0.0f);
        }

        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && !attacking) {

            //attacking = true;

            //stateTime = 0.0f;
        }

        // Left
        if (Gdx.input.getX() < Gdx.graphics.getWidth() / 2.0f && facingRight) {

            facingRight = false;
        }
        // Right
        else if (Gdx.graphics.getWidth() / 2.0f < Gdx.input.getX() && !facingRight) {

            facingRight = true;
        }

        if (tmpDirection.x != 0.0f || tmpDirection.y != 0.0f) {

            if (currentAnimation != walkingAnimation && !attacking) {

                currentAnimation = walkingAnimation;

                stateTime = 0.0f;
            }

            // If facing right and going backwards
            if (facingRight && tmpDirection.x < 0.0f) {

                goingBackwards = true;
            }
            // if facing left and going backwards
            else if (!facingRight && 0.0f < tmpDirection.x) {

                goingBackwards = true;
            }
            else {

                goingBackwards = false;
            }

            tmpDirection.nor();

            map.resolveCollision(getPosition(), tmpDirection, speed);

            updateCameraPosition();
        }
        else {

            if (currentAnimation != stationaryAnimation && !attacking) {

                currentAnimation = stationaryAnimation;

                goingBackwards = false;

                stateTime = 0.0f;
            }
        }


        if (!goingBackwards) {

            stateTime += Gdx.graphics.getDeltaTime();
        }
        else {

            // Increment the state time by the whole animation if less than zero and decrement by
            // delta time to run the animation backwards.
            if (stateTime - Gdx.graphics.getDeltaTime() < 0.0f) {

                stateTime += walkingAnimation.getAnimationDuration();
            }

            stateTime -= Gdx.graphics.getDeltaTime();
        }

        /*
        if (currentAnimation == attackingAnimation) {

            currentTextureRegion = currentAnimation.getKeyFrame(stateTime, false);

            if (currentAnimation.isAnimationFinished(stateTime)) {

                attacking = false;
            }
        }
        else {

            currentTextureRegion = currentAnimation.getKeyFrame(stateTime, true);
        }*/

        currentTextureRegion = currentAnimation.getKeyFrame(stateTime, true);
    }

    @Override
    public void draw(SpriteBatch batch, Vector2 position, Vector2 size) {

        if (!facingRight) {

            currentTextureRegion.flip(true, false);
        }

        batch.draw(currentTextureRegion, camera.position.x, camera.position.y, size.x, size.y);

        if (!facingRight) {

            currentTextureRegion.flip(true, false);
        }
    }

    public void updateCameraPosition() {

        camera.position.set(Math.round(getPosition().x), Math.round(getPosition().y), 0.0f);
        camera.update();
    }

    @Override
    public TextureRegion getTextureRegion() {

        return textureRegion;
    }

    @Override
    public void dispose() {

        stationaryTexture.dispose();
        walkingTexture.dispose();
    }
}

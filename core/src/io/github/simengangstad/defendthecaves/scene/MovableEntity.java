package io.github.simengangstad.defendthecaves.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.simengangstad.defendthecaves.Game;

/**
 * @author simengangstad
 * @since 08/12/15
 */
public abstract class MovableEntity extends Entity {

    /**
     * The map the movable entity is located in.
     */
    protected Map map;

    /**
     * The movement of the moveable entity.
     */
    public Vector2 delta = new Vector2();


    /**
     * The texture for the stationary animation.
     */
    private Texture stationaryTexture;

    /**
     * The animation when the movable entity is standing still.
     */
    protected Animation stationaryAnimation;

    /**
     * The texture for the walking animations.
     */
    private Texture walkingTexture;

    /**
     * The animation when the movable entity is moving.
     */
    protected Animation walkingAnimation;



    /**
     * If the player is facing right.
     */
    protected boolean facingRight = true;

    /**
     * If the player is going backwards. Used for playing the walking loop backwards.
     */
    protected boolean goingBackwards = false;



    /**
     * The current animation playing.
     */
    protected Animation currentAnimation;

    /**
     * The current texture region.
     */
    protected TextureRegion currentTextureRegion;

    /**
     * The state time.
     */
    protected float stateTime = 0.0f;

    /**
     * The shadow under the movable entity.
     */
    private TextureRegion shadow = new TextureRegion(Game.spriteSheet, 0, 80, Game.SizeOfTileInPixelsInSpritesheet, Game.SizeOfTileInPixelsInSpritesheet);


    // Force

    protected final Vector2 forceApplied = new Vector2();

    private boolean forcePositiveX = false, forcePositiveY = false;


    // Leap

    private float LeapDuation = 0.3f, timeLeftOfLeap = 0.0f;

    protected TextureRegion leapTextureRegion;

    /**
     * Initializes the movable entity with a position, size and the locaitons for the animations.
     */
    public MovableEntity(Vector2 position, Vector2 size, String stationaryAnimationTextureLocation, float stationaryAnimationDuration, String walkingAnimationTextureLocation, float walkingAnimationDuration) {

        super(position, size);

        stationaryTexture = new Texture(stationaryAnimationTextureLocation);

        Array<TextureRegion> stationaryTextureRegions = new Array<>();

        for (int i = 0; i < stationaryTexture.getWidth() / Game.SizeOfTileInPixelsInSpritesheet; i++) {

            stationaryTextureRegions.add(new TextureRegion(stationaryTexture, i * Game.SizeOfTileInPixelsInSpritesheet, 0, Game.SizeOfTileInPixelsInSpritesheet, Game.SizeOfTileInPixelsInSpritesheet));
        }

        stationaryAnimation = new Animation(stationaryAnimationDuration, stationaryTextureRegions);


        walkingTexture = new Texture(walkingAnimationTextureLocation);

        Array<TextureRegion> walkingTextureRegions = new Array<>();

        for (int i = 0; i < walkingTexture.getWidth() / Game.SizeOfTileInPixelsInSpritesheet; i++) {

            walkingTextureRegions.add(new TextureRegion(walkingTexture, i * Game.SizeOfTileInPixelsInSpritesheet, 0, Game.SizeOfTileInPixelsInSpritesheet, Game.SizeOfTileInPixelsInSpritesheet));
        }

        walkingAnimation = new Animation(walkingAnimationDuration, walkingTextureRegions);

        currentTextureRegion = stationaryAnimation.getKeyFrame(stateTime, true);
        currentAnimation = stationaryAnimation;
    }

    public void applyForce(Vector2 force) {

        forceApplied.set(force);

        forcePositiveX = 0 < forceApplied.x;
        forcePositiveY = 0 < forceApplied.y;
    }

    /**
     * Sets the map the movable entity resolves its collision against.
     */
    public void setMap(Map map) {

        this.map = map;
    }

    public void leap() {

        if (timeLeftOfLeap == 0.0f) {

            timeLeftOfLeap = LeapDuation;
        }
    }

    protected abstract void collides();


    @Override
    public void tick() {

        if (map == null) {

            throw new RuntimeException("Need an assigned map in order to resolve collisions.");
        }

        if (forceApplied.x != 0.0f || forceApplied.y != 0.0f) {

            float delta = Gdx.graphics.getDeltaTime() * 7.5f;

            this.delta.add(forceApplied.x * delta, forceApplied.y * delta);

            forceApplied.add(delta * (forcePositiveX == false ? 1 : -1), delta * (forcePositiveY == false ? 1 : -1));

            forceApplied.set(forcePositiveX == true ? Math.max(0, forceApplied.x) : Math.min(0, forceApplied.x), forcePositiveY == true ? Math.max(0, forceApplied.y) : Math.min(0, forceApplied.y));
        }

        if (delta.x != 0.0f || delta.y != 0.0f) {

            if (currentAnimation != walkingAnimation) {

                currentAnimation = walkingAnimation;

                stateTime = 0.0f;
            }

            // If facing right and going backwards
            if (facingRight && delta.x < 0.0f) {

                goingBackwards = true;
            }
            // if facing left and going backwards
            else if (!facingRight && 0.0f < delta.x) {

                goingBackwards = true;
            }
            else {

                goingBackwards = false;
            }

            if (map.resolveCollision(getPosition(), delta, speed)) {

                collides();
            }
        }
        else {

            if (currentAnimation != stationaryAnimation) {

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

        if (timeLeftOfLeap == 0.0f) {

            currentTextureRegion = currentAnimation.getKeyFrame(stateTime, true);
        }
        else if (0.0f < timeLeftOfLeap) {

            timeLeftOfLeap -= Gdx.graphics.getDeltaTime();

            currentTextureRegion = leapTextureRegion;
        }
        else {

            timeLeftOfLeap = 0.0f;
        }

        delta.set(0.0f, 0.0f);

        super.tick();
    }

    @Override
    public boolean flip() {

        return !facingRight;
    }

    @Override
    public TextureRegion getTextureRegion() {

        return currentTextureRegion;
    }

    @Override
    protected TextureRegion getShadowTextureRegion() {

        return shadow;
    }

    @Override
    public void dispose() {

        stationaryTexture.dispose();
        walkingTexture.dispose();
    }
}

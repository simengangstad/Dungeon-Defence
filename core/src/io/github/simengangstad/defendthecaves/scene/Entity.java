package io.github.simengangstad.defendthecaves.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.defendthecaves.Callback;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.components.Drawable;
import io.github.simengangstad.defendthecaves.components.GameObject;
import io.github.simengangstad.defendthecaves.scene.item.Potion;
import jdk.nashorn.internal.codegen.CompilerConstants;

import java.util.ArrayList;

/**
 * Represents every livable entity the game.
 *
 * @author simengangstad
 * @since 28/11/15
 */
public abstract class Entity extends GameObject implements Drawable {


    public static final int MaxHealth = 100;

    // Inventory

    public Inventory inventory = null;


    // Weapons

    protected final ArrayList<Tool> tools = new ArrayList<>();

    protected Tool currentTool = null;

    private float IntervalBetweenInteractions = 0.25f;

    private float timeLeftBeforeBeginAbleToInteract = 0.0f;

    protected boolean raiseTool = false;


    // Properties

    public int health = MaxHealth;


    // Taking damage

    private final float DamageTime = 0.4f;

    private float timeLeftOfTakingDamage = 0.0f;


    // Paralysis which happens after the entity has taken damage

    private float TimeParalyzed = 0.5f;

    private float timeLeftOfParalysis = 0;


    // Variables relating the flicker overlay once taken damage.

    private static int uniformLocation = -1;

    private final int AmountOfFlickers = 3;

    private final float FlickersInterval = DamageTime / (AmountOfFlickers * 2);


    /**
     * The map the entity is located in.
     */
    protected Map map;

    /**
     * The movement of the entity during the frames.
     */
    public Vector2 delta = new Vector2();

    /**
     * The animation when the movable entity is standing still.
     */
    protected Animation stationaryAnimation;

    /**
     * The animation when the movable entity is moving.
     */
    protected Animation movingAnimation;

    /**
     * If the entity is facing right.
     */
    protected boolean facingRight = true;

    /**
     * If the entity is going backwards. Used for playing the walking loop backwards.
     */
    protected boolean goingBackwards = false;



    /**
     * The current animation playing.
     */
    protected Animation currentAnimation;

    /**
     * If requesting animation or not.
     */
    private boolean requestingAnimation = false;

    /**
     * The current texture region.
     */
    protected TextureRegion currentTextureRegion;

    /**
     * The state time.
     */
    protected float stateTime = 0.0f;

    /**
     * The shadow under the entity.
     */
    private TextureRegion shadow = new TextureRegion(Game.SpriteSheet, 0, 80, Game.SizeOfTileInPixelsInSpritesheet, Game.SizeOfTileInPixelsInSpritesheet);


    public int speed = 300;

    // Force

    protected final Vector2 forceApplied = new Vector2();

    private boolean forcePositiveX = false, forcePositiveY = false;


    // Leap

    private float LeapDuation = 0.3f, timeLeftOfLeap = 0.0f;

    protected TextureRegion leapTextureRegion;


    private Vector2 tmpVector = new Vector2();



    private Callback requestedAnimationCallback;


    /**
     * Initializes the movable entity with a position, size and the locaitons for the animations.
     */
    public Entity(Vector2 position, Vector2 size, Animation stationaryAnimation, Animation movingAnimation) {

        super(position, size);

        this.stationaryAnimation = stationaryAnimation;
        this.movingAnimation = movingAnimation;

        currentTextureRegion = stationaryAnimation.getKeyFrame(stateTime, true);
        currentAnimation = stationaryAnimation;
    }

    /**
     * Sets the map the movable entity resolves its collision against.
     */
    public void setMap(Map map) {

        this.map = map;
    }

    public void applyForce(Vector2 force) {

        forceApplied.set(force);

        forcePositiveX = 0 < forceApplied.x;
        forcePositiveY = 0 < forceApplied.y;
    }

    public void leap() {

        if (timeLeftOfLeap == 0.0f) {

            timeLeftOfLeap = LeapDuation;
        }
    }

    /**
     * Shuffles the tool so that the next tool in the tool list becomes the current one.
     */
    public void shuffleTool() {

        currentTool = tools.get((tools.indexOf(currentTool) + 1) % tools.size());
    }

    public void attachTool(Tool tool) {

        tools.add(tool);

        tool.parent = this;

        currentTool = tool;
    }

    public void detachTool(Tool tool) {

        tools.remove(tool);

        tool.parent = null;
    }

    public void interact(Vector2 interactionDirection) {

        if (timeLeftBeforeBeginAbleToInteract == 0.0f) {

            timeLeftBeforeBeginAbleToInteract = IntervalBetweenInteractions;

            currentTool.interact(interactionDirection);
        }
    }

    public void react(Potion potion) {

        // The higher the toxicity, the more damage it causes, so we flip the sign
        adjustHealth(-potion.getToxicity());
    }

    public void paralyse() {

        timeLeftOfParalysis = TimeParalyzed;
    }

    public boolean isParalysed() {

        return 0 < timeLeftOfParalysis;
    }

    public void adjustHealth(int value) {

        health = MathUtils.clamp(health + value, 0, MaxHealth);
    }

    public void takeDamage(int damage) {

        adjustHealth(-damage);

        timeLeftOfTakingDamage = DamageTime;
    }

    public void die() {

        health = 0;

        // TODO: Play some sort of die animation.
    }

    /**
     * Requests the given animation to be played.
     */
    public void requestAnimation(Animation animation, Callback callback) {

        setAnimation(animation);

        requestingAnimation = true;

        requestedAnimationCallback = callback;
    }

    protected void setAnimation(Animation animation) {

        if (requestingAnimation) return;

        currentAnimation = animation;
    }

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

            if (currentAnimation != movingAnimation) {

                setAnimation(movingAnimation);

                if (!requestingAnimation) {

                    stateTime = 0.0f;
                }
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

                setAnimation(stationaryAnimation);

                if (!requestingAnimation) {

                    goingBackwards = false;

                    stateTime = 0.0f;
                }
            }
        }

        if (!goingBackwards) {

            stateTime += Gdx.graphics.getDeltaTime();
        }
        else {

            // Increment the state time by the whole animation if less than zero and decrement by
            // delta time to run the animation backwards.
            if (stateTime - Gdx.graphics.getDeltaTime() < 0.0f) {

                stateTime += movingAnimation.getAnimationDuration();
            }

            stateTime -= Gdx.graphics.getDeltaTime();
        }


        if (requestingAnimation) {

            if (stateTime >= currentAnimation.getAnimationDuration()) {

                requestingAnimation = false;

                if (requestedAnimationCallback != null) requestedAnimationCallback.callback();
            }
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

        if (0.0f < timeLeftOfParalysis) {

            timeLeftOfParalysis -= Gdx.graphics.getDeltaTime();
        }
        else if (timeLeftOfParalysis < 0.0f) {

            timeLeftOfParalysis = 0.0f;
        }

        if (0.0f < timeLeftBeforeBeginAbleToInteract) {

            timeLeftBeforeBeginAbleToInteract -= Gdx.graphics.getDeltaTime();
        }
        else if (timeLeftBeforeBeginAbleToInteract < 0.0f) {

            timeLeftBeforeBeginAbleToInteract = 0.0f;
        }

        if (currentTool != null) currentTool.tick();
    }

    /**
     * Gets called when the entity collides with a solid tile within the map.
     */
    protected abstract void collides();

    @Override
    public boolean flip() {

        return !facingRight;
    }

    @Override
    public TextureRegion getTextureRegion() {

        return currentTextureRegion;
    }

    @Override
    public void draw(SpriteBatch batch, Vector2 position, Vector2 size) {

        // Set the uniform location of the white overlay if not previous set.
        if (uniformLocation == -1) uniformLocation = batch.getShader().getUniformLocation("u_flash");

        if (0.0f < timeLeftOfTakingDamage) timeLeftOfTakingDamage -= Gdx.graphics.getDeltaTime();

        if (flip()) getTextureRegion().flip(true, false);

        if (Game.DebubDraw) {

            batch.draw(Game.debugDrawTexture, position.x - size.x / 2.0f, position.y - size.y / 2.0f, size.x, size.y);
        }

        // Shadow under the entity
        batch.draw(shadow, position.x - size.x / 2.0f, position.y - (size.y / 16) - size.y / 2.0f, size.x, size.y);


        // If the entity is taking a damage we apply a white overlay over the entity and its weapons
        // in the form of a white flash.
        if (0.0f < timeLeftOfTakingDamage && (int) (timeLeftOfTakingDamage / FlickersInterval) % 2 == 1) {

            batch.flush();

            batch.getShader().setUniformi(uniformLocation, 1);
        }

        batch.draw(getTextureRegion(), position.x - size.x / 2.0f, position.y - size.y / 2.0f, size.x, size.y);

        tmpVector.set(position.x - size.x / 2.0f, position.y - size.y / 2.0f);

        if (currentTool != null) currentTool.draw(batch, tmpVector, size);

        // We reset so that the overlay is only applied to this entity.
        if (0.0f < timeLeftOfTakingDamage && (int) (timeLeftOfTakingDamage / FlickersInterval) % 2 == 1) {

            batch.flush();

            batch.getShader().setUniformi(uniformLocation, 0);
        }

        if (flip()) getTextureRegion().flip(true, false);
    }

    @Override
    public void dispose() {

    }
}

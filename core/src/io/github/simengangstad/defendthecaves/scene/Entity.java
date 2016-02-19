package io.github.simengangstad.defendthecaves.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import io.github.simengangstad.defendthecaves.Callback;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.scene.gui.HealthBar;
import io.github.simengangstad.defendthecaves.scene.crafting.Inventory;
import io.github.simengangstad.defendthecaves.scene.gui.SpeechBubble;
import io.github.simengangstad.defendthecaves.scene.items.Potion;
import io.github.simengangstad.defendthecaves.scene.items.Shield;
import io.github.simengangstad.defendthecaves.scene.items.Weapon;

import java.util.ArrayList;

/**
 * Represents every livable entity the game.
 *
 * @author simengangstad
 * @since 28/11/15
 */
public abstract class Entity extends Collidable {

    /**
     * The inventory of the entity.
     */
    protected Inventory inventory = new Inventory(new Vector2(0.0f, 0.0f), new Vector2(300.0f, 400.0f), 3, 4);

    /**
     * Reference to the item the entity is currently holding.
     */
    private int currentItemPointer = 0;

    /**
     * The current item.
     */
    protected Item currentItem = null;

    /**
     * The maximum health an entity can have.
     */
    public static final int MaxHealth = 100;

    /**
     * The current health of the item.
     */
    public int health = MaxHealth;

    /**
     * Pixels/second.
     */
    public int speed = 300;

    /**
     * The interval between the interactions of item. This can be the attack of a weapon or throwing a stone.
     */
    private float IntervalBetweenInteractions = 0.25f;

    /**
     * This is the time left before the entity can interact again.
     */
    private float timeLeftBeforeBeginAbleToInteract = 0.0f;

    /**
     * If the entity shall raise the item because of the running (going up and down).
     */
    protected boolean raiseItem = false;

    /**
     * The time damage affects the entity.
     */
    private final float DamageTime = 0.4f;

    /**
     * The time left of taking damage.
     */
    private float timeLeftOfTakingDamage = 0.0f;

    /**
     * The time paralysis affects the entity.
     */
    private float TimeParalyzed = 0.5f;

    /**
     * The time left of paralysis.
     */
    private float timeLeftOfParalysis = 0;

    /**
     * The uniform location of the flickering variable.
     */
    private static int uniformLocation = -1;

    /**
     * The amount of flickers the entity experiences once taken damage.
     */
    private final int AmountOfFlickers = 3;

    /**
     * The time betweeen flickers.
     */
    private final float FlickersInterval = DamageTime / (AmountOfFlickers * 2);

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
     * The state time for animation.
     */
    protected float stateTime = 0.0f;

    /**
     * The shadow under the entity.
     */
    private TextureRegion shadow = new TextureRegion(Game.SpriteSheet, 0, 80, Game.SizeOfTileInPixelsInSpritesheet, Game.SizeOfTileInPixelsInSpritesheet);

    /**
     * The callback that gets fired after a requested animation has finished. See {@link Entity#requestAnimation(Animation, Callback)}
     */
    private Callback requestedAnimationCallback;

    /**
     * Speech bubble that is used for the entity to say messages.
     */
    private SpeechBubble speechBubble = new SpeechBubble();

    /**
     * The time the speech bubble is visible for.
     */
    private float speechBubbleVisibilityDuration = 0.0f;

    /**
     * The progress bar showing health.
     */
    private HealthBar healthBar = new HealthBar(MaxHealth);

    /**
     * Initializes the movable entity with a position, size and the locaitons for the animations.
     */
    public Entity(Vector2 position, Vector2 size, Animation stationaryAnimation, Animation movingAnimation) {

        this.position.set(position);
        this.size.set(size);

        this.stationaryAnimation = stationaryAnimation;
        this.movingAnimation = movingAnimation;

        currentTextureRegion = stationaryAnimation.getKeyFrame(stateTime, true);
        currentAnimation = stationaryAnimation;

        speechBubble.setWidth(200.0f);
        speechBubble.setVisible(false);

        inventory.host = this;
    }

    @Override
    public void create() {

        host.stage.addActor(speechBubble);
        host.stage.addActor(healthBar);
    }

    /**
     * Displays a speech bubble with the given text for the given duration.
     */
    public void displayMessage(String message, float duration) {

        displayMessage(message);

        speechBubbleVisibilityDuration = duration;
    }

    /**
     * Displays a speech bubble with the given text until commanded not to
     */
    public void displayMessage(String message) {

        if (speechBubbleVisibilityDuration != 0.0f) {

            return;
        }

        speechBubble.setText(message);
        speechBubble.setVisible(true);
    }

    /**
     * Hides the speech bubble.
     */
    public void hideMessage() {

        speechBubble.setVisible(false);
    }

    /**
     * Adds an item to the inventory.
     */
    public void addItem(Item item) {

        inventory.placeItem(item);

        item.parent = this;
        item.map = map;

        item.create();
    }

    /**
     * Adds an item at the given location inside the inventory.
     */
    public void addItemAtLocation(int x, int y, Item item) {

        inventory.placeItem(x, y, item);

        item.parent = this;
        item.map = map;

        item.create();

        if (x == (inventory.columns - 2) + currentItemPointer && y == 0) {

            currentItem = item;
        }
    }

    /**
     * Obtains the item at the given location.
     */
    public Item obtainItem(int x, int y) {

        Item item = inventory.obtainItem(x, y, 1).get(0);

        item.parent = null;

        return item;
    }

    /**
     * Removes the item and dereferences the entity as the parent entity of the item.
     */
    public void removeItem(Item item) {

        inventory.removeItem(item);

        item.parent = null;
    }

    /**
     * Shuffles the item in hand.
     */
    public void shuffleItem() {

        currentItemPointer = (currentItemPointer + 1) % 2;

        System.out.println("Set current pointer for current item to: " + ((inventory.columns - 2) + currentItemPointer));
    }

    public void interact(Vector2 interactionDirection) {

        if (timeLeftBeforeBeginAbleToInteract == 0.0f) {

            timeLeftBeforeBeginAbleToInteract = IntervalBetweenInteractions;

            if (currentItem != null) currentItem.interact(interactionDirection);
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

        healthBar.value = health;
    }

    public void takeDamage(int damage) {

        adjustHealth(-damage);

        timeLeftOfTakingDamage = DamageTime;
    }

    public void die() {

        health = 0;

        System.out.println("Entity (" + this + ") died, dropping inventory...");

        Vector2 force = new Vector2();

        for (int x = 0; x < inventory.columns; x++) {

            for (int y = 0; y < inventory.rows; y++) {

                for (Item item : inventory.obtainItem(x, y, inventory.getItemList(x, y).size())) {

                    item.position.set(position);

                    force.set(MathUtils.random(-1, 1), 1.0f);

                    item.applyForce(force, true, 0.5f);

                    host.addGameObject(item);
                }
            }
        }

        host.stage.getActors().removeValue(healthBar, true);
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

        super.tick();

        Vector3 vector = Game.vector3Pool.obtain();

        vector.set(position.x, position.y, 0.0f);

        ((Scene) host).player.camera.project(vector, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (speechBubble.isVisible()) {

            speechBubble.setPosition(vector.x + Game.EntitySize / 2.0f, vector.y + Game.EntitySize / 2.0f);

            if (0.0f < speechBubbleVisibilityDuration) {

                speechBubbleVisibilityDuration -= Gdx.graphics.getDeltaTime();

                if (speechBubbleVisibilityDuration < 1.0f) {

                    speechBubble.setColor(1.0f, 1.0f, 1.0f, Math.max(0.0f, speechBubbleVisibilityDuration));
                }
            }
            else if (speechBubbleVisibilityDuration < 0.0f) {

                speechBubble.setVisible(false);

                speechBubbleVisibilityDuration = 0.0f;
            }
            else {

                speechBubble.setColor(1.0f, 1.0f, 1.0f, 1.0f);
            }
        }


        healthBar.setPosition(vector.x - healthBar.getWidth() / 2.0f, vector.y + Game.EntitySize / 2.0f + 5.0f);

        Game.vector3Pool.free(vector);

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

            if (map.resolveCollision(position, delta, speed)) {

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

        currentTextureRegion = currentAnimation.getKeyFrame(stateTime, true);

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

        ArrayList<Item> list = inventory.getItemList((inventory.columns - 2) + currentItemPointer, 0);

        if (0 < list.size()) {

            currentItem = list.get(list.size() - 1);

            currentItem.tick();
        }
        else {

            currentItem = null;
        }
    }

    /**
     * Gets called when the entity collides with a solid tile within the map.
     */
    protected abstract void collides();

    public boolean flip() {

        return !facingRight;
    }

    public TextureRegion getTextureRegion() {

        return currentTextureRegion;
    }

    public void draw(SpriteBatch batch) {

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

        if (currentItem != null) {

            if (currentItem instanceof Weapon || currentItem instanceof Shield) {

                currentItem.position.set(position.x - size.x / 2.0f, position.y - size.y / 2.0f);
            }
            else {

                currentItem.position.set(position.x, position.y);
            }


            currentItem.draw(batch);
        }

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

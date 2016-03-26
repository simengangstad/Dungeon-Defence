package io.github.simengangstad.defendthecaves.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.GameObject;
import io.github.simengangstad.defendthecaves.scene.crafting.Inventory;

/**
 * Objects that can be placed in an {@link Inventory} and used by an {@link Entity}.
 *
 * @author simengangstad
 * @since 17/01/16
 */
public abstract class Item extends Collidable {

    /**
     * The speed at which the item is thrown.
     */
    public static final int ThrowingScalar = 20;

    /**
     * If the item is in the action of a throw.
     */
    private boolean thrown = false;

    /**
     * The entity which threw the item.
     */
    public Entity thrownFrom = null;

    /**
     * How fast the rotation of the item is.
     */
    private static final int RotationScalar = 1500;

    /**
     * The state if the item was flipped when it was thrown.
     */
    private boolean wasFlippedWhenThrown = false;

    /**
     * The damage applied when the item hits an entity.
     */
    private static final int ThrowDamage = 10;

    /**
     * The entity holding the item.
     */
    public Entity parent;

    /**
     * The position of the item in an inventory.
     */
    public Vector2 inventoryPosition = new Vector2();

    /**
     * The offset in y-direction of the item as the {@link Item#parent} is running whilst holding the item.
     */
    public float walkingOffset = 0.0f;

    /**
     * The drawable of the item.
     */
    protected TextureRegion textureRegion;

    /**
     * If the item is stackable in an inventory or not.
     */
    public boolean stackable;

    /**
     * If we're timing; if the item is in the scene.
     */
    private boolean timing = false;

    /**
     * The time the item has been in the scene.
     */
    private float timer = 0;

    /**
     * The information about the item.
     */
    protected String information = "";

    /**
     * Set the item to a stationary position where it doesn't get attracted to
     * an entity.
     */
    protected boolean placed = false;

    protected boolean collided = false;

    public boolean overwriteFlip = false;
    public boolean flip = false;

    /**
     * Initializes the item.
     */
    public Item(Vector2 position, Vector2 size, TextureRegion textureRegion, boolean stackable) {

        this.position.set(position);
        this.size.set(size);
        this.textureRegion = textureRegion;
        this.stackable = stackable;
    }

    @Override
    /**
     * Gets fired when the item is added to an inventory of an entity.
     */
    public void create() {}

    /**
     * Gets called when the collidable collides with an entity.
     */
    protected void collides(Entity entity) {

        Vector2 vector = Game.vector2Pool.obtain();

        vector.set(forceApplied).nor();

        ((Scene) host).damage(ThrowDamage, vector, position.x, position.y, size.x, size.y, thrownFrom);

        forceApplied.set(0.0f, 0.0f);

        vector.set(0.0f, 0.0f);

        applyForce(vector, true, 1.5f);

        thrown = false;
        thrownFrom = null;

        Game.vector2Pool.free(vector);

        collided = true;
    }

    /**
     * Gets called then item collides with a solid.
     */
    protected void collides() {

        collided = true;
    }

    public boolean isThrown() {

        return thrown;
    }

    public boolean isPlaced() {

        return placed;
    }

    /**
     * @return The parent holding the item.
     */
    public Entity getParent() {

        return parent;
    }

    public String getInformation() {

        return information;
    }

    /**
     * Makes the item interact. Fired by the player.
     *
     * @param direciton The direction of the interaction; from the entity to the
     *                  point of focus.
     */
    public abstract void interact(Vector2 direciton);

    /**
     * Removes the item from the parent entity and throws it in the given direction.
     */
    public void throwItem(Vector2 direction) {

        wasFlippedWhenThrown = parent.flip();
        thrownFrom = parent;

        parent.host.addGameObject(this);
        parent.removeItem(this);

        direction.nor();

        this.applyForce(direction.scl(ThrowingScalar), true, Float.MAX_VALUE);

        thrown = true;
    }

    public void toggleTimer() {

        timing = !timing;

        System.out.println("Timer toggled: " + timing);

        if (!timing) {

            timer = 0;
        }
    }

    public float getTimer() {

        return timer;
    }

    @Override
    public void tick() {

        super.tick();

        if (timing) {

            timer += Gdx.graphics.getDeltaTime();
        }

        if (map.retrieveCollisionPoint(position, size, delta, null) && parent == null) {

            collides();

            forceApplied.set(0.0f, 0.0f);

            thrown = false;
        }

        delta.set(0.0f, 0.0f);
    }

    /**
     * Draws the item with the specified position and size and not taking the
     * respecitve memebers of the item in consideration. Used in particular for
     * drawing the item within an {@link Inventory}.
     *
     * Use {@link GameObject#draw(SpriteBatch)}
     * for general purpose drawing.
     */
    public void draw(SpriteBatch batch, float x, float y, float width, float height, boolean flipAutomatically) {

        if (flip() && flipAutomatically) {

            getTextureRegion().flip(true, false);
        }

        if (Game.DebubDraw) {

            batch.draw(Game.debugDrawTexture, x, y, width, height);
        }

        batch.draw(getTextureRegion(), x, y, width, height);

        if (flip() && flipAutomatically) {

            getTextureRegion().flip(true, false);
        }
    }


    @Override
    public void draw(SpriteBatch batch) {

        if (isThrown()) {

            if (wasFlippedWhenThrown) {

                rotation = timer * RotationScalar;
            }
            else {

                rotation = -timer * RotationScalar;
            }

        }

        if (flip()) {

            getTextureRegion().flip(true, false);
        }

        if (Game.DebubDraw) {

            batch.draw(Game.debugDrawTexture, position.x - size.x / 2.0f, position.y - size.y / 2.0f + walkingOffset, size.x / 2.0f, size.y / 2.0f, size.x, size.y, 1.0f, 1.0f, rotation);
        }

        batch.draw(getTextureRegion(), position.x - size.x / 2.0f, position.y - size.y / 2.0f + walkingOffset, size.x / 2.0f, size.y / 2.0f, size.x, size.y, 1.0f, 1.0f, rotation);

        if (flip()) {

            getTextureRegion().flip(true, false);
        }
    }

    @Override
    public TextureRegion getTextureRegion() {

        return textureRegion;
    }

    @Override
    public boolean flip() {

        if (overwriteFlip) {

            return flip;
        }
        else {

            if (parent == null) {

                return false;
            }

            return !parent.flip();
        }
    }

    @Override
    public void dispose() {

    }
}

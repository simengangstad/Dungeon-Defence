package io.github.simengangstad.defendthecaves.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.scene.gui.Inventory;

/**
 * Objects that can be placed in an {@link Inventory} and used by an {@link Entity}.
 *
 * @author simengangstad
 * @since 17/01/16
 */
public abstract class Item extends Collidable {

    /**
     * The entity holding the item.
     */
    protected Entity parent;

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
    public final boolean stackable;

    /**
     * If we're timing; if the item is in the scene.
     */
    private boolean timing = false;

    /**
     * The time the item has been in the scene.
     */
    private float timer = 0;

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
     * @return The parent holding the item.
     */
    public Entity getParent() {

        return parent;
    }

    /**
     * Makes the item interact. Fired by the player.
     *
     * @param direciton The direction of the interaction; from the entity to the
     *                  point of focus.
     */
    public abstract void interact(Vector2 direciton);

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

        if (map.retrieveCollisionPoint(position, size, delta, null)) {

            forceApplied.set(0.0f, 0.0f);
        }

        delta.set(0.0f, 0.0f);
    }

    /**
     * Draws the item with the specified position and size and not taking the
     * respecitve memebers of the item in consideration. Used in particular for
     * drawing the item within an {@link Inventory}.
     *
     * Use {@link io.github.simengangstad.defendthecaves.components.GameObject#draw(SpriteBatch)}
     * for general purpose drawing.
     */
    public void draw(SpriteBatch batch, float x, float y, float width, float height) {

        if (flip()) {

            getTextureRegion().flip(true, false);
        }

        if (Game.DebubDraw) {

            batch.draw(Game.debugDrawTexture, x, y, width, height);
        }

        batch.draw(getTextureRegion(), x, y, width, height);

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

        if (parent == null) {

            return false;
        }

        return !parent.flip();
    }

    @Override
    public void dispose() {

    }
}

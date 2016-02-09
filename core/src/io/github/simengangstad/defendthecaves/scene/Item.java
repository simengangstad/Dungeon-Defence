package io.github.simengangstad.defendthecaves.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * Objects that can be placed in an {@link Inventory}.
 *
 * @author simengangstad
 * @since 17/01/16
 */
public class Item extends Collidable {

    protected TextureRegion textureRegion;

    private boolean timing = false;

    public final boolean stackable;

    /**
     * The time the item has been in the scene.
     */
    private float timer = 0;

    public Item(Vector2 position, Vector2 size, TextureRegion textureRegion, boolean stackable) {

        super(position, size);

        this.textureRegion = textureRegion;

        this.stackable = stackable;
    }

    @Override
    public TextureRegion getTextureRegion() {

        return textureRegion;
    }

    @Override
    public boolean flip() {

        return false;
    }

    public void collides() {

        forceApplied.set(0.0f, 0.0f);
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

        if (map.retrieveCollisionPoint(getPosition(), forceApplied, 1, null)) {

            collides();
        }
    }

    @Override
    public void dispose() {}
}

package io.github.simengangstad.defendthecaves;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

/**
 * @author simengangstad
 * @since 15/11/15
 */
public abstract class GameObject implements Disposable {

    /**
     * The position (centre) and size (diameter on both axes).
     */
    public Vector2 position = new Vector2(), size = new Vector2();

    /**
     * The rotation of the game object.
     */
    public float rotation = 0.0f;

    /**
     * The host of the game object.
     */
    public Container host;

    /**
     * Instantiates the game object. Gets called once added to the scene. The host of the
     * game object is available for use at this point.
     */
    public abstract void create();

    /**
     * Updates the state of the game object.
     */
    public abstract void tick();

    /**
     * Draws the drawable with the given sprite batch where position is the centre of the drawable.
     */
    public void draw(SpriteBatch batch) {

        if (flip()) {

            getTextureRegion().flip(true, false);
        }

        if (Game.Debug) {

            batch.draw(Game.debugDrawTexture, position.x - size.x / 2.0f, position.y - size.y / 2.0f, size.x / 2.0f, size.y / 2.0f, size.x, size.y, 1.0f, 1.0f, rotation);
        }

        batch.draw(getTextureRegion(), position.x - size.x / 2.0f, position.y - size.y / 2.0f, size.x / 2.0f, size.y / 2.0f, size.x, size.y, 1.0f, 1.0f, rotation);

        if (flip()) {

            getTextureRegion().flip(true, false);
        }
    }

    /**
     * @return If the drawable should be drawn flipped.
     */
    public abstract boolean flip();

    /**
     * @return The drawable to be drawn.
     */
    public abstract TextureRegion getTextureRegion();

    public boolean inside(float x, float y) {

        return position.x - size.x / 2.0f <= x && x < position.x + size.x / 2.0f && position.y - size.y / 2.0f <= y && y < position.y + size.y / 2.0f;
    }
}

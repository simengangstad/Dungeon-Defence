package io.github.simengangstad.defendthecaves.components;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * @author simengangstad
 * @since 15/11/15
 */
public interface Drawable {

    /**
     * @return the texture region the shall drawn.
     */
    TextureRegion getTextureRegion();

    /**
     * Draws the drawable with the given sprite batch.
     */
    default void draw(SpriteBatch batch, Vector2 position, Vector2 size) {

        batch.draw(getTextureRegion(), position.x, position.y, size.x, size.y);
    }
}

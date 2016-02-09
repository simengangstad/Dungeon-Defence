package io.github.simengangstad.defendthecaves.components;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.defendthecaves.Game;

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
      * @return If the texture region should be drawn flipped (on x-axis).
     */
    boolean flip();

    /**
     * Draws the drawable with the given sprite batch where position is the centre of the drawable.
     */
    default void draw(SpriteBatch batch, Vector2 position, Vector2 size) {

        if (flip()) {

            getTextureRegion().flip(true, false);
        }

        if (Game.DebubDraw) {

            batch.draw(Game.debugDrawTexture, position.x - size.x / 2.0f, position.y - size.y / 2.0f, size.x, size.y);
        }

        batch.draw(getTextureRegion(), position.x - size.x / 2.0f, position.y - size.y / 2.0f, size.x, size.y);

        if (flip()) {

            getTextureRegion().flip(true, false);
        }
    }
}

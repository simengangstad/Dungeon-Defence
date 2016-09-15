package io.github.simengangstad.defendthecaves.scene.gui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * @author simengangstad
 * @since 07/04/16
 */
public interface SlotItem {

    TextureRegion getSlotTextureRegion();

    default void draw(Batch batch, float x, float y, float width, float height) {

        boolean flipped = getSlotTextureRegion().isFlipX();

        if (flipped) {

            getSlotTextureRegion().flip(true, false);
        }

        batch.draw(getSlotTextureRegion(), x, y, width, height);

        if (flipped) {

            getSlotTextureRegion().flip(true, false);
        }
    }
}

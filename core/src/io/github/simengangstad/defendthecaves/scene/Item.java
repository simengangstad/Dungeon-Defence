package io.github.simengangstad.defendthecaves.scene;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.components.Drawable;
import io.github.simengangstad.defendthecaves.components.GameObject;

/**
 * Objects that can be placed in an {@link Inventory}.
 *
 * @author simengangstad
 * @since 17/01/16
 */
public class Item extends GameObject implements Drawable {

    protected TextureRegion textureRegion;

    public Item(Vector2 position, Vector2 size, TextureRegion textureRegion) {

        super(position, size);

        this.textureRegion = textureRegion;
    }

    @Override
    public TextureRegion getTextureRegion() {

        return textureRegion;
    }

    @Override
    public boolean flip() {

        return false;
    }

    @Override
    public void tick() {

    }

    @Override
    public void dispose() {}
}

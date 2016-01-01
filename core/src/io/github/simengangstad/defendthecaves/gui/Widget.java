package io.github.simengangstad.defendthecaves.gui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.defendthecaves.components.Drawable;
import io.github.simengangstad.defendthecaves.components.GameObject;

/**
 * @author simengangstad
 * @since 20/12/15
 */
public abstract class Widget extends GameObject implements Drawable {

    public boolean visible = true;

    private TextureRegion textureRegion;

    public Widget(Vector2 position, Vector2 size, TextureRegion textureRegion) {

        super(position, size);

        this.textureRegion = textureRegion;
    }

    public abstract void tick();

    @Override
    public void draw(SpriteBatch batch, Vector2 position, Vector2 size) {

        if (!visible) return;

        batch.draw(getTextureRegion(), position.x, position.y, size.x, size.y);
    }

    @Override
    public TextureRegion getTextureRegion() {

        return textureRegion;
    }

    @Override
    public void dispose() {}

    @Override
    public boolean flip() {

        return false;
    }
}

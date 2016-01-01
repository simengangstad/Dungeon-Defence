package io.github.simengangstad.defendthecaves.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * @author simengangstad
 * @since 20/12/15
 */
public abstract class KeyButton extends Button {

    private final int key;

    public KeyButton(Vector2 position, Vector2 size, TextureRegion textureRegion, int key) {

        super(position, size, textureRegion);

        this.key = key;
    }

    @Override
    public boolean condition() {

        return Gdx.input.isKeyPressed(key);
    }


    @Override
    public void draw(SpriteBatch batch, Vector2 position, Vector2 size) {

        if (!visible) return;

        super.draw(batch, position, size);

        Font.draw(batch, Input.Keys.toString(key), 0, 0, position.x, position.y, 4);
    }
}

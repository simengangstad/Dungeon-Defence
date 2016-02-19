package io.github.simengangstad.defendthecaves.scene.items;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.scene.Item;

/**
 * @author simengangstad
 * @since 24/12/15
 */
public class Shield extends Item {

    private static TextureRegion textureRegion = new TextureRegion(Game.SpriteSheet, 0, 128, 32, 32);

    public Shield() {

        super(new Vector2(), new Vector2(Game.EntitySize, Game.EntitySize), textureRegion, false);

        information = "Arr, yeah, this'll protect me";
    }

    @Override
    public void interact(Vector2 direciton) {

    }

    @Override
    public void draw(SpriteBatch batch) {

        float xDelta;
        float yDelta = -size.y / 2.0f - (size.y / 16) * 2;

        if (flip()) {

            getTextureRegion().flip(true, false);

            xDelta = -size.x / 2.0f + (size.x / 16) * 4;
        }
        else {

            xDelta = -size.x / 2.0f - (size.x / 16) * 4;
        }

        batch.draw(getTextureRegion(), position.x + xDelta, position.y + yDelta + walkingOffset, size.x * 2, size.y * 2);

        if (flip()) {

            getTextureRegion().flip(true, false);
        }
    }
}

package io.github.simengangstad.defendthecaves.scene.weapons;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.defendthecaves.Callback;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.scene.Tool;

/**
 * @author simengangstad
 * @since 24/12/15
 */
public class Shield extends Tool {

    private TextureRegion textureRegions = new TextureRegion(Game.spriteSheet, 0, 128, 32, 32);


    public Shield() {

        super(1.0f, new Callback() {

            @Override
            public void callback() {}
        });
    }

    @Override
    protected void interacting(Vector2 interactingDirection) {}

    @Override
    protected void doneInteracting() {

    }

    @Override
    public void draw(SpriteBatch batch, Vector2 position, Vector2 size) {

        super.draw(batch, position, size);

        float xDelta;
        float yDelta = -size.y / 2.0f - (size.y / 16) * 2;

        if (flip) {

            getTextureRegion().flip(true, false);

            xDelta = -size.x / 2.0f + (size.x / 16) * 4;
        }
        else {

            xDelta = -size.x / 2.0f - (size.x / 16) * 4;
        }

        batch.draw(getTextureRegion(), position.x + xDelta + offset.x, position.y + yDelta + offset.y, size.x * 2, size.y * 2);

        if (flip) {

            getTextureRegion().flip(true, false);
        }
    }

    @Override
    public TextureRegion getTextureRegion() {

        return textureRegions;
    }
}

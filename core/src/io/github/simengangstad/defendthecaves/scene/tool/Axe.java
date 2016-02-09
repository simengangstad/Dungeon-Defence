package io.github.simengangstad.defendthecaves.scene.tool;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.defendthecaves.Callback;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.scene.RotatableWeapon;

/**
 * @author simengangstad
 * @since 28/11/15
 */
public class Axe extends RotatableWeapon {

    public Axe(Callback interactionCallback) {

        super(10, 0.225f, interactionCallback, -45, 45);

        TextureRegion[] textureRegions = new TextureRegion[5];

        int index = 0;

        for (int x = 0; x < 32 * textureRegions.length; x += 32) {

            textureRegions[index] =  new TextureRegion(Game.SpriteSheet, x, 96, 32, 32);

            index++;
        }

        TextureRegion[] attackTextureRegions = new TextureRegion[11];

        index = 0;

        for (int x = 160; x < 160 + 32 * attackTextureRegions.length; x += 32) {

            attackTextureRegions[index] =  new TextureRegion(Game.SpriteSheet, x, 96, 32, 32);

            index++;
        }

        setTextures(textureRegions, attackTextureRegions);

        size.set(120, 100);
    }

    @Override
    public void draw(SpriteBatch batch, Vector2 position, Vector2 size) {

        super.draw(batch, position, size);

        float xDelta;
        float yDelta = -size.y / 2.0f - (size.y / 16) * 2;

        if (flip) {

            getTextureRegion().flip(true, false);

            xDelta = -size.x / 2.0f - (size.x / 16);
        }
        else {

            xDelta = -size.x / 2.0f + (size.x / 16);
        }

        batch.draw(getTextureRegion(), position.x + xDelta + offset.x, position.y + yDelta + offset.y, size.x * 2, size.y * 2);

        if (flip) {

            getTextureRegion().flip(true, false);
        }
    }
}

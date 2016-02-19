package io.github.simengangstad.defendthecaves.scene.items;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.simengangstad.defendthecaves.Callback;
import io.github.simengangstad.defendthecaves.Game;

/**
 * @author simengangstad
 * @since 29/01/16
 */
public class Cudgel extends RotatableWeapon {

    private static TextureRegion[] textureRegions = new TextureRegion[5], attackTextureRegions = new TextureRegion[11];

    static {

        int index = 0;

        for (int x = 0; x < 32 * textureRegions.length; x += 32) {

            textureRegions[index] =  new TextureRegion(Game.SpriteSheet, x, 272, 32, 32);

            index++;
        }

        index = 0;

        for (int x = 160; x < 160 + 32 * attackTextureRegions.length; x += 32) {

            attackTextureRegions[index] =  new TextureRegion(Game.SpriteSheet, x, 272, 32, 32);

            index++;
        }
    }

    public Cudgel(Callback interactionCallback) {

        super(10, 0.35f, interactionCallback, -45, 45, textureRegions, attackTextureRegions);

        size.set(Game.EntitySize, Game.EntitySize);

        information = "Filth's weapon\nAttack damage: " + attackDamage;
    }

    @Override
    public void draw(SpriteBatch batch) {

        float xDelta;
        float yDelta = -size.y / 2.0f - (size.y / 16) * 2;

        if (!flip()) {

            getTextureRegion().flip(true, false);

            xDelta = -size.x / 2.0f - (size.x / 16) * 6;
        }
        else {

            xDelta = -size.x / 2.0f + (size.x / 16) * 6;
        }

        batch.draw(getTextureRegion(), position.x + xDelta, position.y + yDelta + walkingOffset, size.x * 2, size.y * 2);

        if (!flip()) {

            getTextureRegion().flip(true, false);
        }
    }
}

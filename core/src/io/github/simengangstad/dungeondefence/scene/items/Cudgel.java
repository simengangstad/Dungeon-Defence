package io.github.simengangstad.dungeondefence.scene.items;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.simengangstad.dungeondefence.Callback;
import io.github.simengangstad.dungeondefence.Game;

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

        super.stackable = true;

        size.set(Game.EntitySize, Game.EntitySize);

        information = "Filth's weapon\nAttack damage: " + attackDamage;
    }

    @Override
    public TextureRegion getSlotTextureRegion() {

        return textureRegions[0];
    }

    @Override
    protected float xOffset() {

        if (flip()) {

            return -(size.x / 16) * 6;
        }
        else {

            return (size.x / 16) * 6;
        }
    }

    @Override
    protected float yOffset() {

        return -(size.y / 16) * 2;
    }
}

package io.github.simengangstad.defendthecaves.scene.weapons;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.scene.RotatableWeapon;

/**
 * @author simengangstad
 * @since 28/11/15
 */
public class Axe extends RotatableWeapon {

    private static TextureRegion[] textureRegions;
    private static TextureRegion[] attackTextureRegions;

    static {

        textureRegions = new TextureRegion[3 * 2];

        int index = 0;

        for (int y = 80; y <= 80 + 32; y += 32) {

            for (int x = 16; x < 16 + 32 * 3; x += 32) {

                textureRegions[index] =  new TextureRegion(Game.spriteSheet, x, y, 32, 32);

                index++;
            }
        }

        attackTextureRegions = new TextureRegion[3 * 2];

        index = 0;

        for (int y = 208; y < 208 + 32; y += 32) {

            for (int x = 16; x <= 16; x += 1) {

                attackTextureRegions[index] =  new TextureRegion(Game.spriteSheet, x, y, 32, 32);

                index++;
            }
        }
    }

    public Axe() {

        super(10, 140, 50, 140, textureRegions, attackTextureRegions);
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

        batch.draw(getTextureRegion(), position.x + xDelta, position.y + yDelta, size.x * 2, size.y * 2);

        if (flip) {

            getTextureRegion().flip(true, false);
        }

        if (isAttacking()) {

            float attackXDelta = 0.0f;
            float attackYDelta = -size.y / 2.0f;

            if (attackTextureRegionIsFlipped) {

                currentAttackTextureRegion.flip(true, false);
                attackXDelta = size.x * 2;
            }


/*
            switch (currentAttackTextureRegionIndex) {

                case 0:

                    attackYDelta += pixelDelta * 3;

                    break;

                case 1:

                    attackYDelta += pixelDelta * 2;

                    break;

                case 2:

                    attackYDelta += pixelDelta;

                    break;

                case 3:

                    attackYDelta -= pixelDelta * 3;

                    break;

                case 4:

                    attackYDelta -= pixelDelta * 4;

                    break;

                case 5:

                    attackYDelta -= pixelDelta * 5;

                    break;
            }*/


            batch.setColor(1.0f, 1.0f, 1.0f, Math.max(super.getStateTime() / super.attackDuration, 0.0f));

            batch.draw(currentAttackTextureRegion, position.x + xDelta - size.x + attackXDelta, position.y + attackYDelta, size.x * 2, size.y * 2);

            batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);

            if (attackTextureRegionIsFlipped) {

                currentAttackTextureRegion.flip(true, false);
            }
        }
    }
}

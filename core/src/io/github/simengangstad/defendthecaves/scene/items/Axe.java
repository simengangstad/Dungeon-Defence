package io.github.simengangstad.defendthecaves.scene.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.defendthecaves.Callback;
import io.github.simengangstad.defendthecaves.Game;

/**
 * @author simengangstad
 * @since 28/11/15
 */
public class Axe extends RotatableWeapon {

    private static TextureRegion[] textureRegions = new TextureRegion[5], attackTextureRegions = new TextureRegion[11];

    static {

        int index = 0;

        for (int x = 0; x < 32 * textureRegions.length; x += 32) {

            textureRegions[index] =  new TextureRegion(Game.SpriteSheet, x, 96, 32, 32);

            index++;
        }

        index = 0;

        for (int x = 160; x < 160 + 32 * attackTextureRegions.length; x += 32) {

            attackTextureRegions[index] =  new TextureRegion(Game.SpriteSheet, x, 96, 32, 32);

            index++;
        }
    }

    public static Sound Swing = Gdx.audio.newSound(Gdx.files.internal("assets/sfx/axe-swing.ogg")),
                        Hit = Gdx.audio.newSound(Gdx.files.internal("assets/sfx/axe-hit.wav"));


    public Axe(Callback interactionCallback) {

        super(50, 0.225f, interactionCallback, -45, 45, textureRegions, attackTextureRegions);

        size.set(Game.EntitySize, Game.EntitySize);

        information = "A dwarf's deerest friend\nAttack damage: " + attackDamage;
    }

    @Override
    public void interact(Vector2 direction) {

        super.interact(direction);

        if (Game.PlaySound) Swing.play(0.5F);
    }

    @Override
    public TextureRegion getSlotTextureRegion() {

        return textureRegions[0];
    }

    @Override
    protected float xOffset() {

        if (flip()) {

            return -(size.x / 16);
        }
        else {

            return (size.x / 16);
        }
    }

    @Override
    protected float yOffset() {


        return -(size.y / 16) * 2;
    }
}

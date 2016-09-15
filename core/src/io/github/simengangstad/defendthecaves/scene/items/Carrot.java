package io.github.simengangstad.defendthecaves.scene.items;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.scene.Item;

/**
 * Created by simengangstad on 02/09/16.
 */
public class Carrot extends Item {

    public Carrot(Vector2 position) {

        super(position, new Vector2(Game.ItemSize, Game.ItemSize), new TextureRegion(Game.SpriteSheet, 128, 208, 16, 16), true);

        information = "Carrot";
    }


    @Override
    public TextureRegion getSlotTextureRegion() {

        return getTextureRegion();
    }

    @Override
    public void interact(Vector2 direction) {

        // TODO: Enable spot light vision

    }
}

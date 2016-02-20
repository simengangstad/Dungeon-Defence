package io.github.simengangstad.defendthecaves.scene.items;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.scene.Item;

/**
 * @author simengangstad
 * @since 19/02/16
 */
public class Coal extends Item {

    public Coal(Vector2 position) {

        super(position, new Vector2(Game.ItemSize * 1.5f, Game.ItemSize * 1.5f), new TextureRegion(Game.SpriteSheet, 64, 208, 16, 16), true);

        information = "Black gold";
    }

    @Override
    public void interact(Vector2 direciton) {

    }
}

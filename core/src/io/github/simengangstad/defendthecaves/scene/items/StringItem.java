package io.github.simengangstad.defendthecaves.scene.items;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.scene.Item;

/**
 * @author simengangstad
 * @since 22/02/16
 */
public class StringItem extends Item {

    private static final TextureRegion textureRegion = new TextureRegion(Game.SpriteSheet, 96, 208, 16, 16);

    public StringItem(Vector2 position) {

        super(position, new Vector2(Game.ItemSize, Game.ItemSize), textureRegion, true);

        information = "Strangle those bastards with these fella";
    }

    @Override
    public void interact(Vector2 direciton) {

    }
}

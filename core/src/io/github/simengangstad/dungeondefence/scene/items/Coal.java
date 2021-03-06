package io.github.simengangstad.dungeondefence.scene.items;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.dungeondefence.Game;

/**
 * @author simengangstad
 * @since 19/02/16
 */
public class Coal extends MinableItem {

    public Coal(Vector2 position) {

        super(position, new Vector2(Game.ItemSize, Game.ItemSize), new TextureRegion(Game.SpriteSheet, 64, 208, 16, 16));

        information = "Black gold";
    }

    @Override
    public void interact(Vector2 direction) {

    }

    @Override
    public TextureRegion getSlotTextureRegion() {

        return super.getTextureRegion();
    }
}

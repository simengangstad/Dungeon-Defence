package io.github.simengangstad.defendthecaves.scene.items;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.defendthecaves.Game;

/**
 * @author simengangstad
 * @since 14/02/16
 */
public class Rock extends MinableItem {

    private static TextureRegion[] textureRegions = new TextureRegion[] {

            new TextureRegion(Game.SpriteSheet, 384, 0, 16, 16),
            new TextureRegion(Game.SpriteSheet, 400, 0, 16, 16),
            new TextureRegion(Game.SpriteSheet, 416, 0, 16, 16),
            new TextureRegion(Game.SpriteSheet, 432, 0, 16, 16),
    };


    public Rock(Vector2 position) {

        super(position, new Vector2(), textureRegions[MathUtils.random(textureRegions.length - 1)]);

        this.size.set(80.0f, 80.0f);

        information = "Plain ol' rocky";
    }

    @Override
    public void interact(Vector2 direction) {

    }

    @Override
    public TextureRegion getSlotTextureRegion() {

        return super.getTextureRegion();
    }
}

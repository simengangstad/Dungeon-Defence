package io.github.simengangstad.dungeondefence.scene.items;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.dungeondefence.Game;
import io.github.simengangstad.dungeondefence.scene.Item;

/**
 * @author simengangstad
 * @since 19/02/16
 */
public class Wood extends Item {

    public Wood(Vector2 position) {

        super(position, new Vector2(Game.ItemSize * 1.5f, Game.ItemSize * 1.5f), new TextureRegion(Game.SpriteSheet, 80, 208, 16, 16), true);

        information = "Wood";
    }

    @Override
    public void interact(Vector2 direction) {

    }

    @Override
    public void draw(SpriteBatch batch) {

        Vector2 tmpPosition = Game.vector2Pool.obtain();

        tmpPosition.set(position);

        if (parent != null) {

            if (parent.currentItem == this) {

                if (parent.flip()) {

                    rotation = 20.0f;
                    position.x += 12.5f;
                }
                else {

                    rotation = -20.0f;
                    position.x -= 12.5f;
                }
            }
            else {

                rotation = 0.0f;
            }
        }

        super.draw(batch);

        position.set(tmpPosition);

        Game.vector2Pool.free(tmpPosition);
    }

    @Override
    public TextureRegion getSlotTextureRegion() {

        return super.getTextureRegion();
    }
}

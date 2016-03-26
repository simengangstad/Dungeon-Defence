package io.github.simengangstad.defendthecaves.scene.items;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

        information = "Strangle those bastards with this fella";
    }

    @Override
    public void interact(Vector2 direciton) {

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

        position.y -= 20.0f;

        super.draw(batch);

        position.set(tmpPosition);

        Game.vector2Pool.free(tmpPosition);
    }
}

package io.github.simengangstad.dungeondefence.scene.items;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.dungeondefence.Game;
import io.github.simengangstad.dungeondefence.pathfinding.Coordinate;
import io.github.simengangstad.dungeondefence.scene.Item;

/**
 * @author simengangstad
 * @since 17/01/16
 */
public class Key extends Item {

    public final Coordinate positionOfDoor;

    public Key(Vector2 position, Coordinate positionOfDoor) {

        super(position, new Vector2(Game.EntitySize, Game.EntitySize), new TextureRegion(Game.SpriteSheet, 0, 208, 16, 16), true);

        this.positionOfDoor = positionOfDoor;

        information = "The key to glory " + this.hashCode();
    }

    @Override
    public void interact(Vector2 direction) {

    }

    @Override
    public boolean flip() {

        return !super.flip();
    }

    @Override
    public void draw(SpriteBatch batch) {

        Vector2 tmpPosition = Game.vector2Pool.obtain();

        tmpPosition.set(position);

        if (parent != null) {

            if (parent.currentItem == this) {

                if (!parent.flip()) {

                    rotation = 20.0f;
                    position.x -= 30.0f;
                }
                else {

                    rotation = -20.0f;
                    position.x += 30.0f;
                }

                position.y -= 25.0f;
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

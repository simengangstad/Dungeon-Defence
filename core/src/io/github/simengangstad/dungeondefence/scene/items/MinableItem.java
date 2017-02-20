package io.github.simengangstad.dungeondefence.scene.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.dungeondefence.Game;
import io.github.simengangstad.dungeondefence.scene.Item;

/**
 * Abstract class for all items that are minable and that drop with an
 * effect once mined.
 *
 * Created by simengangstad on 29/08/16.
 */
public abstract class MinableItem extends Item {

    public final float TimeToFall = 3.0f;

    protected float timeLeftToFall = 0.0f;

    public MinableItem(Vector2 position, Vector2 size, TextureRegion textureRegion) {

        super(position, size, textureRegion, true);
    }

    @Override
    public void create() {

        if (parent == null) {

            applyForce(new Vector2(MathUtils.random(-6, 6), 5.0f), true, 1.0f);

            timeLeftToFall = TimeToFall;
        }
    }

    @Override
    public void tick() {

        super.tick();

        if (0.0f < timeLeftToFall) {

            timeLeftToFall -= Gdx.graphics.getDeltaTime();
        }
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

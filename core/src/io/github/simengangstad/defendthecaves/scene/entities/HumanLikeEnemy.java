package io.github.simengangstad.defendthecaves.scene.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.scene.RotatableWeapon;
import io.github.simengangstad.defendthecaves.scene.TextureUtil;
import io.github.simengangstad.defendthecaves.scene.item.Cudgel;

/**
 * @author simengangstad
 * @since 27/01/16
 */
public class HumanLikeEnemy extends Enemy {

    public HumanLikeEnemy(Vector2 position, Vector2 size, int coverageRadius, Player player) {

        super(position,
                player,
                coverageRadius,
                size,
                TextureUtil.getAnimation(Game.OrcStationary, 16, 0.2f, Animation.PlayMode.NORMAL),
                TextureUtil.getAnimation(Game.OrcMoving, 16, 0.075f, Animation.PlayMode.NORMAL));
    }

    @Override
    public void create() {

        super.create();

        addItemAtLocation(1, 0, new Cudgel(() -> {}));
    }

    @Override
    protected void hurtPlayer(Vector2 tmpVector) {

        interact(tmpVector);
    }

    @Override
    protected void noticedPlayer(Vector2 direction) {}

    @Override
    public void draw(SpriteBatch batch) {

        if (currentItem != null) {

            if (raiseItem) {

                currentItem.walkingOffset = size.y / 16.0f;
            }
            else {

                currentItem.walkingOffset = 0.0f;
            }
        }

        if (currentItem instanceof RotatableWeapon) {

            ((RotatableWeapon) currentItem).setRotation((int) (Math.atan((playerPositionReference.y - position.y) / (playerPositionReference.x - position.x)) * 180 / Math.PI));
        }

        super.draw(batch);
    }
}

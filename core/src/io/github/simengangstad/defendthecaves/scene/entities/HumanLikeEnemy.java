package io.github.simengangstad.defendthecaves.scene.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.scene.items.RotatableWeapon;
import io.github.simengangstad.defendthecaves.scene.TextureUtil;
import io.github.simengangstad.defendthecaves.scene.items.Cudgel;

/**
 * @author simengangstad
 * @since 27/01/16
 */
public class HumanLikeEnemy extends Enemy {

    public HumanLikeEnemy(Vector2 position, Vector2 size, Player player) {

        super(position,
                player,
                5,
                size,
                TextureUtil.getAnimation(Game.OrcStationary, 16, 0.2f, Animation.PlayMode.NORMAL),
                TextureUtil.getAnimation(Game.OrcMoving, 16, 0.075f, Animation.PlayMode.NORMAL));
    }

    @Override
    public void create() {

        super.create();

        addItemAtLocation(0, 0, new Cudgel(null));
    }

    @Override
    protected void hurtPlayer(Vector2 tmpVector) {

        interact(tmpVector);
    }

    @Override
    protected void noticedPlayer(Vector2 direction) {}

    @Override
    public void draw(SpriteBatch batch) {

        if (currentItem instanceof RotatableWeapon) {

            ((RotatableWeapon) currentItem).setRotation((int) (Math.atan((playerPositionReference.y - position.y) / (playerPositionReference.x - position.x)) * 180 / Math.PI));
        }

        super.draw(batch);
    }
}

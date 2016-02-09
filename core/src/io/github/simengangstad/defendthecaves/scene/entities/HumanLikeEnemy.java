package io.github.simengangstad.defendthecaves.scene.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.scene.RotatableWeapon;
import io.github.simengangstad.defendthecaves.scene.TextureUtil;

/**
 * @author simengangstad
 * @since 27/01/16
 */
public class HumanLikeEnemy extends Enemy {

    public HumanLikeEnemy(Vector2 position, Vector2 size, Player player) {

        super(position,
                player,
                10,
                size,
                TextureUtil.getAnimation(Game.OrcStationary, 16, 0.2f, Animation.PlayMode.NORMAL),
                TextureUtil.getAnimation(Game.OrcMoving, 16, 0.075f, Animation.PlayMode.NORMAL));
    }

    @Override
    protected void hurtPlayer(Vector2 tmpVector) {

        interact(tmpVector);
    }

    @Override
    protected void noticedPlayer(Vector2 direction) {}

    @Override
    public void draw(SpriteBatch batch, Vector2 position, Vector2 size) {

        if (raiseTool) {

            currentTool.offset.set(0.0f, (size.y / 16.0f));
        }
        else {

            currentTool.offset.set(0.0f, 0.0f);
        }

        if (currentTool instanceof RotatableWeapon) {

            ((RotatableWeapon) currentTool).setRotation((int) (Math.atan((playerPositionReference.y - getPosition().y) / (playerPositionReference.x - getPosition().x)) * 180 / Math.PI));
        }

        super.draw(batch, position, size);
    }
}

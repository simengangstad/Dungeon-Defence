package io.github.simengangstad.defendthecaves.scene.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.scene.Scene;
import io.github.simengangstad.defendthecaves.scene.TextureUtil;

/**
 * @author simengangstad
 * @since 27/01/16
 */
public class Snake extends Enemy {

    private static final Animation BiteAnimation = TextureUtil.getAnimation(Game.SnakeBiting, 32, 0.025f, Animation.PlayMode.NORMAL);

    public int attackDamage = 30;

    public Snake(Vector2 position, Player player, int coverageRadius, Vector2 size) {

        super(position,
                player,
                coverageRadius,
                size,
                TextureUtil.getAnimation(Game.SnakeStationary, 32, 0.4f, Animation.PlayMode.NORMAL),
                TextureUtil.getAnimation(Game.SnakeMoving, 32, 0.08f, Animation.PlayMode.NORMAL));
    }

    @Override
    protected void hurtPlayer(Vector2 tmpVector) {

        requestAnimation(BiteAnimation, () -> ((Scene) host).damage(attackDamage, tmpVector, getPosition().x + (!flip() ? getSize().x / 2.0f : -getSize().x / 2.0f), getPosition().y, getSize().x, getSize().y, Snake.this));
    }

    @Override
    protected void noticedPlayer(Vector2 direction) {}

    @Override
    public void draw(SpriteBatch batch, Vector2 position, Vector2 size) {

        super.draw(batch, position, size);

        if (Game.DebubDraw) {

            batch.draw(Game.debugDrawTexture, position.x + (!flip() ? 0 : -size.x), position.y - size.y / 2.0f, size.x, size.y);
        }
    }
}

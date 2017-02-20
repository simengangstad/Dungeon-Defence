package io.github.simengangstad.dungeondefence.scene.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.dungeondefence.Callback;
import io.github.simengangstad.dungeondefence.Game;
import io.github.simengangstad.dungeondefence.scene.Scene;
import io.github.simengangstad.dungeondefence.scene.TextureUtil;

/**
 * @author simengangstad
 * @since 27/01/16
 */
public class Snake extends Enemy {

    private static final Animation BiteAnimation = TextureUtil.getAnimation(Game.SnakeBiting, 32, 0.025f, Animation.PlayMode.NORMAL);

    public static Sound hiss = Gdx.audio.newSound(Gdx.files.internal("sfx/snake-hiss.mp3"));
    public static Sound bite = Gdx.audio.newSound(Gdx.files.internal("sfx/snake-bite.wav"));

    private float timeToNextStep = 0.0f;
    private float timeToNextAttack = 0.0f;

    public int attackDamage = 30;

    public Snake(Vector2 position, Vector2 size, Player player) {

        super(position,
                player,
                20,
                size,
                TextureUtil.getAnimation(Game.SnakeStationary, 32, 0.4f, Animation.PlayMode.NORMAL),
                TextureUtil.getAnimation(Game.SnakeMoving, 32, 0.08f, Animation.PlayMode.NORMAL));
    }

    @Override
    public void create() {

        super.create();
    }

    @Override
    protected void hurtPlayer(final Vector2 tmpVector) {

        timeToNextAttack -= Gdx.graphics.getDeltaTime();

        if (timeToNextAttack <= 0) {

            if (Game.PlaySound) bite.play(0.25f);

            requestAnimation(BiteAnimation, new Callback() {

                @Override
                public void callback() {

                    ((Scene) host).damage(attackDamage, tmpVector, position.x + (!flip() ? size.x / 2.0f : -size.x / 2.0f), position.y, size.x, size.y, Snake.this);
                }
            });

            while (timeToNextAttack < 0) {

                timeToNextAttack += 0.5f;
            }
        }
    }

    @Override
    protected void noticedPlayer(Vector2 direction) {

        timeToNextStep -= Gdx.graphics.getDeltaTime();

        if (timeToNextStep < 0) {

            if (MathUtils.random(100) < 60) {

                if (Game.PlaySound) hiss.play(0.25f);
            }

            while (timeToNextStep < 0) {

                timeToNextStep += 5.0f;
            }
        }
    }

    @Override
    public void draw(SpriteBatch batch) {

        super.draw(batch);

        if (Game.Debug) {

            batch.draw(Game.debugDrawTexture, position.x + (!flip() ? 0 : -size.x), position.y - size.y / 2.0f, size.x, size.y);
        }
    }
}

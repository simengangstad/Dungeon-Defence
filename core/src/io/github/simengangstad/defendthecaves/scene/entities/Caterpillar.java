package io.github.simengangstad.defendthecaves.scene.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.scene.Entity;
import io.github.simengangstad.defendthecaves.scene.Projectile;
import io.github.simengangstad.defendthecaves.scene.TextureUtil;
import io.github.simengangstad.defendthecaves.scene.items.Axe;

/**
 * @author simengangstad
 * @since 30/01/16
 */
public class Caterpillar extends Enemy {

    private static final Animation AttackAnimation = TextureUtil.getAnimation(Game.CaterpillarAttacking, 16, 0.5f, Animation.PlayMode.NORMAL);

    public static final Animation projectingAnimation = TextureUtil.getAnimation(Game.SpriteSheet, 0, 304, 48, 16, 16, 0.1f, Animation.PlayMode.NORMAL);
    public static final Animation impactAnimation = TextureUtil.getAnimation(Game.SpriteSheet, 48, 304, 192, 16, 16, 0.1f, Animation.PlayMode.NORMAL);

    public int attackDamage = 30;

    public int projectileSpeed = 15;

    private final Vector2 tmp = new Vector2();

    private float timeToNextStep = 0.0f;

    public static final Sound Hiss = Gdx.audio.newSound(Gdx.files.internal("assets/sfx/hiss.wav")), Spitting = Gdx.audio.newSound(Gdx.files.internal("assets/sfx/spitting.wav"));

    public Caterpillar(Vector2 position, Vector2 size, Player player) {

        super(position,
                player,
                5,
                size,
                TextureUtil.getAnimation(Game.CaterpillarStationary, 16, 0.2f, Animation.PlayMode.NORMAL),
                TextureUtil.getAnimation(Game.CaterpillarMoving, 16, 0.2f, Animation.PlayMode.NORMAL));
    }

    @Override
    public void create() {

        super.create();

    }

    @Override
    public boolean flip() {

        return tmp.x > 0;
    }

    @Override
    protected void hurtPlayer(Vector2 tmpVector) {

    }

    boolean fire = false;

    @Override
    protected void noticedPlayer(Vector2 direction) {


        timeToNextStep -= Gdx.graphics.getDeltaTime();

        if (timeToNextStep < 0) {

            if (MathUtils.random(100) < 60) {

                Hiss.play(1.0f);
            }

            while (timeToNextStep < 0) {

                timeToNextStep += 5.0f;
            }
        }

        if (currentAnimation != AttackAnimation) {

            fire = true;

            tmp.set(direction).nor();

            tmp.scl(projectileSpeed);

            requestAnimation(AttackAnimation, () -> {

                Projectile projectile = new Projectile(position.cpy(), new Vector2(80, 80), tmp.cpy(), projectingAnimation, impactAnimation, host.getGameObjects(), this);

                projectile.map = map;
                projectile.tiledCollisionTests = true;
                projectile.computeTiledCollisionMaps(projectingAnimation.getKeyFrames(), 16, 16);

                projectile.canBePickedUp = false;

                projectile.impactCallback = (object) -> {

                    if (object != null) {

                        Entity entity = (Entity) object;

                        if (entity instanceof Enemy) {

                            return;
                        }

                        Axe.Hit.play();

                        entity.takeDamage(attackDamage);

                        entity.paralyse();

                        entity.applyForce(direction.nor(), false, 0.0f);

                        System.out.println("Damage applied to entity: " + entity + " - current health: " + entity.health);
                    }
                };

                projectile.finishedPlayingImpactAnimationCallback = () -> {

                    host.removeGameObject(projectile);
                };

                Spitting.play();

                host.addGameObject(projectile);
            });
        }
    }
}

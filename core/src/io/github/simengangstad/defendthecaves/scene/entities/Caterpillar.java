package io.github.simengangstad.defendthecaves.scene.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.sun.istack.internal.NotNull;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.components.GameObject;
import io.github.simengangstad.defendthecaves.scene.*;

import java.util.List;

/**
 * @author simengangstad
 * @since 30/01/16
 */
public class Caterpillar extends Enemy {

    private static final Animation AttackAnimation = TextureUtil.getAnimation(Game.CaterpillarAttacking, 16, 0.2f, Animation.PlayMode.NORMAL);

    private static final Animation projectingAnimation = TextureUtil.getAnimation(Game.SpriteSheet, 0, 304, 48, 16, 16, 0.1f, Animation.PlayMode.NORMAL);
    private static final Animation impactAnimation = TextureUtil.getAnimation(Game.SpriteSheet, 48, 304, 192, 16, 16, 0.1f, Animation.PlayMode.NORMAL);

    public int attackDamage = 30;

    public int projectileSpeed = 1000;

    private final Vector2 tmp = new Vector2();

    private List<GameObject> gameObjectList;

    public Caterpillar(Vector2 position, Player player, List<GameObject> gameObjectList) {

        super(position,
                player,
                6,
                new Vector2(80, 80),
                TextureUtil.getAnimation(Game.CaterpillarStationary, 16, 0.2f, Animation.PlayMode.NORMAL),
                TextureUtil.getAnimation(Game.CaterpillarMoving, 16, 0.2f, Animation.PlayMode.NORMAL));

        this.gameObjectList = gameObjectList;
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
    @NotNull
    protected void noticedPlayer(Vector2 direction) {

        if (currentAnimation != AttackAnimation) {

            fire = true;

            tmp.set(direction).nor();

            tmp.scl(projectileSpeed);

            requestAnimation(AttackAnimation, () -> {

                Projectile projectile = new Projectile(getPosition().cpy(), new Vector2(80, 80), tmp.cpy(), projectingAnimation, impactAnimation, map, gameObjectList, this);

                projectile.impactCallback = (object) -> {

                    if (object != null) {

                        Entity entity = (Entity) object;

                        if (entity instanceof Enemy) {

                            return;
                        }

                        entity.takeDamage(attackDamage);

                        entity.paralyse();

                        entity.applyForce(direction.nor().scl(7.5f));

                        System.out.println("Damage applied to entity: " + entity + " - current health: " + entity.health);
                    }
                };

                projectile.finishedPlayingImpactAnimationCallback = () -> {

                    host.removeGameObject(projectile);
                };

                host.addGameObject(projectile);
            });
        }
    }
}

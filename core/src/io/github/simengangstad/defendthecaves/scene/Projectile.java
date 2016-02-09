package io.github.simengangstad.defendthecaves.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.defendthecaves.Callback;
import io.github.simengangstad.defendthecaves.components.Drawable;
import io.github.simengangstad.defendthecaves.components.GameObject;
import io.github.simengangstad.defendthecaves.scene.entities.Player;
import io.github.simengangstad.defendthecaves.scene.tool.Shield;

import java.util.List;

/**
 * @author simengangstad
 * @since 30/01/16
 */
public class Projectile extends GameObject implements Drawable {

    private final Animation projectingAnimation, impactAnimation;

    private TextureRegion currentTextureRegion;

    private final Vector2 velocity, tmp = new Vector2(), tmpCollisionPoint = new Vector2();

    private final Map map;

    private final List<GameObject> gameObjectList;

    private boolean colliding = false;

    private float stateTime = 0;

    public ImpactCallback impactCallback;

    public Callback finishedPlayingImpactAnimationCallback;

    private final Class avoidable;

    private final boolean flip;

    public Projectile(Vector2 position, Vector2 size, Vector2 velocity, Animation projectingAnimation, Animation impactAnimation, Map map, List<GameObject> gameObjectList, Class avoidable) {

        super(position, size);

        this.velocity = velocity;

        this.projectingAnimation = projectingAnimation;
        this.impactAnimation = impactAnimation;

        this.map = map;

        this.gameObjectList = gameObjectList;

        currentTextureRegion = projectingAnimation.getKeyFrame(stateTime, true);

        this.avoidable = avoidable;

        flip = velocity.x > 0;
    }

    @Override
    public TextureRegion getTextureRegion() {

        return currentTextureRegion;
    }

    private void checkCollision(Vector2 direction) {

        tmp.set(direction).nor();

        if (velocity.x != 0.0f || velocity.y != 0.0f) {

            // Check the collision of the projectile against the map
            if (map.retrieveCollisionPoint(getPosition(), tmp, 1000, tmpCollisionPoint)) {

                if (!colliding) {

                    colliding = true;

                    stateTime = 0.0f;

                    velocity.set(0.0f, 0.0f);
                }

                if (impactCallback != null) impactCallback.callback(null);
            }
            else {

                for (GameObject gameObject : gameObjectList) {

                    if ((gameObject instanceof Entity) & !(gameObject.getClass().getSuperclass().equals(avoidable))) {

                        if (gameObject.intersects(this)) {

                            if (!colliding) {

                                colliding = true;

                                stateTime = 0.0f;

                                velocity.set(0.0f, 0.0f);
                            }

                            if (gameObject instanceof Player && ((Player) gameObject).currentTool instanceof Shield && ((Player) gameObject).currentTool.flip() != flip) {

                                return;
                            }

                            if (impactCallback != null) impactCallback.callback(gameObject);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean flip() {

        return flip;
    }

    @Override
    public void tick() {

        stateTime += Gdx.graphics.getDeltaTime();

        checkCollision(velocity);

        if (!colliding) {

            currentTextureRegion = projectingAnimation.getKeyFrame(stateTime, true);
        }
        else {

            currentTextureRegion = impactAnimation.getKeyFrame(stateTime, false);

            if (stateTime > impactAnimation.getAnimationDuration()) {

                if (finishedPlayingImpactAnimationCallback != null) finishedPlayingImpactAnimationCallback.callback();
            }
        }
    }

    @Override
    public void dispose() {

    }
}

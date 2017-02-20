package io.github.simengangstad.dungeondefence.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.dungeondefence.Callback;
import io.github.simengangstad.dungeondefence.Game;
import io.github.simengangstad.dungeondefence.GameObject;
import io.github.simengangstad.dungeondefence.scene.entities.Player;
import io.github.simengangstad.dungeondefence.scene.items.Shield;

import java.util.List;

/**
 * @author simengangstad
 * @since 30/01/16
 */
public class Projectile extends Item {

    public Animation projectingAnimation, impactAnimation;

    private TextureRegion currentTextureRegion;

    public final Vector2 velocity = new Vector2();

    private Vector2 tmp = new Vector2();

    public List<GameObject> gameObjectList;

    private boolean colliding = false;

    private float stateTime = 0;

    public ImpactCallback impactCallback;

    public boolean calledImpactCallback = false;

    public Callback finishedPlayingImpactAnimationCallback;

    public GameObject avoidable;

    public boolean flip;

    private final int pixelWidth, pixelHeight;

    public Projectile(Vector2 position, Vector2 size, Vector2 velocity, Animation projectingAnimation, Animation impactAnimation, List<GameObject> gameObjectList, GameObject avoidable) {

        super(position, size, (TextureRegion) projectingAnimation.getKeyFrame(0.0f), true);

        this.velocity.set(velocity);

        this.projectingAnimation = projectingAnimation;
        this.impactAnimation = impactAnimation;

        this.gameObjectList = gameObjectList;

        currentTextureRegion = (TextureRegion) projectingAnimation.getKeyFrame(stateTime, true);

        this.avoidable = avoidable;

        flip = velocity.x > 0;

        pixelWidth = ((TextureRegion) projectingAnimation.getKeyFrame(0.0f)).getRegionWidth();
        pixelHeight = ((TextureRegion) projectingAnimation.getKeyFrame(0.0f)).getRegionHeight();
    }

    @Override
    public void interact(Vector2 direction) {
    }

    @Override
    public TextureRegion getSlotTextureRegion() {

        return getTextureRegion();
    }

    @Override
    public void create() {

        colliding = false;
        stateTime = 0.0f;
    }

    @Override
    public TextureRegion getTextureRegion() {

        return currentTextureRegion;
    }

    private boolean check(int x, int y, Vector2 tilePosition, Vector2 tileSize, boolean flip) {

        if (bakedTiledCollisionMaps[bakedTiledCollisionMapIndex][x][y]) {

            tilePosition.set(position.x - size.x / 2.0f + (flip ? size.x - tileSize.x * (x + 1) : tileSize.x * x), position.y + size.y / 2.0f - tileSize.y * (y + 1) + (Game.Debug ? 20 : 0));

            if (Game.Debug) host.batch.draw(Game.debugDrawTexture, tilePosition.x, tilePosition.y, tileSize.x, tileSize.y);

            if (map.collides(tilePosition, tileSize, tmp)) {

                if (!colliding) {

                    colliding = true;

                    stateTime = 0.0f;

                    velocity.set(0.0f, 0.0f);
                }

                if (impactCallback != null && !calledImpactCallback) {

                    impactCallback.callback(null);

                    calledImpactCallback = true;
                }

                return true;
            }
        }

        return false;
    }

    private void checkCollision(Vector2 direction) {

        tmp.set(direction);

        if (velocity.x != 0.0f || velocity.y != 0.0f) {

            if (tiledCollisionTests) {

                // Only collision test, not response

                Vector2 tileSize = Game.vector2Pool.obtain();
                Vector2 tilePostion = Game.vector2Pool.obtain();

                tileSize.set(size.x / pixelWidth, size.y / pixelHeight);

                if (!flip) {

                    for (int x = 0; x < bakedTiledCollisionMaps[bakedTiledCollisionMapIndex].length; x++) {

                        for (int y = 0; y < bakedTiledCollisionMaps[bakedTiledCollisionMapIndex][0].length; y++) {

                            if (check(x, y, tilePostion, tileSize, false)) {

                                break;
                            }
                        }
                    }
                }
                else {

                    for (int x = bakedTiledCollisionMaps[bakedTiledCollisionMapIndex].length - 1; x >= 0; x--) {

                        for (int y = bakedTiledCollisionMaps[bakedTiledCollisionMapIndex][0].length - 1; y >= 0; y--) {

                            if (check(x, y, tilePostion, tileSize, true)) {

                                break;
                            }
                        }
                    }
                }


                Game.vector2Pool.free(tileSize);
                Game.vector2Pool.free(tilePostion);

                if (!colliding) {

                    position.add(direction);
                }
            }
            else {

                // Check the collision of the projectile against the map
                if (map.retrieveCollisionPoint(position, tmp, direction.len(), null)) {

                    if (!colliding) {

                        colliding = true;

                        stateTime = 0.0f;

                        velocity.set(0.0f, 0.0f);
                    }


                    if (impactCallback != null && !calledImpactCallback) {

                        impactCallback.callback(null);

                        calledImpactCallback = true;
                    }
                }
            }

            if (!colliding) {

                for (GameObject gameObject : gameObjectList) {

                    if ((gameObject instanceof Entity) && gameObject != avoidable) {

                        if (((Collidable) gameObject).intersects(this, true)) {

                            if (!colliding) {

                                colliding = true;

                                stateTime = 0.0f;

                                velocity.set(0.0f, 0.0f);
                            }

                            if (gameObject instanceof Player && ((Player) gameObject).currentItem instanceof Shield && ((Player) gameObject).currentItem.flip() != flip) {

                                return;
                            }


                            if (impactCallback != null && !calledImpactCallback) {

                                impactCallback.callback(gameObject);

                                calledImpactCallback = true;
                            }
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

        if (timing) {

            timer += Gdx.graphics.getDeltaTime();
        }

        stateTime += Gdx.graphics.getDeltaTime() / 5;

        if (!colliding) super.bakedTiledCollisionMapIndex = projectingAnimation.getKeyFrameIndex(stateTime % projectingAnimation.getAnimationDuration());

        checkCollision(velocity);

        if (!colliding) {

            currentTextureRegion = (TextureRegion) projectingAnimation.getKeyFrame(stateTime, true);
        }
        else {

            if (impactAnimation != null) {

                currentTextureRegion = (TextureRegion) impactAnimation.getKeyFrame(stateTime, false);

                if (stateTime > impactAnimation.getAnimationDuration()) {

                    if (finishedPlayingImpactAnimationCallback != null) finishedPlayingImpactAnimationCallback.callback();
                }
            }
            else {

                currentTextureRegion = (TextureRegion) projectingAnimation.getKeyFrame(0.0f);
            }
        }
    }

    @Override
    public void dispose() {

    }
}

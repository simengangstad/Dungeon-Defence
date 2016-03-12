package io.github.simengangstad.defendthecaves.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.simengangstad.defendthecaves.Callback;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.GameObject;

/**
 * @author simengangstad
 * @since 12/03/16
 */
public class Explosion extends GameObject {

    private static final Animation animation = TextureUtil.getAnimation(Game.SpriteSheet, 0, 432, 32 * 9, 32, 32, 0.1f, Animation.PlayMode.NORMAL);

    public float intensity;

    public float radius;

    private boolean processedStarted = false;

    private float stateTime = 0.0f;

    private Callback explosionCallback;

    private boolean firedCallback = false;

    public Explosion(float radius, float intensity, Callback explosionCallback) {

        this.intensity = intensity;
        this.radius = radius;
        this.explosionCallback = explosionCallback;
    }

    public void start() {

        processedStarted = true;
    }

    public void reset() {

        stateTime = 0.0f;
        processedStarted = false;
        firedCallback = false;
    }

    @Override
    public void create() {}

    @Override
    public void tick() {

        if (processedStarted) {

            stateTime += Gdx.graphics.getDeltaTime();

            if (animation.getAnimationDuration() < stateTime) {

                processedStarted = false;
            }

            if (animation.getKeyFrameIndex(stateTime) == 1 && !firedCallback) {

                explosionCallback.callback();

                firedCallback = true;
            }
        }

        size.set(radius * 2, radius * 2);
    }

    @Override
    public boolean flip() {

        return false;
    }

    @Override
    public TextureRegion getTextureRegion() {

        return animation.getKeyFrame(stateTime, false);
    }

    @Override
    public void dispose() {}
}

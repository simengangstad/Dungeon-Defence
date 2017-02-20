package io.github.simengangstad.dungeondefence.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.simengangstad.dungeondefence.Callback;
import io.github.simengangstad.dungeondefence.Game;
import io.github.simengangstad.dungeondefence.GameObject;

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

    public static final Sound ExplosionSound = Gdx.audio.newSound(Gdx.files.internal("sfx/explosion.wav"));

    public Explosion(float radius, float intensity) {

        this.intensity = intensity;
        this.radius = radius;
    }

    public void setExplosionCallback(Callback explosionCallback) {

        this.explosionCallback = explosionCallback;
    }

    public void start() {

        reset();

        if (Game.PlaySound) ExplosionSound.play();

        processedStarted = true;

        ((Scene) host).scheduleScreenShake(1.0f, 60);
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

            if (animation.getKeyFrameIndex(stateTime) == 1 && !firedCallback && explosionCallback != null) {

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

        return (TextureRegion) animation.getKeyFrame(stateTime, false);
    }

    @Override
    public void dispose() {}
}

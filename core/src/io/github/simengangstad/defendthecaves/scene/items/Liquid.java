package io.github.simengangstad.defendthecaves.scene.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.GameObject;
import io.github.simengangstad.defendthecaves.scene.TextureUtil;

/**
 * @author simengangstad
 * @since 08/03/16
 */
public class Liquid extends GameObject {

    public static final float FrameDuration = 0.3f;

    public static final Animation animationFrame   = TextureUtil.getAnimation(Game.SpriteSheet, 0, 352, 32 * 9, 32, 32, FrameDuration, Animation.PlayMode.NORMAL);
    public static final Animation animationContent = TextureUtil.getAnimation(Game.SpriteSheet, 0, 384, 32 * 9, 32, 32, FrameDuration, Animation.PlayMode.NORMAL);

    private float stateTime = 0.0f;

    private final Potion potion;

    public Liquid(Vector2 position, Potion potion) {

        this.position = position;
        this.potion = potion;

        this.size = new Vector2(Game.EntitySize * 2, Game.EntitySize * 2);
    }

    @Override
    public void create() {}

    @Override
    public void tick() {

        if (stateTime < animationFrame.getAnimationDuration()) {

            stateTime += Gdx.graphics.getDeltaTime();
        }
    }

    @Override
    public boolean flip() {

        return false;
    }

    public float getToxicity() {

        return potion.getToxicity();
    }

    @Override
    public void draw(SpriteBatch batch) {

        batch.setColor(Math.abs(potion.getToxicity() + Chemical.UpperBoundary - 100) / 100f, Math.abs(potion.getStability() + Chemical.UpperBoundary - 100) / 100f, (potion.getFlammability() + Chemical.UpperBoundary) / 100f, 1.0f);

        batch.draw(animationContent.getKeyFrame(stateTime), position.x - size.x / 2.0f, position.y - size.y / 2.0f, size.x, size.y);

        batch.setColor(Color.WHITE);

        batch.draw(animationFrame.getKeyFrame(stateTime), position.x - size.x / 2.0f, position.y - size.y / 2.0f, size.x, size.y);
    }

    @Override
    public TextureRegion getTextureRegion() {

        return null;
    }

    @Override
    public void dispose() {}
}

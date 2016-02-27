package io.github.simengangstad.defendthecaves.scene.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.scene.Item;
import io.github.simengangstad.defendthecaves.scene.TextureUtil;

/**
 * @author simengangstad
 * @since 20/02/16
 */
public class Torch extends Item {

    private final static Animation animation = TextureUtil.getAnimation(Game.Torch, 16, 0.4f, Animation.PlayMode.LOOP);

    private float animationTime = 0.0f;

    private static final Vector2 tmpVector = new Vector2();

    public Torch(Vector2 position) {

        super(position, new Vector2(Game.EntitySize, Game.EntitySize), animation.getKeyFrame(0.0f, true), true);

        information = "Torch";
    }

    @Override
    public void interact(Vector2 direciton) {

    }

    @Override
    public void tick() {

        super.tick();

        animationTime += Gdx.graphics.getDeltaTime();

        textureRegion = animation.getKeyFrame(animationTime, true);
    }

    @Override
    public boolean flip() {

        return !super.flip();
    }

    @Override
    public void draw(SpriteBatch batch) {

        tmpVector.set(position);

        float xOffset;

        if (flip()) {

            xOffset = -(size.x / Game.SizeOfTileInPixelsInSpritesheet) * 6;
        }
        else {

            xOffset = (size.x / Game.SizeOfTileInPixelsInSpritesheet) * 6;
        }

        position.set(position.x + xOffset, position.y + walkingOffset + (size.y / Game.SizeOfTileInPixelsInSpritesheet) * 2);

        super.draw(batch);

        position.set(tmpVector);
    }
}
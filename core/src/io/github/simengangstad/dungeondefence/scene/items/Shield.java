package io.github.simengangstad.dungeondefence.scene.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.dungeondefence.Game;
import io.github.simengangstad.dungeondefence.scene.Item;
import io.github.simengangstad.dungeondefence.scene.Map;
import io.github.simengangstad.dungeondefence.scene.Scene;
import io.github.simengangstad.dungeondefence.scene.TextureUtil;

/**
 * @author simengangstad
 * @since 24/12/15
 */
public class Shield extends Item {

    private static Animation animation = TextureUtil.getAnimation(Game.SpriteSheet, 0, 128, 288, 32, 32, 0.05f, Animation.PlayMode.NORMAL);

    private TextureRegion currentRegion = (TextureRegion) animation.getKeyFrame(0.0f);

    private final Vector2 tmpPosition = new Vector2(), tmpSize = new Vector2();

    private float blockTimer = 0.0f;

    private boolean blocking = false;

    public Shield() {

        super(new Vector2(), new Vector2(Game.EntitySize, Game.EntitySize), (TextureRegion) animation.getKeyFrames()[0], false);

        information = "Arr, yeah, this'll protect me";

        for (Object textureRegion : animation.getKeyFrames()) {

            ((TextureRegion) textureRegion).flip(true, false);
        }
    }

    @Override
    public void interact(Vector2 direction) {

        ((Scene ) parent.host).pushEntities(parent, position, Map.TileSizeInPixelsInWorldSpace, direction, 3.0f);

        blocking = true;

        blockTimer = 0.0f;

        if (Game.PlaySound) Axe.Swing.play(0.5f);
    }

    @Override
    public void draw(SpriteBatch batch) {

        tmpPosition.set(position);
        tmpSize.set(size);

        float xDelta;
        float yDelta = -size.y / 2.0f - (size.y / 16) * 2;

        if (flip()) {

            xDelta = -size.x / 2.0f + (size.x / 16) * 4;
        }
        else {

            xDelta = -size.x / 2.0f - (size.x / 16) * 4;
        }

        if (blocking) {

            blockTimer += Gdx.graphics.getDeltaTime();

            if (blockTimer >= animation.getAnimationDuration()) {

                blocking = false;

                blockTimer = 0.0f;
            }
        }

        currentRegion = (TextureRegion) animation.getKeyFrame(blockTimer, true);

        if (flip() && !currentRegion.isFlipX()) {

            currentRegion.flip(true, false);
        }

        size.set(size.x * 2, size.y * 2);
        position.set(position.x + xDelta + size.x / 2.0f, position.y + yDelta + walkingOffset + size.y / 2.0f);

        super.draw(batch);

        position.set(tmpPosition);
        size.set(tmpSize);
    }

    @Override
    public TextureRegion getTextureRegion() {

        return currentRegion;
    }

    @Override
    public TextureRegion getSlotTextureRegion() {

        return (TextureRegion) animation.getKeyFrame(0.0f);
    }
}

package io.github.simengangstad.defendthecaves.scene.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.scene.Item;
import io.github.simengangstad.defendthecaves.scene.Map;
import io.github.simengangstad.defendthecaves.scene.TextureUtil;
import io.github.simengangstad.defendthecaves.scene.entities.Player;

/**
 * @author simengangstad
 * @since 13/03/16
 */
public class StepTrap extends Item {

    public static final Animation animation = TextureUtil.getAnimation(Game.SpriteSheet, 0, 464, 16 * 2, 16, 16, 0.40f, Animation.PlayMode.NORMAL);

    public Potion potion;

    private float stateTime = 0.0f;

    private boolean stepped = false;

    private boolean triggered = false;

    public StepTrap(Vector2 position) {

        super(position, new Vector2(Game.ItemSize, Game.ItemSize), animation.getKeyFrame(0.0f), true);
    }

    public void step() {

        stepped = true;
    }

    @Override
    public void tick() {

        super.tick();

        if (stepped) {

            stateTime += Gdx.graphics.getDeltaTime();
        }

        if (!triggered && animation.getKeyFrameIndex(stateTime) == 1 && stateTime > animation.getAnimationDuration()) {

            potion.position = position.cpy();

            potion.breakPotion();

            triggered = true;

            host.removeGameObject(this);
        }
    }

    @Override
    public void interact(Vector2 direciton) {

        Vector3 vec = Game.vector3Pool.obtain();

        vec.set(Gdx.input.getX(), Gdx.input.getY(), 0.0f);
        vec.set(((Player) parent).camera.unproject(vec));

        if (vec.len() - position.len() < Map.TileSizeInPixelsInWorldSpace * 1.5f) {

            flip = !parent.flip();

            // Clamp to tiles
            position.set(((int) (vec.x / Map.TileSizeInPixelsInWorldSpace)) * Map.TileSizeInPixelsInWorldSpace + Map.TileSizeInPixelsInWorldSpace / 2.0f, ((int) (vec.y / Map.TileSizeInPixelsInWorldSpace)) * Map.TileSizeInPixelsInWorldSpace + Map.TileSizeInPixelsInWorldSpace / 2.0f);
            size.set(Map.TileSizeInPixelsInWorldSpace, Map.TileSizeInPixelsInWorldSpace);

            parent.removeItem(this);

            host.addGameObject(this);

            placed = true;
        }

        Game.vector3Pool.free(vec);
    }

    @Override
    public TextureRegion getTextureRegion() {

        return animation.getKeyFrame(stateTime);
    }
}

package io.github.simengangstad.dungeondefence.scene.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import io.github.simengangstad.dungeondefence.Game;
import io.github.simengangstad.dungeondefence.scene.Item;
import io.github.simengangstad.dungeondefence.scene.Map;
import io.github.simengangstad.dungeondefence.scene.TextureUtil;
import io.github.simengangstad.dungeondefence.scene.entities.Player;

/**
 * @author simengangstad
 * @since 13/03/16
 */
public class StepTrap extends Item {

    public static final String Information = "Booby trap - surprise, surprise!\nDamage: lethal";
    public static final String CraftInformation = "Booby trap - surprise, surprise! Made from:\n3 wood\n1 explosive potion\nDamage: lethal";

    public static final Animation animation = TextureUtil.getAnimation(Game.SpriteSheet, 0, 464, 16 * 2, 16, 16, 0.40f, Animation.PlayMode.NORMAL);

    private Potion potion;

    private float stateTime = 0.0f;

    private boolean stepped = false;

    private boolean triggered = false;

    public StepTrap(Vector2 position) {

        super(position, new Vector2(Game.ItemSize, Game.ItemSize), (TextureRegion) animation.getKeyFrame(0.0f), true);

        information = Information;
        craftInformation = CraftInformation;

        potion = new ExplosivePotion(this.position.cpy());
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
            potion.host = this.host;
            potion.map = this.map;

            potion.breakPotion();

            triggered = true;

            host.removeGameObject(this);
        }
    }

    @Override
    public void interact(Vector2 direction) {

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

        return (TextureRegion) animation.getKeyFrame(stateTime);
    }

    @Override
    public TextureRegion getSlotTextureRegion() {

        return (TextureRegion) animation.getKeyFrame(0.0f);
    }
}

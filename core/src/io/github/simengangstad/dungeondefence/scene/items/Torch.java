package io.github.simengangstad.dungeondefence.scene.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import io.github.simengangstad.dungeondefence.Game;
import io.github.simengangstad.dungeondefence.scene.*;
import io.github.simengangstad.dungeondefence.scene.entities.Player;

/**
 * @author simengangstad
 * @since 20/02/16
 */
public class Torch extends Item {

    public static final String Information = "Torch";
    public static final String CraftInformation = "Torch. Made from:\n1 wood\n1 coal";

    public static final Vector3 DefaultColour = new Vector3(0.9f, 0.55f, 0.19f);

    public final Light light;

    public final static Animation animation = TextureUtil.getAnimation(Game.Torch, 16, 0.4f, Animation.PlayMode.LOOP);

    private float animationTime = 0.0f;

    private static final Vector2 tmpVector = new Vector2();

    private boolean addedToScene = false;

    public Torch(Vector2 position) {

        super(position, new Vector2(Game.EntitySize, Game.EntitySize), (TextureRegion) animation.getKeyFrame(0.0f, true), true);

        light = new Light(position, DefaultColour, 60);

        information = Information;
        craftInformation = CraftInformation;
    }

    @Override
    public void interact(Vector2 direction) {

        Vector3 vec = Game.vector3Pool.obtain();

        vec.set(Gdx.input.getX(), Gdx.input.getY(), 0.0f);
        vec.set(((Player) parent).camera.unproject(vec));

        if (vec.len() - position.len() < Map.TileSizeInPixelsInWorldSpace * 1.5f) {

            if (!map.isSolid((int) (vec.x / Map.TileSizeInPixelsInWorldSpace), (int) (vec.y / Map.TileSizeInPixelsInWorldSpace))) {

                overwriteFlip = true;
                flip = !parent.flip();

                position.set(vec.x, vec.y);

                parent.removeItem(this);

                host.addGameObject(this);

                placed = true;
            }
        }

        Game.vector3Pool.free(vec);
    }

    @Override
    public void tick() {

        super.tick();

        if (!addedToScene) {

            ((Scene) host).addLight(light);

            addedToScene = true;
        }

        animationTime += Gdx.graphics.getDeltaTime();

        textureRegion = (TextureRegion) animation.getKeyFrame(animationTime, true);
    }

    @Override
    public boolean flip() {

        return !super.flip();
    }

    @Override
    public void draw(SpriteBatch batch) {

        if (!isPlaced() && !isThrown()) {

            if (parent != null) position.set(parent.position);
        }

        tmpVector.set(position);

        light.position.set(position);

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

    @Override
    public TextureRegion getSlotTextureRegion() {

        return (TextureRegion) animation.getKeyFrame(0.0f);
    }
}
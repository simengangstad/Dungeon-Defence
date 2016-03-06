package io.github.simengangstad.defendthecaves.scene.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.scene.*;
import io.github.simengangstad.defendthecaves.scene.entities.Player;

/**
 * @author simengangstad
 * @since 20/02/16
 */
public class Torch extends Item {

    public static final Vector3 DefaultColour = new Vector3(0.9f, 0.55f, 0.19f);

    public final Light light;

    private final static Animation animation = TextureUtil.getAnimation(Game.Torch, 16, 0.4f, Animation.PlayMode.LOOP);

    private float animationTime = 0.0f;

    private static final Vector2 tmpVector = new Vector2();

    private boolean addedToScene = false;

    public Torch(Vector2 position) {

        super(position, new Vector2(Game.EntitySize, Game.EntitySize), animation.getKeyFrame(0.0f, true), true);

        light = new Light(position, DefaultColour, 30);

        information = "Torch";
    }

    @Override
    public void interact(Vector2 direciton) {

        Vector3 vec = Game.vector3Pool.obtain();

        vec.set(Gdx.input.getX(), Gdx.input.getY(), 0.0f);
        vec.set(((Player) parent).camera.unproject(vec));

        if (vec.len() - position.len() < Map.TileSizeInPixelsInWorldSpace * 1.5f) {

            overwriteFlip = true;
            flip = !parent.flip();

            position.set(vec.x, vec.y);

            parent.removeItem(this);

            host.addGameObject(this);

            placed = true;
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

        textureRegion = animation.getKeyFrame(animationTime, true);
    }

    @Override
    public boolean flip() {

        return !super.flip();
    }

    @Override
    public void draw(SpriteBatch batch) {

        if (!isPlaced()) {

            position.set(parent.position);
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
}
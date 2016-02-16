package io.github.simengangstad.defendthecaves.scene.item;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.scene.Item;

/**
 * @author simengangstad
 * @since 14/02/16
 */
public class Rock extends Item {

    private static TextureRegion[] textureRegions = new TextureRegion[] {

            new TextureRegion(Game.SpriteSheet, 384, 0, 16, 16),
            new TextureRegion(Game.SpriteSheet, 400, 0, 16, 16),
            new TextureRegion(Game.SpriteSheet, 416, 0, 16, 16),
            new TextureRegion(Game.SpriteSheet, 432, 0, 16, 16),
    };

    final float timeToFall = 3.0f;

    float timeLeft = 0.0f;

    public Rock(Vector2 position) {

        super(position, new Vector2(), textureRegions[MathUtils.random(textureRegions.length - 1)], true);

        this.size.set(80.0f, 80.0f);
    }

    @Override
    public void create() {

        applyForce(new Vector2(MathUtils.random(-6, 6), 5.0f), true, 1.0f);

        timeLeft = timeToFall;
    }

    @Override
    public void interact(Vector2 direciton) {

    }


    @Override
    public void tick() {

        super.tick();

        if (0.0f < timeLeft) {

            timeLeft -= Gdx.graphics.getDeltaTime();
        }
        else {

            //forceApplied.set(0.0f, 0.0f);
        }
    }
}

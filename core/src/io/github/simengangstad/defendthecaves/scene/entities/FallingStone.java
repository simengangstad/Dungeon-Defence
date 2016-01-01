package io.github.simengangstad.defendthecaves.scene.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.components.Drawable;
import io.github.simengangstad.defendthecaves.components.GameObject;

/**
 * @author simengangstad
 * @since 12/12/15
 */
public class FallingStone extends GameObject implements Drawable {

    private static TextureRegion[] textureRegions = new TextureRegion[] {

            new TextureRegion(Game.spriteSheet, 160, 80, 16, 16),
            new TextureRegion(Game.spriteSheet, 176, 80, 16, 16)
    };

    private TextureRegion textureRegion;

    private float acceleration = 30.0f;

    private Vector2 dstPosition, direction, tmp = new Vector2();

    private float passedTime = 0.0f;

    public FallingStone(Vector2 position, Vector2 dstPosition, Vector2 size) {

        super(position, size);

        textureRegion = textureRegions[MathUtils.random(textureRegions.length - 1)];

        this.dstPosition = dstPosition;
        direction = new Vector2(dstPosition.x - position.x, dstPosition.y - dstPosition.y).nor();
    }

    @Override
    public void dispose() {}

    @Override
    public TextureRegion getTextureRegion() {

        return textureRegion;
    }

    @Override
    public boolean flip() {

        return false;
    }

    int speed = 100;

    @Override
    public void tick() {

        tmp.set(dstPosition.x - getPosition().x, dstPosition.y - getPosition().y);

        if (0.1f < tmp.len()) {

            getPosition().set(getPosition().x + direction.x * Gdx.graphics.getDeltaTime() * speed, getPosition().y + direction.y * Gdx.graphics.getDeltaTime() * speed);
        }
    }
}

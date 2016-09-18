package io.github.simengangstad.defendthecaves.scene.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.GameObject;
import io.github.simengangstad.defendthecaves.scene.Entity;
import io.github.simengangstad.defendthecaves.scene.Explosion;
import io.github.simengangstad.defendthecaves.scene.Projectile;

import java.util.List;

/**
 * @author simengangstad
 * @since 09/04/16
 */
public class Arrow extends Projectile {

    public static final String
            StoneInformation = "Arrow with an arrow head of stone\nDamage: 70",
            ExplosiveInformation = "Arrow with an arrow head that explodes. Use wisely!\nDamage: lethal",
            ToxicInformation = "Arrow with an arrow head that will poison the enemy\nDamage: 100";

    public static final String
            StoneCraftInformation = "Arrow with an arrow head of stone (70 in damage). Made from:\n1 stone\n1 string\n1 wood\nDamage: 70",
            ExplosiveCraftInformation = "Arrow with an arrow head that explodes. Use wisely! Made from:\n1 stone\n1 explosive potion\n1 string\n1 wood\nDamage: lethal",
            ToxicCraftInformation = "Arrow with an arrow head that will poison the enemy (100 in damage). Made from:\n1 stone\n1 toxic potion\n1 string\n1 wood\nDamage: 100";


    public static TextureRegion explosiveArrow = new TextureRegion(Game.SpriteSheet, 240, 320, 32, 32);
    public static TextureRegion toxicArrow = new TextureRegion(Game.SpriteSheet, 272, 320, 32, 32);

    public static Animation[] projectingAnimations = new Animation[6];
    private static TextureRegion[] arrowHead = new TextureRegion[6];

    static {

        float frameDuration = 0.05f;

        TextureRegion[] textureRegions = new TextureRegion[6 * 3];

        int index = 0;

        for (int x = 304; x < 304 + 32 * textureRegions.length / 3; x += 32) {

            for (int y = 0; y < textureRegions.length / 6; y++) {

                textureRegions[index + y * textureRegions.length / 3] = new TextureRegion(Game.SpriteSheet, x, 400 + 32 * y, 32, 32);
            }

            index++;
        }

        Array<TextureRegion> regions = new Array<>();

        for (int i = 0; i < projectingAnimations.length; i++) {

            regions.clear();

            for (int j = 0; j < 3; j++) {

                regions.add(textureRegions[i + j * 6]);
            }

            projectingAnimations[i] = new Animation(frameDuration, regions);
        }

        index = 0;

        for (int x = 96; x < 96 + 32 * arrowHead.length; x += 32) {

            arrowHead[index++] = new TextureRegion(Game.SpriteSheet, x, 480, 32, 32);
        }
    }


    /**
     * The different types of arrows.
     *
     * 0 = stone
     * 1 = explosive
     * 2 = toxic
     */
    public final int type;

    public int rotationIndex = 0;

    /*private int timesFired = 0;

    private final static int TimesFiredLimit = 3;
*/
    public Arrow(Vector2 position, Vector2 velocity, int type, int rotationIndex, List<GameObject> gameObjects, GameObject avoidable) {

        super(position, new Vector2(Game.EntitySize * 2, Game.EntitySize * 2), velocity, projectingAnimations[rotationIndex], null, gameObjects, avoidable);

        tiledCollisionTests = true;
        computeTiledCollisionMaps(projectingAnimation.getKeyFrames(), 32, 32);

        this.type = type;
        this.rotationIndex = rotationIndex;

        switch (type) {

            // Stone
            case 0:

                information = StoneInformation;

                break;

            case 1:

                information = ExplosiveInformation;

                break;

            case 2:

                information = ToxicInformation;

                break;
        }

        impactCallback = (object) -> {

            if (object instanceof Entity) {

                Axe.Hit.play();

                ((Entity) object).takeDamage(70, 0.0f);

                if (type == 2) {

                    ((Entity) object).takeDamage(30, 0.0f);
                }
/*
                timesFired++;

                if (timesFired >= TimesFiredLimit) {

                    // Breaks
                    host.removeGameObject(this);
                }*/
            }

            if (type == 1) {

                ExplosivePotion explosivePotion = new ExplosivePotion(this.position.cpy());

                explosivePotion.map = this.map;
                explosivePotion.host = this.host;

                explosivePotion.breakPotion();
            }
        };
    }

    @Override
    public void draw(SpriteBatch batch) {

        Vector2 tmpPosition = Game.vector2Pool.obtain();

        tmpPosition.set(position);

        if (parent != null) {

            if (parent.currentItem == this) {

                if (parent.flip()) {

                    rotation = 20.0f;
                    position.x += 12.5f;
                }
                else {

                    rotation = -20.0f;
                    position.x -= 12.5f;
                }
            }
            else {

                rotation = 0.0f;
            }

            position.y -= 20.0f;
        }

        super.draw(batch);

        Color colour = null;

        switch (type) {

            case 0:

                colour = Color.WHITE;

                break;

            case 1:

                colour = Color.RED;

                break;

            case 2:

                colour = Color.GREEN;

                break;
        }


        batch.setColor(colour);

        if (flip()) {

            arrowHead[rotationIndex].flip(true, false);
        }

        batch.draw(arrowHead[rotationIndex], position.x - size.x / 2.0f, position.y - size.y / 2.0f + walkingOffset, 0.0f, 0.0f, size.x, size.y, 1.0f, 1.0f, rotation);

        if (flip()) {

            arrowHead[rotationIndex].flip(true, false);
        }

        batch.setColor(Color.WHITE);

        position.set(tmpPosition);

        Game.vector2Pool.free(tmpPosition);
    }

    @Override
    public void draw(Batch batch, float x, float y, float width, float height) {

        Color colour = null;

        switch (type) {

            case 0:

                colour = Color.WHITE;

                break;

            case 1:

                colour = Color.RED;

                break;

            case 2:

                colour = Color.GREEN;

                break;
        }

        boolean flipped = getSlotTextureRegion().isFlipX();

        if (flipped) {

            getSlotTextureRegion().flip(true, false);
        }

        batch.draw(getSlotTextureRegion(), x, y, width / 2.0f - 12.0f, height / 2.0f + 20.0f, width, height, 2, 2, 0.0f);

        if (flipped) {

            getSlotTextureRegion().flip(true, false);
        }

        batch.setColor(colour);

        if (flipped) {

            arrowHead[rotationIndex].flip(true, false);
        }

        batch.draw(arrowHead[rotationIndex], x, y, width / 2.0f - 12.0f, height / 2.0f + 20.0f, width, height, 2, 2, 0.0f);

        if (flipped) {

            arrowHead[rotationIndex].flip(true, false);
        }

        batch.setColor(Color.WHITE);

    }
}

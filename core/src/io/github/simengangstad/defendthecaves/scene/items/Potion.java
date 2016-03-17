package io.github.simengangstad.defendthecaves.scene.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.scene.*;

import java.util.ArrayList;

/**
 * @author simengangstad
 * @since 23/01/16
 */
public class Potion extends Item {

    private final TextureRegion fill = new TextureRegion(Game.SpriteSheet, 48, 208, 16, 16);

    private ArrayList<Chemical> chemicals = new ArrayList<>();

    private int stability = 0, toxicity = 0, flammability = 0;

    private boolean broken = false;

    public Potion(Vector2 position) {

        super(position, new Vector2(Game.ItemSize, Game.ItemSize), new TextureRegion(Game.SpriteSheet, 32, 208, 16, 16), true);
    }

    @Override
    public void interact(Vector2 direciton) {

        parent.drinkPotion(this);
    }

    @Override
    protected void collides(Entity entity) {

        super.collides(entity);

        if (toxicity > 0 && collided) {

            System.out.println(entity + " taking damage from toxicity.");

            entity.takeDamage(toxicity);
        }

        host.removeGameObject(this);

        breakPotion();
    }

    @Override
    protected void collides() {

        super.collides();

        breakPotion();
    }

    public void breakPotion() {

        if (broken) {

            return;
        }


        broken = true;

        System.out.println(this + " broke!");

        if (getStability() < -25.0f && getFlammability() > 25.0f) {

            Explosion explosion = new Explosion(getFlammability() * -getStability() / 5.0f, getFlammability() * 3.0f);

            explosion.position = position.cpy();

            ((Scene) host).addExplosion(explosion);
        }
        else {

            Liquid liquid = new Liquid(position.cpy(), this);

            host.addGameObject(liquid);
        }
    }

    public void addChemical(Chemical... chemicals) {

        for (Chemical chemical : chemicals) {

            stability       = clampValue(stability + chemical.stability);
            toxicity        = clampValue(toxicity + chemical.toxicity);
            flammability    = clampValue(flammability + chemical.flammability);

            this.chemicals.add(chemical);
        }

        information = "Potion bottle with funky liquid\nStability: " + stability + "\nToxicity: " + toxicity + "\nFlammability: " + flammability;
    }

    private int clampValue(int value) {

        return MathUtils.clamp(value, Chemical.LowerBoundary, Chemical.UpperBoundary);
    }

    public int getStability() {

        return stability;
    }

    public int getToxicity() {

        return toxicity;
    }

    public int getFlammability() {

        return flammability;
    }

    @Override
    public void draw(SpriteBatch batch, float centreX, float centreY, float width, float height, boolean flipAutomatically) {

        batch.setColor(Math.abs(getToxicity() + Chemical.UpperBoundary - 100) / 100f, Math.abs(getStability() + Chemical.UpperBoundary - 100) / 100f, (getFlammability() + Chemical.UpperBoundary) / 100f, 1.0f);

        batch.draw(fill, centreX, centreY, width, height);

        batch.setColor(Color.WHITE);

        super.draw(batch, centreX, centreY, width, height, flipAutomatically);
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
        }

        position.y -= 20.0f;

        batch.setColor(Math.abs(getToxicity() + Chemical.UpperBoundary - 100) / 100f, Math.abs(getStability() + Chemical.UpperBoundary - 100) / 100f, (getFlammability() + Chemical.UpperBoundary) / 100f, 1.0f);

        batch.draw(fill, position.x - size.x / 2.0f, position.y - size.y / 2.0f + walkingOffset, size.x / 2.0f, size.y / 2.0f, size.x, size.y, 1.0f, 1.0f, rotation);

        batch.setColor(Color.WHITE);

        super.draw(batch);

        position.set(tmpPosition);

        Game.vector2Pool.free(tmpPosition);
    }

    @Override
    public String toString() {

        return "Potion (stability: " + stability + ", toxicity: " + toxicity + ", flammability: " + flammability + ")";
    }
}

package io.github.simengangstad.defendthecaves.scene.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.scene.Item;

import java.util.ArrayList;

/**
 * @author simengangstad
 * @since 23/01/16
 */
public class Potion extends Item {

    private final TextureRegion fill = new TextureRegion(Game.SpriteSheet, 48, 208, 16, 16);

    private ArrayList<Chemical> chemicals = new ArrayList<>();

    private int stability = 0, toxicity = 0, flammability = 0;

    public Potion(Vector2 position) {

        super(position, new Vector2(Game.ItemSize, Game.ItemSize), new TextureRegion(Game.SpriteSheet, 32, 208, 16, 16), false);
    }

    @Override
    public void interact(Vector2 direciton) {

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

        batch.setColor(Math.abs(getToxicity() + Chemical.UpperBoundary - 100) / 100f, Math.abs(getStability() + Chemical.UpperBoundary - 100) / 100f, (getFlammability() + Chemical.UpperBoundary) / 100f, 1.0f);

        batch.draw(fill, position.x - size.x / 2.0f, position.y - size.y / 2.0f, size.x / 2.0f, size.y / 2.0f, size.x, size.y, 1.0f, 1.0f, rotation);

        batch.setColor(Color.WHITE);

        super.draw(batch);
    }

    @Override
    public String toString() {

        return "Potion (stability: " + stability + ", toxicity: " + toxicity + ", flammability: " + flammability + ")";
    }
}

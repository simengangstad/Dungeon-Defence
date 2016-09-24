package io.github.simengangstad.defendthecaves.scene.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
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
public class  Potion extends Item {

    private final TextureRegion fill = new TextureRegion(Game.SpriteSheet, 48, 208, 16, 16);

    private ArrayList<Chemical> chemicals = new ArrayList<Chemical>();

    private int stability = 0, toxicity = 0, flammability = 0;

    private boolean broken = false;

    public static final Sound Breaking = Gdx.audio.newSound(Gdx.files.internal("assets/sfx/breaking.ogg")), Drinking = Gdx.audio.newSound(Gdx.files.internal("assets/sfx/rpg/inventory/bottle.ogg")), Burp = Gdx.audio.newSound(Gdx.files.internal("assets/sfx/burp.wav"));

    public Potion(Vector2 position) {

        super(position, new Vector2(Game.ItemSize, Game.ItemSize), new TextureRegion(Game.SpriteSheet, 32, 208, 16, 16), true);

        //super.tiledCollisionTests = true;
        //super.computeTiledCollisionMaps(new TextureRegion[] {getTextureRegion()}, 16, 16);
    }

    @Override
    public void interact(Vector2 direction) {

        parent.drinkPotion(this);
    }

    @Override
    protected void collides(Entity entity) {

        super.collides(entity);

        if (toxicity > 0 && collided) {

            System.out.println(entity + " taking damage from toxicity.");

            entity.takeDamage(toxicity);
        }

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

        Breaking.play();

        broken = true;

        System.out.println(this + " broke!");

        if (getStability() < -25.0f && getFlammability() > 25.0f) {

            Explosion explosion = new Explosion(getFlammability() * -getStability() / 5.0f, getFlammability() * 2.0f);

            explosion.host = this.host;
            explosion.position = position.cpy();

            ((Scene) host).addExplosion(explosion, this);
        }
        else {

            Liquid liquid = new Liquid(position.cpy(), this);

            host.addGameObject(liquid);
        }

        host.removeGameObject(this);
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
    public void draw(Batch batch, float x, float y, float width, float height) {

        batch.setColor(Math.abs(getToxicity() + Chemical.UpperBoundary - 100) / 100f, Math.abs(getStability() + Chemical.UpperBoundary - 100) / 100f, (getFlammability() + Chemical.UpperBoundary) / 100f, 1.0f);

        batch.draw(fill, x, y, width, height);

        batch.setColor(Color.WHITE);

        boolean flipped = getSlotTextureRegion().isFlipX();

        if (flipped) {

            getSlotTextureRegion().flip(true, false);
        }

        batch.draw(getSlotTextureRegion(), x, y, width, height);

        if (flipped) {

            getSlotTextureRegion().flip(true, false);
        }
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
    public TextureRegion getSlotTextureRegion() {

        return super.getTextureRegion();
    }

    @Override
    public String toString() {

        return "Potion (stability: " + stability + ", toxicity: " + toxicity + ", flammability: " + flammability + ")";
    }
}

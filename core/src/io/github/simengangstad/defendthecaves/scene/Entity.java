package io.github.simengangstad.defendthecaves.scene;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.components.Drawable;
import io.github.simengangstad.defendthecaves.components.GameObject;

import java.util.ArrayList;

/**
 * @author simengangstad
 * @since 28/11/15
 */
public abstract class Entity extends GameObject implements Drawable {

    private final ArrayList<Weapon> weapons = new ArrayList<>();

    public int health = 100;

    public Entity(Vector2 position, Vector2 size) {

        super(position, size);
    }

    public void attachWeapon(Weapon weapon) {

        weapons.add(weapon);

        weapon.parent = this;
    }

    public void detachWeapon(Weapon weapon) {

        weapons.remove(weapon);

        weapon.parent = null;
    }

    @Override
    public void tick() {

        for (Weapon weapon : weapons) {

            weapon.tick();
        }
    }

    @Override
    public void draw(SpriteBatch batch, Vector2 position, Vector2 size) {

        if (flip()) {

            getTextureRegion().flip(true, false);
        }

        if (Game.DebubDraw) {

            batch.draw(Game.debugDrawTexture, position.x, position.y, size.x, size.y);
        }

        // Shadow under the entity
        batch.draw(getShadowTextureRegion(), position.x, position.y - (size.y / 16), size.x, size.y);

        batch.draw(getTextureRegion(), position.x, position.y, size.x, size.y);


        if (flip()) {

            getTextureRegion().flip(true, false);
        }

        for (Weapon weapon : weapons) {

            weapon.draw(batch, position, size);
        }
    }

    protected abstract TextureRegion getShadowTextureRegion();
}

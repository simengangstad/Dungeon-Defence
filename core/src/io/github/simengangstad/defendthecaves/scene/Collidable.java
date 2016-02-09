package io.github.simengangstad.defendthecaves.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.defendthecaves.components.Drawable;
import io.github.simengangstad.defendthecaves.components.GameObject;

/**
 * An item which can be drawn and collide with the map.
 *
 * @author simengangstad
 * @since 09/02/16
 */
public abstract class Collidable extends GameObject implements Drawable {

    /**
     * The map the entity is located in.
     */
    protected Map map;

    /**
     * The movement of the entity during the frames.
     */
    protected Vector2 delta = new Vector2();

    /**
     * The force applied on the collidable.
     */
    protected final Vector2 forceApplied = new Vector2();

    /**
     * What way the force is applied in.
     */
    private boolean forcePositiveX = false, forcePositiveY = false;

    public Collidable(Vector2 position, Vector2 size) {

        super(position, size);
    }

    /**
     * Applies a vector force to the collidable which gets decreased by a
     */
    public void applyForce(Vector2 force) {

        forceApplied.set(force);

        forcePositiveX = 0 < forceApplied.x;
        forcePositiveY = 0 < forceApplied.y;
    }

    /**
     * Sets the map the movable entity resolves its collision against.
     */
    public void setMap(Map map) {

        this.map = map;
    }

    @Override
    public void tick() {

        if (map == null) {

            throw new RuntimeException("Need an assigned map in order to resolve collisions.");
        }

        if (forceApplied.x != 0.0f || forceApplied.y != 0.0f) {

            float delta = Gdx.graphics.getDeltaTime() * forceApplied.len();

            this.delta.add(forceApplied.x * delta, forceApplied.y * delta);

            forceApplied.add(delta * (forcePositiveX == false ? 1 : -1), delta * (forcePositiveY == false ? 1 : -1));

            forceApplied.set(forcePositiveX == true ? Math.max(0, forceApplied.x) : Math.min(0, forceApplied.x), forcePositiveY == true ? Math.max(0, forceApplied.y) : Math.min(0, forceApplied.y));
        }
    }
}

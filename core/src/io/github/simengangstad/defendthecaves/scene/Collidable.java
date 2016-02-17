package io.github.simengangstad.defendthecaves.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.defendthecaves.components.GameObject;

/**
 * An item which can be drawn and collide with the map.
 *
 * @author simengangstad
 * @since 09/02/16
 */
public abstract class Collidable extends GameObject {

    /**
     * The map the entity is located in.
     */
    public Map map;

    /**
     * The movement of the entity during the frames.
     */
    protected Vector2 delta = new Vector2();

    /**
     * The force applied on the collidable.
     */
    protected final Vector2 forceApplied = new Vector2();

    /**
     * The forces acting on the collidable regardless of force applied.
     * The body is in equilibrium with 0 velocity at default state.
     */
    private static final float Drag = 10.0f, Weight = -10.0f;

    private boolean weightActing = false;

    private float duration = 0.0f;

    /**
     * If the force along the axes are positive or negative.
     */
    private boolean horisontalPositive = false, verticalPositive = false;

    /**
     * Applies a vector force to the collidable which gets decreased by a drag force.
     */
    public void applyForce(Vector2 force, boolean weightActing, float duration) {

        forceApplied.set(force);

        horisontalPositive = 0 < forceApplied.x;
        verticalPositive = 0 < forceApplied.y;

        this.weightActing = weightActing;

        this.duration = duration;
    }

    @Override
    public void tick() {

        if (map == null) {

            throw new RuntimeException("Need an assigned map in order to resolve collisions.");
        }

        if (forceApplied.x != 0.0f) {

            float dragPerFrame = Gdx.graphics.getDeltaTime() * Drag;

            forceApplied.x += (horisontalPositive ? -dragPerFrame : dragPerFrame);

            if (horisontalPositive) {

                if (forceApplied.x < 0.0f) {

                    forceApplied.x = 0.0f;
                }
            }
            else {

                if (0.0f < forceApplied.x) {

                    forceApplied.x = 0.0f;
                }
            }
        }

        if (weightActing) {

            if (0.0f < duration) {

                duration -= Gdx.graphics.getDeltaTime();

                float weightPerFrame = Gdx.graphics.getDeltaTime() * Weight;

                forceApplied.y += weightPerFrame;
            }
            else {

                forceApplied.y = 0.0f;
            }
        }
        else {

            if (forceApplied.y != 0.0f) {

                float dragPerFrame = Gdx.graphics.getDeltaTime() * Drag;

                if (verticalPositive) {

                    forceApplied.y -= dragPerFrame;

                    if (forceApplied.y < 0.0f) {

                        forceApplied.y = 0.0f;
                    }
                }
                else {

                    forceApplied.y += dragPerFrame;

                    if (0.0f < forceApplied.y) {

                        forceApplied.y = 0.0f;
                    }
                }
            }
        }


        delta.add(forceApplied.x, forceApplied.y);
    }
}

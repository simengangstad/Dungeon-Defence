package io.github.simengangstad.defendthecaves.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.defendthecaves.Callback;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.components.Drawable;

/**
 * @author simengangstad
 * @since 24/12/15
 */
public abstract class Tool implements Drawable {

    public Entity parent;

    public Vector2 offset = new Vector2();

    protected final Vector2 size = new Vector2(120, 20);

    /**
     * If the drawable should be flipped.
     */
    public boolean flip = false;

    /**
     * If the weapon is interacting.
     */
    protected boolean interacting = false;

    /**
     * The duration of the interaction.
     */
    public final float interactionDuration;

    /**
     * The time left of the interaction.
     */
    protected float stateTime = 0.0f;

    /**
     * The callback called when the interaction has finished.
     */
    Callback interactionCallback;

    public Tool(float interactionDuration, Callback interactionCallback) {

        this.interactionDuration = interactionDuration;
        this.interactionCallback = interactionCallback;
    }

    public boolean isInteracting() {

        return interacting;
    }

    public float getStateTime() {

        return stateTime;
    }

    public void interact(Vector2 interactionDirection) {

        if (0 < getStateTime()) {

            return;
        }

        interacting(interactionDirection);

        interacting = true;

        stateTime = interactionDuration;
    }

    protected abstract void interacting(Vector2 interactingDirection);

    public void tick() {

        if (interacting) {

            stateTime -= Gdx.graphics.getDeltaTime();

            if (stateTime < 0.0f) {

                interacting = false;

                stateTime = 0.0f;

                doneInteracting();

                interactionCallback.callback();
            }
        }
    }

    protected abstract void doneInteracting();

    @Override
    public void draw(SpriteBatch batch, Vector2 position, Vector2 size) {

        if (Game.DebubDraw) {

            batch.draw(Game.debugDrawTexture, parent.getPosition().x + (flip() == false ? -size.x : 0), parent.getPosition().y - size.y / 2.0f, size.x, size.y);
        }
    }

    @Override
    public boolean flip() {

        return flip;
    }
}

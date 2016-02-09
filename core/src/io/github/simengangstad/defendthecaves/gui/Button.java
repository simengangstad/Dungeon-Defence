package io.github.simengangstad.defendthecaves.gui;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * @author simengangstad
 * @since 20/12/15
 */
public abstract class Button extends View {

    private boolean pressed = false;

    public Button(Vector2 position, Vector2 size, TextureRegion textureRegion) {

        super(position, size, textureRegion);
    }

    public boolean isPressed() {

        return pressed;
    }

    /**
     * @return The condition that the button is pressed.
     */
    public abstract boolean condition();

    /**
     * Delegate methods.
     */
    public abstract void buttonClicked();
    public abstract void buttonPressed();
    public abstract void buttonReleased();

    @Override
    public void tick() {

        if (condition()) {

            if (!pressed) {

                buttonClicked();
            }

            pressed = true;

            buttonPressed();
        }
        else {

            if (pressed) {

                buttonReleased();

                pressed = false;
            }
        }
    }
}

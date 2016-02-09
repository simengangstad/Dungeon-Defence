package io.github.simengangstad.defendthecaves.gui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.components.Drawable;
import io.github.simengangstad.defendthecaves.components.GameObject;

import java.util.ArrayList;

/**
 * @author simengangstad
 * @since 19/01/16
 */
public class View extends GameObject implements Drawable {

    /**
     * The view hosting this view (if any).
     */
    public View superview = null;

    /**
     * If the view is visible or not.
     */
    public boolean visible = true;

    /**
     * The subviews of this view.
     */
    private final ArrayList<View> subviews = new ArrayList<>();

    /**
     * The background texture.
     **/
    private TextureRegion background;

    private final Vector2 tmpPosition = new Vector2();

    /**
     * Initialises the view with a position and size (in pixels) and a {@link View#background}
     */
    public View(Vector2 position, Vector2 size, TextureRegion background) {

        super(position, size);

        this.background = background;
    }

    /**
     * Initialises the view with a position and size (in pixels) and a default background.
     */
    public View(Vector2 position, Vector2 size) {

        this(position, size, new TextureRegion(Game.SpriteSheet, 0, 160, 80, 48));
    }

    public void addSubview(View subview) {

        subviews.add(subview);

        subview.superview = this;
    }

    @Override
    public TextureRegion getTextureRegion() {

        return background;
    }

    public void tick() {

        subviews.forEach(subview -> subview.tick());
    }

    @Override
    public void draw(SpriteBatch batch, Vector2 position, Vector2 size) {

        if (!visible) return;

        if (getTextureRegion() != null) {

            batch.draw(getTextureRegion(), position.x, position.y, size.x, size.y);
        }

        subviews.forEach(subview -> {

            if (!subview.visible) return;

            tmpPosition.set(position).add(subview.getPosition());

            subview.draw(batch, tmpPosition, subview.getSize());
        });
    }

    @Override
    public boolean flip() {

        return false;
    }

    @Override
    public void dispose() {}
}

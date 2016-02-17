package io.github.simengangstad.defendthecaves.scene.gui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import io.github.simengangstad.defendthecaves.Game;

/**
 * @author simengangstad
 * @since 16/02/16
 */
public class HealthBar extends Table {

    private HealthBarStyle style;

    public final int max;

    public float value;

    public HealthBar(int max) {

        setStyle(Game.UISkin.get(HealthBarStyle.class));

        value = max;

        this.max = max;

        super.setSize(100.0f, 10.0f);
    }

    public void setStyle(HealthBarStyle style) {

        setBackground(style.background);

        this.style = style;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        super.draw(batch, parentAlpha);

        float posX = 2.0f;
        float posY = 2.0f;

        float width = getWidth() - posX * 2.0f;
        float height = getHeight() - posY * 2.0f;

        style.progress.draw(batch, getX() + posX, getY() + posY, (value / max) * width, height);
    }

    public static class HealthBarStyle {

        public Drawable background, progress;

        public HealthBarStyle() {}

        public HealthBarStyle(Drawable background, Drawable progress) {

            this.background = background;
            this.progress = progress;
        }
    }

}

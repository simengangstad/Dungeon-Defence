package io.github.simengangstad.dungeondefence.scene.gui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import io.github.simengangstad.dungeondefence.Game;

/**
 * @author simengangstad
 * @since 16/02/16
 */
public class ProgressBar extends Table {

    private ProgressBarStyle style;

    public final int max;

    public float value;

    public ProgressBar(int initial, int max) {

        setStyle(Game.UISkin.get(ProgressBarStyle.class));

        value = initial;

        this.max = max;

        super.setSize(100.0f, 10.0f);
    }

    public void setStyle(ProgressBarStyle style) {

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
}

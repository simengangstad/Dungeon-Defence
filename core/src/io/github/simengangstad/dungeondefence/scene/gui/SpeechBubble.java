package io.github.simengangstad.dungeondefence.scene.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import io.github.simengangstad.dungeondefence.Game;

/**
 * @author simengangstad
 * @since 14/02/16
 */
public class SpeechBubble extends Table {

    private Label label = new Label("", Game.UISkin);

    private SpeechBubbleStyle style;

    public SpeechBubble() {

        setStyle(Game.UISkin.get(SpeechBubbleStyle.class));

        label.setColor(Color.WHITE);
        label.setAlignment(Align.center);
    }

    public void setText(CharSequence text) {

        label.setText(text);

        label.setWrap(true);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        if (!isVisible()) {

            return;
        }

        label.setX(getX() + (getWidth() / style.background.getMinWidth()) * 1);
        label.setWidth(getWidth() - (getWidth() / style.background.getMinWidth()) * 1 * 2);
        setHeight(label.getPrefHeight());
        label.setY(getY() + getHeight() - label.getPrefHeight() / 2.0f);

        super.draw(batch, parentAlpha);
        label.draw(batch, parentAlpha);
    }

    public void setStyle(SpeechBubbleStyle style) {

        if (style == null) throw new NullPointerException("style cannot be null");

        this.style = style;

        setBackground(style.background);

        Label.LabelStyle labelStyle = label.getStyle();

        labelStyle.font = style.font;
        labelStyle.fontColor = style.fontColor;

        label.setStyle(labelStyle);
    }
}

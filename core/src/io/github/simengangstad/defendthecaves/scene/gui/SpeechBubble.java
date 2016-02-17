package io.github.simengangstad.defendthecaves.scene.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import io.github.simengangstad.defendthecaves.Game;

/**
 * @author simengangstad
 * @since 14/02/16
 */
public class SpeechBubble extends Table {

    private Label label = new Label("", Game.UISkin);

    private SpeechBubbleStyle style;

    public SpeechBubble() {

        setStyle(Game.UISkin.get(SpeechBubbleStyle.class));
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
        label.setY(getY() + label.getPrefHeight() / 2.0f);
        setHeight(label.getPrefHeight());

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

    public static class SpeechBubbleStyle {

        public Drawable background;
        public Drawable extra;
        public BitmapFont font;
        public Color fontColor;

        public SpeechBubbleStyle() {

        }

        public SpeechBubbleStyle(Drawable background, Drawable extra, BitmapFont font, Color fontColor) {

            this.background = background;
            this.extra = extra;
            this.font = font;
            this.fontColor = fontColor;
        }
    }
}

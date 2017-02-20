package io.github.simengangstad.dungeondefence.scene.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * Created by simengangstad on 18.02.2017.
 */
public class SpeechBubbleStyle {

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

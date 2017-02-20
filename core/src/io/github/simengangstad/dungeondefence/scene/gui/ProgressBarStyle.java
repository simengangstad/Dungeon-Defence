package io.github.simengangstad.dungeondefence.scene.gui;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * Created by simengangstad on 18.02.2017.
 */
public class ProgressBarStyle {
    public Drawable background, progress;

    public ProgressBarStyle() {}

    public ProgressBarStyle(Drawable background, Drawable progress) {

        this.background = background;
        this.progress = progress;
    }
}

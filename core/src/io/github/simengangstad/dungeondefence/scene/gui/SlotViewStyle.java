package io.github.simengangstad.dungeondefence.scene.gui;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * Created by simengangstad on 18.02.2017.
 */
public class SlotViewStyle {

    public Drawable background, slot;

    public SlotViewStyle() {

    }

    public SlotViewStyle(Drawable background, Drawable slot) {

        this.background = background;
        this.slot = slot;
    }
}

package io.github.simengangstad.dungeondefence.scene.gui;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.simengangstad.dungeondefence.Game;
import io.github.simengangstad.dungeondefence.GameObject;

/**
 * @author simengangstad
 * @since 26/03/16
 */
public class Pointer extends GameObject {

    private final TextureRegion mousePointerTextureRegion = new TextureRegion(Game.SpriteSheet, 96, 80, 16, 16);

    @Override
    public void create() {

    }

    @Override
    public void tick() {

    }

    @Override
    public boolean flip() {
        return false;
    }

    @Override
    public TextureRegion getTextureRegion() {

        return mousePointerTextureRegion;
    }

    @Override
    public void dispose() {

    }
}

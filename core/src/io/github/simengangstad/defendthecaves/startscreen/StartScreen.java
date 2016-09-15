package io.github.simengangstad.defendthecaves.startscreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import io.github.simengangstad.defendthecaves.Container;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.scene.Scene;

/**
 * @author simengangstad
 * @since 25/05/16
 */
public class StartScreen extends Container {

    String controls = "Thor the dwarf is stuck in a cave system with only a set\n" +
                      "of utilities. Explore the cave by crafting torches and placing\n" +
                      "them around. Be careful though, you are not alone...\n\n" +
                      "Move - WASD\n" +
                      "Mouse - Look around\n" +
                      "Left mouse - attack/place items/push with shield\n" +
                      "E - Inventory\n" +
                      "F - Interact/pick up items\n" +
                      "C - Throw items\n" +
                      "Tab - Shift between items\n" +
                      "Press items in left grid to craft when showing inventory\n";

    private TextButton playButton = new TextButton("Play", Game.UISkin);
    private TextButton exitButton = new TextButton("Exit", Game.UISkin);
    private Label label = new Label(controls, Game.UISkin, "default-font", Color.WHITE);

    public StartScreen() {

        Gdx.input.setCursorCatched(false);

        playButton.setWidth(200);
        playButton.setHeight(50);
        playButton.setPosition(Gdx.graphics.getWidth() / 2.0f - playButton.getWidth() / 2.0f, Gdx.graphics.getHeight() / 2.0f - playButton.getHeight() - 50);
        exitButton.setWidth(200);
        exitButton.setHeight(50);
        exitButton.setPosition(Gdx.graphics.getWidth() / 2.0f - playButton.getWidth() / 2.0f, Gdx.graphics.getHeight() / 2.0f - playButton.getHeight() - 125);
        label.setPosition(Gdx.graphics.getWidth() / 2.0f - (label.getWidth() / 2.0f) * 1.5f, Gdx.graphics.getHeight() / 2.0f - label.getHeight() + 300);
        label.setFontScale(1.5f);

        stage.addActor(playButton);
        stage.addActor(exitButton);
        stage.addActor(label);
    }

    @Override
    public void tick() {

        super.tick();

        if (playButton.isPressed()) {

            Game.container = new Scene();
        }
        else if (exitButton.isPressed()) {

            Gdx.app.exit();
        }
    }
}

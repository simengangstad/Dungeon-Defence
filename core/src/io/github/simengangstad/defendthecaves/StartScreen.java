package io.github.simengangstad.defendthecaves;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
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
                      "them around. Be careful though, you are not alone...\n\n";

    private TextButton playButton = new TextButton("Play", Game.UISkin);
    private TextButton controlsButton = new TextButton("Controls", Game.UISkin);
    private TextButton exitButton = new TextButton("Exit", Game.UISkin);
    private Label label = new Label(controls, Game.LabelStyle16);

    public StartScreen() {

        Gdx.input.setCursorCatched(false);

        playButton.setWidth(200);
        playButton.setHeight(50);
        playButton.setPosition(Gdx.graphics.getWidth() / 2.0f - playButton.getWidth() / 2.0f, Gdx.graphics.getHeight() / 2.0f - playButton.getHeight() + 25);
        controlsButton.setWidth(200);
        controlsButton.setHeight(50);
        controlsButton.setPosition(Gdx.graphics.getWidth() / 2.0f - controlsButton.getWidth() / 2.0f, Gdx.graphics.getHeight() / 2.0f - controlsButton.getHeight() - 50);
        exitButton.setWidth(200);
        exitButton.setHeight(50);
        exitButton.setPosition(Gdx.graphics.getWidth() / 2.0f - exitButton.getWidth() / 2.0f, Gdx.graphics.getHeight() / 2.0f - exitButton.getHeight() - 125);

        label.setPosition(Gdx.graphics.getWidth() / 2.0f - (label.getWidth() / 2.0f), Gdx.graphics.getHeight() - label.getHeight() - 20.0f);

        stage.addActor(playButton);
        stage.addActor(controlsButton);
        stage.addActor(exitButton);
        stage.addActor(label);


        playButton.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                Game.container = new Scene();

                return true;
            }
        });

        controlsButton.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                Game.tmpContainer = StartScreen.this;
                Game.container = new ControlsScreen();

                return true;
            }
        });

        exitButton.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                Gdx.app.exit();

                return true;
            }
        });
    }
}

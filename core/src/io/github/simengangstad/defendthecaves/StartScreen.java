package io.github.simengangstad.defendthecaves;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import io.github.simengangstad.defendthecaves.scene.Scene;

/**
 * @author simengangstad
 * @since 25/05/16
 */
public class StartScreen extends Container {

    private TextButton playButton, controlsButton, settingsButton, creditsButton, exitButton;

    public StartScreen() {

        playButton = new TextButton("Play", Game.UISkin);
        playButton.setWidth(200);
        playButton.setHeight(50);
        playButton.setPosition(Gdx.graphics.getWidth() / 2.0f - playButton.getWidth() / 2.0f, Gdx.graphics.getHeight() / 2.0f - playButton.getHeight() / 2.0f + 25);
        playButton.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

                super.touchUp(event, x, y, pointer, button);

                Game.container = new Scene();
            }
        });

        controlsButton = new TextButton("Controls", Game.UISkin);
        controlsButton.setWidth(200);
        controlsButton.setHeight(50);
        controlsButton.setPosition(Gdx.graphics.getWidth() / 2.0f - controlsButton.getWidth() / 2.0f, Gdx.graphics.getHeight() / 2.0f - controlsButton.getHeight() / 2.0f - 35);
        controlsButton.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

                super.touchUp(event, x, y, pointer, button);

                Game.swapContainer(StartScreen.this, new ControlsScreen());
            }
        });

        settingsButton = new TextButton("Settings", Game.UISkin);
        settingsButton.setWidth(200);
        settingsButton.setHeight(50);
        settingsButton.setPosition(Gdx.graphics.getWidth() / 2.0f - settingsButton.getWidth() / 2.0f, Gdx.graphics.getHeight() / 2.0f - settingsButton.getHeight() / 2.0f - 95);
        settingsButton.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

                super.touchUp(event, x, y, pointer, button);

                Game.swapContainer(StartScreen.this, new SettingsScreen());
            }
        });

        creditsButton = new TextButton("Credits", Game.UISkin);
        creditsButton.setWidth(200);
        creditsButton.setHeight(50);
        creditsButton.setPosition(Gdx.graphics.getWidth() / 2.0f - creditsButton.getWidth() / 2.0f, Gdx.graphics.getHeight() / 2.0f - creditsButton.getHeight() / 2.0f - 155);
        creditsButton.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

                super.touchUp(event, x, y, pointer, button);

                Game.swapContainer(StartScreen.this, new CreditsScreen());
            }
        });

        exitButton = new TextButton("Exit", Game.UISkin);
        exitButton.setWidth(200);
        exitButton.setHeight(50);
        exitButton.setPosition(Gdx.graphics.getWidth() / 2.0f - exitButton.getWidth() / 2.0f, Gdx.graphics.getHeight() / 2.0f - exitButton.getHeight() / 2.0f - 215);
        exitButton.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

                super.touchUp(event, x, y, pointer, button);

                Gdx.app.exit();
            }
        });


        String controls =   "Thor the dwarf is stuck in a cave system with only a set\n" +
                            "of utilities. Explore the cave by crafting torches and placing\n" +
                            "them around. Be careful though, you are not alone...\n\n";
        Label label = new Label(controls, Game.LabelStyle16);
        label.setPosition(Gdx.graphics.getWidth() / 2.0f - (label.getWidth() / 2.0f), playButton.getY() + 40.0f);

        Label title = new Label("Dungeon Defence beta 0.0.2", Game.LabelStyle32);
        title.setPosition(Gdx.graphics.getWidth() / 2.0f - (title.getWidth() / 2.0f), playButton.getY() + label.getHeight() + 20.0f + 40);

        stage.addActor(controlsButton);
        stage.addActor(exitButton);
        stage.addActor(settingsButton);
        stage.addActor(creditsButton);
        stage.addActor(playButton);
        stage.addActor(label);
        stage.addActor(title);

        background = Game.Background;
    }

    @Override
    public void tick() {

        super.tick();

        pointer.position.set(Gdx.input.getX(), -(Gdx.input.getY() - (Gdx.graphics.getHeight() - 1)));
        stage.getBatch().begin();
        pointer.draw((SpriteBatch) stage.getBatch());
        stage.getBatch().end();
    }
}

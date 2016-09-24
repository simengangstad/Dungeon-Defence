package io.github.simengangstad.defendthecaves;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

    TextButton playButton, controlsButton, exitButton;

    public StartScreen() {

        playButton = new TextButton("Play", Game.UISkin);
        playButton.setWidth(200);
        playButton.setHeight(50);
        playButton.setPosition(Gdx.graphics.getWidth() / 2.0f - playButton.getWidth() / 2.0f, Gdx.graphics.getHeight() / 2.0f - playButton.getHeight() / 2.0f + 25);

        controlsButton = new TextButton("Controls", Game.UISkin);
        controlsButton.setWidth(200);
        controlsButton.setHeight(50);
        controlsButton.setPosition(Gdx.graphics.getWidth() / 2.0f - controlsButton.getWidth() / 2.0f, Gdx.graphics.getHeight() / 2.0f - controlsButton.getHeight() / 2.0f - 50);

        exitButton = new TextButton("Exit", Game.UISkin);
        exitButton.setWidth(200);
        exitButton.setHeight(50);
        exitButton.setPosition(Gdx.graphics.getWidth() / 2.0f - exitButton.getWidth() / 2.0f, Gdx.graphics.getHeight() / 2.0f - exitButton.getHeight() / 2.0f - 125);

        String controls =   "Thor the dwarf is stuck in a cave system with only a set\n" +
                            "of utilities. Explore the cave by crafting torches and placing\n" +
                            "them around. Be careful though, you are not alone...\n\n";
        Label label = new Label(controls, Game.LabelStyle16);
        label.setPosition(Gdx.graphics.getWidth() / 2.0f - (label.getWidth() / 2.0f), Gdx.graphics.getHeight() - label.getHeight() - 20);

        stage.addActor(controlsButton);
        stage.addActor(exitButton);
        stage.addActor(playButton);
        stage.addActor(label);


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

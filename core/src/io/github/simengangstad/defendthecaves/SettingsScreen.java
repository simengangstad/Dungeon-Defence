package io.github.simengangstad.defendthecaves;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import io.github.simengangstad.defendthecaves.scene.Scene;

/**
 * Created by simengangstad on 02/10/2016.
 */
public class SettingsScreen extends Container {

    private Preferences preferences = Gdx.app.getPreferences("prefs");

    private boolean music = preferences.getBoolean("music"), sfx = preferences.getBoolean("sound");

    private Label label = new Label("Settings", Game.LabelStyle16);
    private TextButton backButton = new TextButton("Back", Game.UISkin);
    private TextButton musicButton = new TextButton("Music: " + music, Game.UISkin), soundButton = new TextButton("Sound effects: " + sfx, Game.UISkin);

    public SettingsScreen() {

        label.setPosition(Gdx.graphics.getWidth() / 2.0f - label.getWidth() / 2.0f, Gdx.graphics.getHeight() / 2.0f - label.getHeight() / 2.0f + 100);

        musicButton.setWidth(200);
        musicButton.setHeight(50);
        musicButton.setPosition(Gdx.graphics.getWidth() / 2.0f - musicButton.getWidth() / 2.0f, Gdx.graphics.getHeight() / 2.0f - musicButton.getHeight() / 2.0f + 25);
        musicButton.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

                super.touchUp(event, x, y, pointer, button);

                music = !music;
                preferences.putBoolean("music", music);

                musicButton.setText("Music: " + music);

                if (Game.tmpContainer instanceof Scene) {

                    ((Scene) Game.tmpContainer).jukebox.setMute(!music);
                }
            }
        });


        soundButton.setWidth(200);
        soundButton.setHeight(50);
        soundButton.setPosition(Gdx.graphics.getWidth() / 2.0f - soundButton.getWidth() / 2.0f, Gdx.graphics.getHeight() / 2.0f - soundButton.getHeight() / 2.0f - 50);
        soundButton.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

                super.touchUp(event, x, y, pointer, button);

                sfx = !sfx;
                preferences.putBoolean("sound", sfx);

                soundButton.setText("Sound effects: " + sfx);

                Game.PlaySound = sfx;
            }
        });


        backButton.setWidth(200);
        backButton.setHeight(50);
        backButton.setPosition(Gdx.graphics.getWidth() / 2.0f - backButton.getWidth() / 2.0f, Gdx.graphics.getHeight() / 2.0f - backButton.getHeight() - 125);

        backButton.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

                super.touchUp(event, x, y, pointer, button);

                Gdx.input.setInputProcessor(Game.tmpContainer.inputMultiplexer);

                preferences.flush();

                Game.container = Game.tmpContainer;
                Game.tmpContainer = SettingsScreen.this;
            }
        });

        stage.addActor(backButton);
        stage.addActor(musicButton);
        stage.addActor(soundButton);
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

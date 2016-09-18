package io.github.simengangstad.defendthecaves;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

/**
 * Created by simengangstad on 15/09/16.
 */
public class ControlsScreen extends Container {

    String controls =   "Move - WASD\n" +
                        "Mouse - Look around\n" +
                        "Left mouse - attack/place items/push with shield\n" +
                        "E - Inventory\n" +
                        "F - Interact/pick up items\n" +
                        "C - Throw items\n" +
                        "Tab or 1, 2, 3, 4 - Shift between items\n" +
                        "Press items in left grid to craft when showing inventory\n";

    private TextButton backButton = new TextButton("Back", Game.UISkin);
    private Label label = new Label(controls, Game.LabelStyle16);

    public ControlsScreen() {

        Gdx.input.setCursorCatched(false);

        backButton.setWidth(200);
        backButton.setHeight(50);
        backButton.setPosition(Gdx.graphics.getWidth() / 2.0f - backButton.getWidth() / 2.0f, Gdx.graphics.getHeight() / 2.0f - backButton.getHeight() - 125);
        label.setPosition(Gdx.graphics.getWidth() / 2.0f - (label.getWidth() / 2.0f), Gdx.graphics.getHeight() - label.getHeight() - 20.0f);


        backButton.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                Game.container = Game.tmpContainer;

                return true;
            }
        });

        stage.addActor(backButton);
        stage.addActor(label);
    }
}
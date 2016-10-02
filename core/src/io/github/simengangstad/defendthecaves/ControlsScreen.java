package io.github.simengangstad.defendthecaves;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
                        "Left mouse - attack/place items/push with shield/mine stone\n" +
                        "E - Inventory\n" +
                        "F - Interact/pick up items\n" +
                        "C - Throw items\n" +
                        "Tab, 1, 2, 3, 4, 5 or scroll - Shift between items\n" +
                        "Press items in left grid to craft when showing inventory\n";

    private TextButton backButton = new TextButton("Back", Game.UISkin);
    private Label label = new Label(controls, Game.LabelStyle16);

    public ControlsScreen() {

        backButton.setWidth(200);
        backButton.setHeight(50);
        backButton.setPosition(Gdx.graphics.getWidth() / 2.0f - backButton.getWidth() / 2.0f, Gdx.graphics.getHeight() / 2.0f - backButton.getHeight() - 125);
        label.setPosition(Gdx.graphics.getWidth() / 2.0f - (label.getWidth() / 2.0f), Gdx.graphics.getHeight() - label.getHeight() - 20.0f);

        backButton.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

                super.touchUp(event, x, y, pointer, button);

                Gdx.input.setInputProcessor(Game.tmpContainer.inputMultiplexer);

                Game.container = Game.tmpContainer;
                Game.tmpContainer = ControlsScreen.this;
            }
        });

        stage.addActor(backButton);
        stage.addActor(label);
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
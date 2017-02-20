package io.github.simengangstad.dungeondefence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

/**
 * Created by simengangstad on 02/10/2016.
 */
public class CreditsScreen extends Container {

    String credits =    "Music: http://www.purple-planet.com\n" +
                        "Explosion sprites - Master484, http://m484games.ucoz.com/\n" +
                        "Orc sfx - LittleRobotSoundFactory (freesound), http://www.freesound.org/people/LittleRobotSoundFactory/sounds/316358/, no changes made\n" +
                        "Orc sfx - Stephen Saldanha (freesound), http://www.freesound.org/people/StephenSaldanha/sounds/186673/, no changes made\n" +
                        "Snake hiss - csaszi (freesound), http://www.freesound.org/people/csaszi/sounds/252768/\n" +
                        "Snake bite - Jamius (freesound), http://www.freesound.org/people/Jamius/sounds/41531/\n" +
                        "RPG sound pack - artisticdude (freesound), http://opengameart.org/content/rpg-sound-pack\n" +
                        "Error - Isaac200000 (freesound), http://www.freesound.org/people/Isaac200000/sounds/188013/\n" +
                        "Spit - davidou (freesound), http://www.freesound.org/people/davidou/sounds/88459/\n" +
                        "Rocks - kwandalist (freesound), http://www.freesound.org/people/kwandalist/sounds/47496/\n" +
                        "Burp - Adam_N (freesound), http://www.freesound.org/people/Adam_N/sounds/324514/\n" +
                        "Bottle breaking - Chela Sivesta (freesound), http://www.freesound.org/people/Chela%20Sivesta/sounds/162169/\n" +
                        "Crossbow - Erdie (freesound), http://www.freesound.org/people/Erdie/sounds/65734/?page=3#comment\n" +
                        "Explosion sound - Tom McCann (freesound), http://www.freesound.org/people/tommccann/sounds/235968/\n";

    private TextButton backButton = new TextButton("Back", Game.UISkin);
    private Label label = new Label(credits, Game.LabelStyle8);

    public CreditsScreen() {


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
                Game.tmpContainer = CreditsScreen.this;
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

package io.github.simengangstad.defendthecaves.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.GameObject;
import io.github.simengangstad.defendthecaves.scene.entities.*;
import io.github.simengangstad.defendthecaves.scene.items.*;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * An arithmetic sequence which adds {@link WaveSystem#startAmount} + {@link WaveSystem#difference} * ({@link WaveSystem#wave} - 1)
 * with every request after waiting a set {@link WaveSystem#intervalBetweenWaves}.
 *
 * @author simengangstad
 * @since 17/03/16
 */
public class WaveSystem {

    public interface WaveCallback {

        void callback(HashMap<Barrier, ArrayList<Enemy>> enemiesAtHold);
    }

    private final int startAmount;

    private final int difference;

    private final float intervalBetweenWaves;


    private final HashMap<Barrier, ArrayList<Enemy>> enemiesAtHold = new HashMap<>();

    private int wave = 1;

    private float timer = 0.0f;

    private boolean requestedNewWave = false;


    private Map map;

    private Player player;

    private int enemiesLeft = 0;

    private ArrayList<Key> keys;

    public Barrier[] barriers;

    private WaveCallback callback;


    private int state = 0;

    private Label countdownLabel = new Label("", new Label.LabelStyle(new BitmapFont(Gdx.files.internal("assets/gui/font.txt"), Gdx.files.internal("assets/gui/font.png"), false), new Color(Color.WHITE)));

    private Label currentLabel = new Label("Current wave: 1", Game.UISkin);

    public WaveSystem(int startAmount, int difference, int intervalBetweenWaves, Map map, Player player, ArrayList<Key> keys, Barrier[] barriers, WaveCallback callback) {

        this.startAmount = startAmount;
        this.difference = difference;
        this.intervalBetweenWaves = intervalBetweenWaves;
        this.map = map;
        this.player = player;
        this.keys = keys;
        this.barriers = barriers;
        this.callback = callback;

        countdownLabel.setVisible(true);

        player.host.stage.addActor(countdownLabel);

        for (Barrier barrier : barriers) {

            enemiesAtHold.put(barrier, new ArrayList<>());
        }

        currentLabel.setFontScale(0.45f);
        currentLabel.setPosition(10, -100);
        player.host.stage.addActor(currentLabel);
    }

    public void reset() {

        wave = 1;
        timer = 0.0f;
        requestedNewWave = false;
        state = 0;

        enemiesAtHold.clear();

        for (Barrier barrier : barriers) {

            enemiesAtHold.put(barrier, new ArrayList<>());
        }

        player.host.stage.addActor(countdownLabel);
        player.host.stage.addActor(currentLabel);
    }

    public float getRemainingTime() {

        return timer;
    }

    public int getWave() {

        return wave;
    }

    public void requestWave() {

        requestedNewWave = true;

        timer = intervalBetweenWaves;
    }

    public void addDeadEnemy() {

        enemiesLeft--;

        if (enemiesLeft == 0) {

            requestWave();
        }
    }

    public int getEnemiesLeft() {

        return enemiesLeft;
    }

    public void tick() {

        if (requestedNewWave) {

            timer -= Gdx.graphics.getDeltaTime();

            if (0.0f < timer && timer <= 10.0f) {

                int value = (int) Math.ceil(timer);

                float delta = 1 - (value - timer);

                if (state != value) {

                    countdownLabel.setVisible(true);

                    countdownLabel.setText(value + "");

                    state = value;
                }

                countdownLabel.setPosition(Gdx.graphics.getWidth() - countdownLabel.getPrefWidth() - 20, 70);
                countdownLabel.setColor(1.0f, 1.0f, 1.0f, delta);
            }
            else if (-1 < timer && timer <= 0) {

                float delta = 1 - (0 - timer);

                if (state != 0) {

                    countdownLabel.setVisible(true);

                    countdownLabel.setText("New wave incoming!");

                    state = 0;
                }

                countdownLabel.setPosition(Gdx.graphics.getWidth() / 2.0f - countdownLabel.getPrefWidth() / 2.0f, Gdx.graphics.getHeight() / 2.0f);
                countdownLabel.setColor(1.0f, 1.0f, 1.0f, delta);
            }
            else {

                countdownLabel.setVisible(false);
            }

            if (timer < -1.0f) {

                timer = -1.0f;

                requestedNewWave = false;

                System.out.println("Requesting another wave. " + (startAmount + (wave - 1) * difference) + " enemies ready in " + intervalBetweenWaves + " seconds!");

                deployEnemies();

                wave++;

                if (wave % 10 == 0) {

                    ((Scene) player.host).fillLockedRoomsWithLoot(false);

                    countdownLabel.setText("New wave incoming! Rooms refilled with loot!");
                }

                if (wave > 1) {

                    currentLabel.setText("Current wave: " + (wave - 1));
                }
            }
        }
    }

    private void deployEnemies() {

        Vector2 positionOfBarrier = Game.vector2Pool.obtain();

        enemiesLeft = (startAmount + difference * (wave - 1));

        ArrayList<Item> items = new ArrayList<>();

        for (int enemyIndex = 0; enemyIndex < (startAmount + difference * (wave - 1)); enemyIndex++) {

            Barrier barrier = barriers[MathUtils.random(barriers.length - 1)];

            positionOfBarrier.set(barrier.position);

            if (map.isSolid((int) barrier.position.x - 1, (int) barrier.position.y) && map.isSolid((int) barrier.position.x + 1, (int) barrier.position.y)) {

                positionOfBarrier.y += map.isSolid((int) barrier.position.x, (int) barrier.position.y + 1) ? -1 : 1;
            }
            else {

                positionOfBarrier.x += map.isSolid((int) barrier.position.x + 1, (int) barrier.position.y) ? -1 : 1;
            }

            Vector2 positionOfEnemy = positionOfBarrier.scl(Map.TileSizeInPixelsInWorldSpace);

            Enemy enemyToAdd = null;

            switch (MathUtils.random(2)) {

                case 0:

                    enemyToAdd = new Orc(positionOfEnemy, new Vector2(Game.EntitySize, Game.EntitySize), player);

                    Cudgel cudgel = new Cudgel(null);

                    enemyToAdd.addItemAtLocation(0, 0, cudgel);

                    cudgel.map = map;

                    break;

                case 1:

                    enemyToAdd = new Snake(positionOfEnemy, new Vector2(Game.EntitySize * 2, Game.EntitySize), player);

                    break;

                case 2:

                    enemyToAdd = new Caterpillar(positionOfEnemy, new Vector2(Game.EntitySize, Game.EntitySize), player);

                    break;
            }

            items.clear();

            for (int i = 0; i < 2; i++) {

                switch (MathUtils.random(5)) {

                    case 0:

                        items.add(new HealthPotion(positionOfEnemy));

                        break;

                    case 1:

                        items.add(new ToxicPotion(positionOfEnemy));

                        break;

                    case 2:

                        items.add(new ExplosivePotion(positionOfEnemy));

                        break;

                    case 3:

                        items.add(new StringItem(positionOfEnemy));

                        break;

                    case 4:

                        items.add(new Coal(positionOfEnemy));

                        break;

                    case 5:

                        items.add(new Wood(positionOfEnemy));

                        break;
                }
            }

            enemyToAdd.map = map;
            items.forEach(enemyToAdd::addItem);

            if (!keys.isEmpty() && MathUtils.random(100) < 15) {

                Key key = keys.get(keys.size() - 1);

                key.map = this.map;

                enemyToAdd.addItemAtLocation(1, 0, key);

                keys.remove(key);
            }

            enemiesAtHold.get(barrier).add(enemyToAdd);
        }

        Game.vector2Pool.free(positionOfBarrier);

        callback.callback(enemiesAtHold);
    }
}

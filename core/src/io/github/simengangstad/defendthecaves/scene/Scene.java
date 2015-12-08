package io.github.simengangstad.defendthecaves.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;
import io.github.simengangstad.defendthecaves.Map;
import io.github.simengangstad.defendthecaves.components.Drawable;
import io.github.simengangstad.defendthecaves.components.GameObject;
import io.github.simengangstad.defendthecaves.scene.entities.Player;

import java.util.ArrayList;

/**
 * @author simengangstad
 * @since 15/11/15
 */
public class Scene implements Disposable {

    /**
     * The batch responsable for drawing.
     */
    private SpriteBatch batch = new SpriteBatch();

    /**
     * The map that hosts the game objects.
     */
    private Map map;

    /**
     * The player which interacts with the map.
     */
    private Player player;

    /**
     * The game objects in the scene.
     */
    private ArrayList<GameObject> gameObjects = new ArrayList<>();

    /**
     * Instantiates the scene with a map and a player.
     */
    public Scene(Map map, Player player) {

        this.map = map;
        this.player = player;

        player.setMap(map);

        boolean spawnPositionFound = false;

        for (int x = 0; x < map.getWidth() * map.Subdivision; x++) {

            for (int y = 0; y < map.getHeight() * map.Subdivision; y++) {

                if (!map.isSolid(x, y) && MathUtils.random(100) < 50) {

                    player.getPosition().set(x * map.CellSize, y * map.CellSize);
                    player.updateCameraPosition();

                    spawnPositionFound = true;
                }

                if (x == map.getWidth() * map.Subdivision - 1 && y == map.getHeight() * map.Subdivision - 1 && !spawnPositionFound) {

                    x = 0;

                    break;
                }

                if (spawnPositionFound) break;
            }

            if (spawnPositionFound) break;
        }
    }

    /**
     * Updates the viewport and the projection matrix of the player's camera.
     */
    public void updateMatrices() {

        player.camera.viewportWidth = Gdx.graphics.getWidth();
        player.camera.viewportHeight = Gdx.graphics.getHeight();
        player.camera.update();
    }

    /**
     * Updates the state of the scene.
     */
    public void tick() {

        player.tick();

        batch.setProjectionMatrix(player.camera.combined);

        batch.begin();

        map.playerPosition = player.getPosition();

        map.draw(batch);

        player.draw(batch, player.getPosition(), player.getSize());

        gameObjects.forEach((gameObject -> {

            gameObject.tick();

            if (gameObject instanceof Drawable) {

                ((Drawable) gameObject).draw(batch, gameObject.getPosition(), gameObject.getSize());
            }
        }));

        batch.end();
    }

    @Override
    public void dispose() {

        batch.dispose();

        player.dispose();

        gameObjects.forEach((gameObject -> gameObject.dispose()));
    }
}

package io.github.simengangstad.defendthecaves.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;
import io.github.simengangstad.defendthecaves.Game;
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

        TextureRegion tex = new TextureRegion(Game.spriteSheet, 48, 240, 16, 16);
        TextureRegion shadow = new TextureRegion(Game.spriteSheet, 0, 0, 16, 16);

        Entity entity = new Entity(player.getPosition().cpy(), player.getSize().cpy()) {

            @Override
            public void dispose() {


            }

            @Override
            public TextureRegion getTextureRegion() {

                return tex;
            }

            @Override
            public TextureRegion getShadowTextureRegion() {

                return shadow;
            }

            @Override
            public boolean flip() {
                return false;
            }
        };

        addGameObject(entity);
    }

    /**
     * Updates the viewport and the projection matrix of the player's camera.
     */
    public void updateMatrices() {

        player.camera.viewportWidth = Gdx.graphics.getWidth();
        player.camera.viewportHeight = Gdx.graphics.getHeight();
        player.camera.update();
    }

    public void addGameObject(GameObject gameObject) {

        gameObjects.add(gameObject);

        gameObject.host = this;
    }

    /**
     * Damages the entities inside the rect besides the excludables.
     */
    public void damage(Weapon weapon, float x, float y, float width, float height, Entity excludable) {

        for (GameObject gameObject : gameObjects) {

            if (gameObject instanceof Entity) {

                Entity entity = (Entity) gameObject;

                if (entity == excludable) {

                    continue;
                }

                if (intersect(x, y, width, height, entity.getPosition().x, entity.getPosition().y, entity.getSize().x, entity.getSize().y)) {

                    entity.health -= weapon.attackDamage;

                    System.out.println("Damage applied to entity: " + entity);
                }
            }
        }
    }

    public boolean intersect(float x1, float y1, float w1, float h1, float x2, float y2, float w2, float h2) {

        return  (x1 <= x2 + w1) && (x1 + w2 >= x2) &&
                (y1 <= y2 + h1) && (y1 + h2 >= y2);
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

        for (GameObject gameObject : gameObjects) {

            if (gameObject instanceof Entity) {

                Entity entity = (Entity) gameObject;

                if (entity.health < 0) {

                    continue;
                }
            }

            gameObject.tick();

            if (gameObject instanceof Drawable) {

                ((Drawable) gameObject).draw(batch, gameObject.getPosition(), gameObject.getSize());
            }
        }

        batch.end();
    }

    @Override
    public void dispose() {

        batch.dispose();

        player.dispose();

        gameObjects.forEach((gameObject -> gameObject.dispose()));
    }
}

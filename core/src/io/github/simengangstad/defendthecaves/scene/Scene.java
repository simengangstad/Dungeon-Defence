package io.github.simengangstad.defendthecaves.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.defendthecaves.Container;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.components.Drawable;
import io.github.simengangstad.defendthecaves.components.GameObject;
import io.github.simengangstad.defendthecaves.gui.KeyButton;
import io.github.simengangstad.defendthecaves.pathfinding.PathfindingGrid;
import io.github.simengangstad.defendthecaves.scene.entities.Enemy;
import io.github.simengangstad.defendthecaves.scene.entities.FallingStone;
import io.github.simengangstad.defendthecaves.scene.entities.Player;
import io.github.simengangstad.defendthecaves.scene.weapons.Shield;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author simengangstad
 * @since 20/12/15
 */
public class Scene extends Container {

    /**
     * The map that hosts the game objects.
     */
    private Map map;

    public PathfindingGrid pathfindingGrid;

    /**
     * The player which interacts with the map.
     */
    public Player player;

    /**
     * The barriers.
     */
    private final Barrier[] barriers;

    private final HashMap<Barrier, ArrayList<Enemy>> enemiesAtHold = new HashMap<>();

    private final Vector2 tmp = new Vector2();

    private Container frame = new Container();

    private Matrix4 frameProjectionMatrix = new Matrix4();

    /**
     * Instantiates the scene with a player.
     */
    public Scene(Player player) {

        map = new Map(40, 40, 15, 3, 9, 12342, player.getSize(), 6, 6);

        pathfindingGrid = new PathfindingGrid(map.getWidth(), map.getHeight());

        updatePathfindingGrid();

        this.player = player;

        player.host = this;
        player.setMap(map);

        addGameObject(player);

        boolean spawnPositionFound = false;

        while (!spawnPositionFound) {

            for (int x = 0; x < map.getWidth(); x++) {

                for (int y = 0; y < map.getHeight(); y++) {

                    if (!map.isSolid(x, y) && MathUtils.random(100) < 50) {

                        player.getPosition().set(x * map.TileSizeInPixelsInWorldSpace + player.getSize().x / 2.0f, y * map.TileSizeInPixelsInWorldSpace + player.getSize().y / 2.0f);
                        player.updateCameraPosition();

                        spawnPositionFound = true;

                        break;
                    }

                    if (spawnPositionFound) {

                        break;
                    }
                }

                if (spawnPositionFound) {

                    break;
                }
            }
        }

        Vector2[] spawnPoints = map.getSpawnPoints();

        barriers = new Barrier[spawnPoints.length];

        for (int i = 0; i < barriers.length; i++) {

            barriers[i] = new Barrier(spawnPoints[i]);

            enemiesAtHold.put(barriers[i], new ArrayList<>());
        }

        Spawner<Barrier> spawner = new Spawner(barriers);

        spawner.spawn(4, 4000, item -> {

            Barrier barrier = (Barrier) item;

            Vector2 position = barrier.position.cpy();

            boolean horisontal = map.isSolid((int) barrier.position.x - 1, (int) barrier.position.y) && map.isSolid((int) barrier.position.x + 1, (int) barrier.position.y);

            if (horisontal) {

                if (map.isSolid((int) barrier.position.x, (int) barrier.position.y + 1)) {

                    position.y -= 1;
                }
                else {

                    position.y += 1;
                }
            }
            else {

                if (map.isSolid((int) barrier.position.x + 1, (int) barrier.position.y)) {

                    position.x -= 1;
                }
                else {

                    position.x += 1;
                }
            }

            position.add(0.5f, 0.5f);

            enemiesAtHold.get(barrier).add(new Enemy(position.cpy().scl(map.TileSizeInPixelsInWorldSpace), player.getPosition(), player.getSize(), 2));
        });

        //addGameObject(new Enemy(player.getPosition().cpy(), player.getPosition(), player.getSize(), 2));


        // FRAME

        button = new KeyButton(new Vector2(), new Vector2(32, 32), new TextureRegion(Game.guiSheet, 0, 0, 16, 16), Input.Keys.G) {

            @Override
            public void buttonClicked() {

            }

            @Override
            public void buttonPressed() {

                if (closestsBarrier == null) return;

                closestsBarrier.updateState(10 * Gdx.graphics.getDeltaTime());
            }

            @Override
            public void buttonReleased() {

            }
        };

        button.visible = false;

        frame.addGameObject(button);

        ShaderProgram shaderProgram = new ShaderProgram(Gdx.files.internal("assets/shaders/shader.vs"), Gdx.files.internal("assets/shaders/shader.fs"));

        if (!shaderProgram.isCompiled()) {

            System.err.println("Couldn't compile shader: " + shaderProgram.getLog());
        }

        batch.setShader(shaderProgram);
    }

    private void updatePathfindingGrid() {

        for (int x = 0; x < pathfindingGrid.width; x++) {

            for (int y = 0; y < pathfindingGrid.height; y++) {

                pathfindingGrid.set(x, y, map.isSolid(x, y) == true ? PathfindingGrid.State.Closed : PathfindingGrid.State.Open);
            }
        }
    }

    KeyButton button;

    /**
     * Updates the viewport and the projection matrix of the player's camera.
     */
    public void updateMatrices() {

        player.camera.viewportWidth = Gdx.graphics.getWidth();
        player.camera.viewportHeight = Gdx.graphics.getHeight();
        player.camera.update();

        frame.setProjectionMatrix(frameProjectionMatrix.setToOrtho(0.0f, Gdx.graphics.getWidth(), 0.0f, Gdx.graphics.getHeight(), -1.0f, 1.0f));
    }

    @Override
    public void addGameObject(GameObject gameObject) {

        super.addGameObject(gameObject);

        if (gameObject instanceof MovableEntity) {

            MovableEntity movableEntity = (MovableEntity) gameObject;

            movableEntity.setMap(map);
        }
    }

    /**
     * Damages the entities inside the rect besides the excludables.
     */
    public void damage(Weapon weapon, Vector2 attackDirection, float x, float y, float width, float height, Entity excludable) {

        for (GameObject gameObject : gameObjects) {

            // Prevent enemies from hitting enemies
            if (excludable.getClass() == Enemy.class && gameObject.getClass() == Enemy.class) {

                continue;
            }

            if (gameObject instanceof Entity) {

                Entity entity = (Entity) gameObject;

                if (entity == excludable) {

                    continue;
                }

                if (intersect(x, y, width, height, entity.getPosition().x, entity.getPosition().y, entity.getSize().x, entity.getSize().y)) {

                    if (entity.currentTool instanceof Shield && entity.flip() != excludable.flip()) {

                        break;
                    }

                    entity.takeDamage(weapon.attackDamage);

                    entity.paralyse();

                    if (entity instanceof MovableEntity) {

                        ((MovableEntity) entity).applyForce(attackDirection.nor().scl(7.5f));
                    }

                    System.out.println("Damage applied to entity: " + entity);

                    break;
                }
            }
        }
    }

    public boolean intersect(float x1, float y1, float w1, float h1, float x2, float y2, float w2, float h2) {

        return  (x1 <= x2 + w1) && (x1 + w2 >= x2) &&
                (y1 <= y2 + h1) && (y1 + h2 >= y2);
    }

    private void spawnFallingStones(Vector2 position) {

        boolean horisontal = map.isSolid((int) position.x - 1, (int) position.y) && map.isSolid((int) position.x + 1, (int) position.y);

        Vector2 dstPosition = new Vector2(position);

        int yDelta = 0;
        int xDelta = 0;

        if (horisontal) {

            if (map.isSolid((int) position.x, (int) position.y + 1)) {

                yDelta = 1;
            }
            else {

                yDelta = -1;
            }
        }
        else {

            if (map.isSolid((int) position.x + 1, (int) position.y)) {

                xDelta = -1;
            }
            else {

                xDelta = 1;
            }
        }

        addGameObject(new FallingStone(
                new Vector2(position.x * Map.TileSizeInPixelsInWorldSpace - (Map.TileSizeInPixelsInWorldSpace / Game.SizeOfTileInPixelsInSpritesheet) * 6 + MathUtils.random(Map.TileSizeInPixelsInWorldSpace - 5 * (Map.TileSizeInPixelsInWorldSpace / Game.SizeOfTileInPixelsInSpritesheet)), position.y * Map.TileSizeInPixelsInWorldSpace - (Map.TileSizeInPixelsInWorldSpace / Game.SizeOfTileInPixelsInSpritesheet) * 6 + MathUtils.random(Map.TileSizeInPixelsInWorldSpace - (Map.TileSizeInPixelsInWorldSpace / Game.SizeOfTileInPixelsInSpritesheet) * 5)),
                dstPosition.set(dstPosition.x * Map.TileSizeInPixelsInWorldSpace + xDelta * (MathUtils.random(Map.TileSizeInPixelsInWorldSpace - 5) + 5), dstPosition.y * Map.TileSizeInPixelsInWorldSpace + yDelta * (MathUtils.random(Map.TileSizeInPixelsInWorldSpace - 5) + 5)),
                new Vector2(Map.TileSizeInPixelsInWorldSpace, Map.TileSizeInPixelsInWorldSpace)));
    }


    @Override
    /**
     * Updates the state of the scene.
     */
    public void tick() {

        enemiesAtHold.forEach((barrier, array) -> {

            if (barrier.getState() == 0) {

                array.forEach(enemy -> addGameObject(enemy));

                array.clear();
            }
            else {

                barrier.updateState(-array.size() * Gdx.graphics.getDeltaTime());
            }

            if (barrier.getState() / barrier.TimeToDemolishBarrier == 1.0f && map.get((int) barrier.position.x, (int) barrier.position.y) != Map.SpawnIntact){

                map.set((int) barrier.position.x, (int) barrier.position.y, Map.SpawnIntact);
            }
            else if ((1.0f/3.0f < barrier.getState() / barrier.TimeToDemolishBarrier && barrier.getState() / barrier.TimeToDemolishBarrier < 2.0f/3.0f) && map.get((int) barrier.position.x, (int) barrier.position.y) != Map.SpawnSlightlyBroken) {

                map.set((int) barrier.position.x, (int) barrier.position.y, Map.SpawnSlightlyBroken);

                //spawnFallingStones(barrier.position);
            }
            else if ((barrier.getState() / barrier.TimeToDemolishBarrier) < 1.0f/3.0f && map.get((int) barrier.position.x, (int) barrier.position.y) != Map.SpawnBroken) {

                map.set((int) barrier.position.x, (int) barrier.position.y, Map.SpawnBroken);

                //spawnFallingStones(barrier.position);
            }

        });

        batch.setProjectionMatrix(player.camera.combined);

        batch.begin();

        map.playerPosition = player.getPosition();

        map.draw(batch);

        Iterator<GameObject> iterator = gameObjects.iterator();

        while (iterator.hasNext()) {

            GameObject gameObject = iterator.next();

            gameObject.tick();

            if (gameObject instanceof Drawable) {

                if (!(gameObject instanceof Player)) {

                    ((Drawable) gameObject).draw(batch, gameObject.getPosition(), gameObject.getSize());
                }
            }

            if (gameObject instanceof Entity) {

                Entity entity = (Entity) gameObject;

                if (entity.health < 0) {

                    iterator.remove();
                }

                if (gameObject instanceof Player) {

                    boolean closeToBarrier = false;

                    for (Barrier barrier : barriers) {

                        tmp.set(gameObject.getPosition().x - barrier.position.x * Map.TileSizeInPixelsInWorldSpace, gameObject.getPosition().y - barrier.position.y * Map.TileSizeInPixelsInWorldSpace);

                        // System.out.println(barrier.getState());

                        if (tmp.len() / Map.TileSizeInPixelsInWorldSpace < distanceToBarrierInOrderToRebuild && barrier.getState() < barrier.TimeToDemolishBarrier) {

                            closeToBarrier = true;

                            closestsBarrier = barrier;
                            button.visible = true;

                            break;
                        }
                    }

                    if (!closeToBarrier) {

                        closestsBarrier = null;
                        button.visible = false;
                    }
                }
            }
        }

        player.draw(batch, player.getPosition(), player.getSize());

        batch.end();


        // FRAME

        frame.tick();
    }

    private int distanceToBarrierInOrderToRebuild = 2;
    private Barrier closestsBarrier = null;


    @Override
    public void dispose() {

        super.dispose();

        player.dispose();
    }
}

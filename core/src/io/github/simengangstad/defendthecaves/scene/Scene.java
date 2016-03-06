package io.github.simengangstad.defendthecaves.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ai.fsm.StackStateMachine;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import io.github.simengangstad.defendthecaves.Callback;
import io.github.simengangstad.defendthecaves.Container;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.GameObject;
import io.github.simengangstad.defendthecaves.pathfinding.PathfindingGrid;
import io.github.simengangstad.defendthecaves.procedural.MapGenerator;
import io.github.simengangstad.defendthecaves.scene.entities.*;
import io.github.simengangstad.defendthecaves.scene.items.*;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * @author simengangstad
 * @since 20/12/15
 */
public class Scene extends Container {

    /**
     * The map that hosts the game objects.
     */
    private Map map;

    /**
     * The shader of the scene.
     */
    private LightShader lightShader;

    /**
     * The lights in the scene.
     */
    private ArrayList<Light> lights = new ArrayList<>();

    /**
     * The pathfinding grid used for enemy AI.
     */
    public PathfindingGrid pathfindingGrid;

    /**
     * The player which interacts with the map.
     */
    public Player player;

    /**
     * The barriers; where the enemies spawn.
     */
    private Barrier[] barriers;

    /**
     * A list of the amount of enemies behind each barrier breaking the barrier down.
     */
    private final HashMap<Barrier, ArrayList<Enemy>> enemiesAtHold = new HashMap<>();

    /**
     * A reference to the keys which open the locked rooms in the map.
     */
    private ArrayList<Key> keys = new ArrayList<>();

    /**
     * The maximum amount one can zoom out of the map.
     */
    private final int MaxZoom = 400;

    /**
     * Tmp
     */
    private final Vector2 tmp = new Vector2();

    private int distanceToBarrierInOrderToRebuild = 2;
    private Barrier closestsBarrier = null;

    /**
     * Instantiates the scene with a player.
     */
    public Scene(Player player) {

        super();

        // TODO: Why must we do this here as well???
        Gdx.input.setInputProcessor(inputMultiplexer);

        this.player = player;

        initialiseMap();

        lightShader = new LightShader(Map.TileSizeInPixelsInWorldSpace / Game.SizeOfTileInPixelsInSpritesheet / 2, map.getWidth(), map.getHeight());

        batch.setShader(lightShader.handle);

        lightShader.setAmbientColour(1.1f, 1.1f, 1.1f);

        lightShader.updateLights(lights.size());

        lightShader.handle.begin();
        lightShader.updateMap(map);
        lightShader.handle.end();
        batch.setShader(lightShader.handle);
        recompileShader = false;
    }

    /**
     * Adds random loot to a room. The total amount of added loot will be
     * the amount of random loot plus the required items.
     *
     * @param amountOfRandomLoot The amount of loot to be added.
     * @param requiredItems The required items which will be added regardless.
     */
    private void addRandomLootInRoom(MapGenerator.Room room, int amountOfRandomLoot, Item... requiredItems) {

        System.out.println("\n ---- Adding random loot ----");

        for (int i = 0; i < amountOfRandomLoot + requiredItems.length; i++) {

            Item itemToAdd;

            Vector2 position = new Vector2(

                    MathUtils.random((float) (room.centreX - Math.floor((room.width - 2) / 2.0f) + 0.5f), (float) (room.centreX + Math.ceil((room.width - 2) / 2.0f) - 0.5f)),
                    MathUtils.random((float) (room.centreY - Math.floor((room.height - 2) / 2.0f) + 0.5f), (float) (room.centreY + Math.ceil((room.height - 2) / 2.0f) - 0.5f))
            ).scl(Map.TileSizeInPixelsInWorldSpace);

            if (i < requiredItems.length) {

               itemToAdd = requiredItems[i];
            }
            else {

                Potion potion = new Potion(position);

                for (int j = 0; j < MathUtils.random(1, 5); j++) {

                    potion.addChemical(new Chemical());
                }

                itemToAdd = potion;
            }

            System.out.println("Placing random loot (" + itemToAdd + ") at position: " + "(" + (position.x / Map.TileSizeInPixelsInWorldSpace) + ", " + (position.y / Map.TileSizeInPixelsInWorldSpace) + ")" + " inside room: " + room);

            itemToAdd.position.set(position);
            itemToAdd.map = map;

            addGameObject(itemToAdd);
        }

        System.out.println("---- Finished adding random loot ----\n");
    }

    private void initialiseMap() {

        map = new Map(30, 30, 3, 5, 11, 323123, player.size, 1, 3);

        map.changeCallback = () -> {

            if (!batch.isDrawing()) {

                lightShader.handle.begin();
            }

            lightShader.updateMap(map);

            if (!batch.isDrawing()) {

                batch.getShader().end();
            }
        };

        pathfindingGrid = new PathfindingGrid(map.getWidth(), map.getHeight());

        updatePathfindingGrid();

        addGameObject(player);

        for (MapGenerator.Room room : map.getRooms()) {

            if (room.isLocked()) {

                keys.add(room.getKey());

                addRandomLootInRoom(room, (int) Math.sqrt((room.width - 2) * (room.height - 2)));
            }
        }

        boolean spawnPositionFound = false;

        while (!spawnPositionFound) {

            for (MapGenerator.Room room : map.getRooms()) {

                if (!room.isLocked()) {

                    for (int x = room.centreX - ((room.width - 2) / 2); x < room.centreX + ((room.width - 2) / 2); x++) {

                        for (int y = room.centreY - ((room.height - 2) / 2); y < room.centreY + ((room.height - 2) / 2); y++) {

                            if (!map.isSolid(x, y) && MathUtils.random(100) < 50) {

                                player.position.set(x * map.TileSizeInPixelsInWorldSpace + player.size.x / 2.0f, y * map.TileSizeInPixelsInWorldSpace + player.size.y / 2.0f);
                                player.updateCameraPosition();

                                spawnPositionFound = true;

                                break;
                            }
                        }

                        if (spawnPositionFound) {

                            break;
                        }
                    }
                }
            }
        }

        Vector2[] spawnPoints = map.getSpawnPoints();

        barriers = new Barrier[spawnPoints.length];

        for (int i = 0; i < barriers.length; i++) {

            barriers[i] = new Barrier(spawnPoints[i], map, player);

            stage.addActor(barriers[i].progressBar);

            enemiesAtHold.put(barriers[i], new ArrayList<>());
        }

        Spawner<Barrier> spawner = new Spawner<>(barriers);

        spawner.spawn(1, 4000, item -> {

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

            Vector2 positionOfEnemy = position.cpy().scl(Map.TileSizeInPixelsInWorldSpace);

            Enemy enemyToAdd = null;

            switch (MathUtils.random(2)) {

                case 0:

                    enemyToAdd = new HumanLikeEnemy(positionOfEnemy, new Vector2(Game.EntitySize, Game.EntitySize), 6, player);

                    break;

                case 1:

                    enemyToAdd = new Snake(positionOfEnemy, new Vector2(Game.EntitySize * 2, Game.EntitySize), 4, player);

                    break;

                case 2:

                    enemyToAdd = new Caterpillar(positionOfEnemy, new Vector2(Game.EntitySize, Game.EntitySize), 5, player, gameObjects);

                    break;
            }

            Potion potion = new Potion(positionOfEnemy);

            for (int j = 0; j < MathUtils.random(1, 5); j++) {

                potion.addChemical(new Chemical());
            }

            enemyToAdd.map = this.map;

            enemyToAdd.addItem(potion);

            if (!keys.isEmpty()) {

                Key key = keys.get(keys.size() - 1);

                key.map = this.map;

                enemyToAdd.addItem(key);
            }

            enemiesAtHold.get(barrier).add(enemyToAdd);
        });
     }

    private void updatePathfindingGrid() {

        for (int x = 0; x < pathfindingGrid.width; x++) {

            for (int y = 0; y < pathfindingGrid.height; y++) {

                pathfindingGrid.set(x, y, map.isSolid(x, y) ? PathfindingGrid.State.Closed : PathfindingGrid.State.Open);
            }
        }
    }

    @Override
    public void resize(int width, int height) {

        super.resize(width, height);

        player.camera.viewportWidth = Gdx.graphics.getWidth();
        player.camera.viewportHeight = Gdx.graphics.getHeight();
        player.camera.update();

        lightShader.handle.begin();
        lightShader.uploadUniforms(lights);
        lightShader.handle.end();
    }

    @Override
    public void addGameObject(GameObject gameObject) {

        if (gameObject instanceof Collidable) {

            ((Collidable) gameObject).map = map;
        }

        if (gameObject instanceof Item) {

            ((Item) gameObject).toggleTimer();
            ((Item) gameObject).map = map;
        }

        super.addGameObject(gameObject);
    }

    @Override
    public void removeGameObject(GameObject gameObject) {

        super.removeGameObject(gameObject);

        if (gameObject instanceof Item) {

            ((Item) gameObject).toggleTimer();
        }
    }

    private boolean recompileShader = false;

    public void addLight(Light light) {

        lights.add(light);

        recompileShader = true;
    }

    public void removeLight(Light light) {

        lights.remove(light);

        recompileShader = true;
    }

    /**
     * Damages the entities inside the rect besides the excludables.
     */
    public void damage(int attackDamage, Vector2 attackDirection, float x, float y, float width, float height, Entity excludable) {

        for (GameObject gameObject : gameObjects) {

            // Prevent enemies from hitting enemies
            if (excludable.getClass() == Enemy.class && gameObject.getClass() == Enemy.class) {

                continue;
            }
            if (gameObject instanceof Entity) {


                // Enemies don't hurt each other.
                if (gameObject instanceof Enemy && excludable instanceof Enemy) {

                    return;
                }

                Entity entity = (Entity) gameObject;

                if (entity == excludable ) {

                    continue;
                }

                if (intersect(x, y, width, height, entity.position.x, entity.position.y, entity.size.x, entity.size.y)) {

                    if (entity.currentItem instanceof Shield && entity.flip() != excludable.flip()) {

                        break;
                    }

                    entity.takeDamage(attackDamage);

                    entity.paralyse();

                    entity.applyForce(attackDirection.nor().scl(3.0f), false, 0.0f);

                    System.out.println("Damage applied to entity: " + entity + " - current health: " + entity.health);

                    break;
                }
            }
        }
    }

    /**
     * Checks intersection between two rectangles with a centre and half size.
     */
    public boolean intersect(float x1, float y1, float w1, float h1, float x2, float y2, float w2, float h2) {

        if(Math.abs(x1 - x2) < w1 / 2.0f + w2 / 2.0f) {

            if(Math.abs(y1 - y2) < h1 / 2.0f + h2 / 2.0f) {

                return true;
            }
        }

        return false;
    }

    public float scaleFactor = 1.0f;

    @Override
    public boolean scrolled(int amount) {

        scaleFactor += amount;

        scaleFactor = Math.round(MathUtils.clamp(scaleFactor, 1.0f, MaxZoom));

        player.camera.zoom = Math.max(scaleFactor / 4.0f, 1.0f);

        return true;
    }

    @Override
    /**
     * Updates the state of the scene.
     */
    public void tick() {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (recompileShader) {

            lightShader.handle.begin();
            lightShader.updateLights(lights.size());
            lightShader.handle.end();

            batch.setShader(lightShader.handle);

            recompileShader = false;

            System.out.println("Recompiling shader!");
        }

        if (!buffer.isEmpty()) {

            gameObjects.addAll(buffer);

            buffer.clear();
        }

        if (!removeBuffer.isEmpty()) {

            gameObjects.removeAll(removeBuffer);

            removeBuffer.clear();
        }


        if (player.inRangeOfBarrier && Gdx.input.isKeyPressed(Input.Keys.F)) {

            if (!rebuilding) {

                List<Object> rocks = new ArrayList<>();

                player.inventory.getAllItemsByType(Rock.class, rocks);

                // If player has stones, then update state...
                if (rocks.size() > 0) {

                    rebuilding = true;
                }
                else {

                    player.displayMessage("Bollocks, no rocks left...", 2.0f);
                }
            }
            else {

                rebuildingTimer += Gdx.graphics.getDeltaTime();
                totalTimer += Gdx.graphics.getDeltaTime();

                if (interval * closestsBarrier.TimeToDemolishBarrier <= rebuildingTimer || closestsBarrier.TimeToDemolishBarrier <= totalTimer) {

                    rebuilding = false;

                    rebuildingTimer = 0.0f;

                    boolean obtained = false;

                    for (int x = 0; x < player.inventory.columns; x++) {

                        for (int y = 0; y < player.inventory.rows; y++) {

                            List<Item> items = player.inventory.getItemList(x, y);

                            if (items.isEmpty()) {

                                continue;
                            }

                            if (items.get(0) instanceof Rock) {

                                player.inventory.obtainItem(x, y, 1);

                                closestsBarrier.lastState += closestsBarrier.TimeToDemolishBarrier / 3.0f;

                                obtained = true;

                                break;
                            }
                        }

                        if (obtained) {

                            break;
                        }
                    }
                }

                closestsBarrier.updateState(Gdx.graphics.getDeltaTime());
            }
        }
        else {

            if (closestsBarrier != null && rebuildingTimer != 0.0f) {

                closestsBarrier.setState(closestsBarrier.lastState);
            }

            totalTimer = 0.0f;
            rebuildingTimer = 0.0f;
            rebuilding = false;
        }

        enemiesAtHold.forEach((barrier, array) -> {

            if (barrier.getState() <= 0) {

                array.forEach((enemy -> addGameObject(enemy)));

                array.clear();
            }
            else {

                if (array.size() > 0) {

                    barrier.updateState(-array.size() * Gdx.graphics.getDeltaTime());
                    barrier.lastState = barrier.getState();
                }
            }

            barrier.tick();

            if (barrier.getState() / barrier.TimeToDemolishBarrier == 1.0f && map.get((int) barrier.position.x, (int) barrier.position.y) != Map.SpawnIntact) {

                map.set((int) barrier.position.x, (int) barrier.position.y, Map.SpawnIntact);
            }
            else if ((1.5f/3.0f <= barrier.getState() / barrier.TimeToDemolishBarrier && barrier.getState() / barrier.TimeToDemolishBarrier < 3.0f/3.0f) && map.get((int) barrier.position.x, (int) barrier.position.y) != Map.SpawnSlightlyBroken) {

                map.set((int) barrier.position.x, (int) barrier.position.y, Map.SpawnSlightlyBroken);

                System.out.println("Barrier (" + barrier + ") is slightly broken.");
            }
            else if ((barrier.getState() / barrier.TimeToDemolishBarrier) <= 0.0f && map.get((int) barrier.position.x, (int) barrier.position.y) != Map.SpawnBroken) {

                map.set((int) barrier.position.x, (int) barrier.position.y, Map.SpawnBroken);

                System.out.println("Barrier (" + barrier + ") is broken.");
            }

        });

        batch.setProjectionMatrix(player.camera.combined);

        batch.begin();

        lightShader.uploadUniforms(lights);

        map.playerPosition = player.position;

        map.draw(batch, scaleFactor);

        player.draw(batch);

        boolean inRangeOfPlacedItem = false;

        for (Iterator<GameObject> iterator = gameObjects.iterator(); iterator.hasNext();) {

            GameObject gameObject = iterator.next();

            gameObject.tick();

            if (!(gameObject instanceof Player)) {

                gameObject.draw(batch);
            }

            if (gameObject instanceof Entity) {

                Entity entity = (Entity) gameObject;

                if (entity.health == 0) {

                    if (entity instanceof Player) {

                        // TODO: Game over

                        //continue;
                    }

                    entity.die();

                    // TODO: Temp, will be removed after die animation has been played.
                    iterator.remove();

                    continue;
                }

                if (gameObject instanceof Player) {

                    boolean closeToBarrier = false;

                    for (Barrier barrier : barriers) {

                        barrier.progressBar.setVisible(false);

                        tmp.set(gameObject.position.x / Map.TileSizeInPixelsInWorldSpace - barrier.position.x, gameObject.position.y / Map.TileSizeInPixelsInWorldSpace - barrier.position.y);

                        if (tmp.len() < distanceToBarrierInOrderToRebuild && barrier.getState() < barrier.TimeToDemolishBarrier) {

                            closeToBarrier = true;

                            closestsBarrier = barrier;
                            closestsBarrier.progressBar.setVisible(true);

                            player.displayMessage("If only I had some stones to block out those bastards (f).");

                            player.inRangeOfBarrier = true;

                            break;
                        }
                    }

                    if (!closeToBarrier) {

                        closestsBarrier = null;
                        player.inRangeOfBarrier = false;
                    }
                }
            }
            else if (gameObject instanceof Item) {

                Item item = ((Item) gameObject);

                if (!item.isPlaced()) {

                    for (GameObject gameObjectToCheckAgainst : gameObjects) {

                        if (gameObjectToCheckAgainst instanceof Entity) {

                            Entity entity = ((Entity) gameObjectToCheckAgainst);

                            if (item.isThrown()) {

                                if (entity == item.thrownFrom) {

                                    continue;
                                }

                                if (entity.intersects(item)) {

                                    item.collides(entity);
                                }
                            }
                            else {

                                float length = map.lengthBetweenCoordinates(item.position, entity.position);

                                if (item.getTimer() > 0.75f) {

                                    if (length < 0.5) {

                                        item.forceApplied.set(0.0f, 0.0f);

                                        System.out.println("Item timer: " + item.getTimer());

                                        if (entity.inventory.sufficientPlaceForItem(item)) {

                                            entity.addItem(item);

                                            System.out.println("Entity (" + entity + ") picked up item: " + item);

                                            removeGameObject(item);
                                        }
                                    }
                                    else if (length < 2) {

                                        if (entity.inventory.sufficientPlaceForItem(item) && item.forceApplied.x == 0.0f && item.forceApplied.y == 0.0f) {

                                            System.out.println("Item (" + item + ") in range of entity (" + entity + "), applying force.");

                                            Vector2 vector = Game.vector2Pool.obtain();

                                            vector.set(entity.position).sub(item.position);
                                            vector.scl(0.05f);

                                            item.applyForce(vector, false, 0.0f);

                                            Game.vector2Pool.free(vector);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                else {

                    float length = map.lengthBetweenCoordinates(item.position, player.position);

                    if (length < 1.25f) {

                        inRangeOfPlacedItem = true;

                        player.displayMessage("This might come handy (f)");

                        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {

                            if (player.inventory.sufficientPlaceForItem(item)) {

                                player.addItem(item);

                                System.out.println("Entity (" + player + ") picked up item: " + item);

                                removeGameObject(item);
                            }
                            else {

                                player.displayMessage("Darn it, not enough place in my rucksac...", 2.0f);
                            }
                        }
                    }
                }
            }
        }

        player.inRangeOfStationaryItem = inRangeOfPlacedItem;

        batch.end();

        stage.draw();
    }

    float totalTimer = 0.0f;
    float rebuildingTimer = 0.0f;
    final float interval = 0.5f;
    boolean rebuilding = false;

    @Override
    public void dispose() {

        super.dispose();

        player.dispose();
    }
}

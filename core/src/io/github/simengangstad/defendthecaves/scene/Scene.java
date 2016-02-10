package io.github.simengangstad.defendthecaves.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.defendthecaves.Container;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.components.Drawable;
import io.github.simengangstad.defendthecaves.components.GameObject;
import io.github.simengangstad.defendthecaves.gui.KeyButton;
import io.github.simengangstad.defendthecaves.gui.View;
import io.github.simengangstad.defendthecaves.pathfinding.PathfindingGrid;
import io.github.simengangstad.defendthecaves.procedural.MapGenerator;
import io.github.simengangstad.defendthecaves.scene.entities.*;
import io.github.simengangstad.defendthecaves.scene.item.Chemical;
import io.github.simengangstad.defendthecaves.scene.item.Key;
import io.github.simengangstad.defendthecaves.scene.item.Potion;
import io.github.simengangstad.defendthecaves.scene.tool.Cudgel;
import io.github.simengangstad.defendthecaves.scene.tool.Shield;

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
     * The container which displays the inventory of the player.
     */
    private Container inventoryFrame = new Container();

    /**
     * The container which displays the different interaction widgets and the labels for the scene.
     */
    private Container widgetFrame = new Container();

    /**
     * A button, this will be renamed later.
     */
    private KeyButton button;

    /**
     * The maximum amount one can zoom out of the map.
     */
    private final int MaxZoom = 60;

    /**
     * Tmp
     */
    private final Vector2 tmp = new Vector2();

    /**
     * Instantiates the scene with a player.
     */
    public Scene(Player player) {

        this.player = player;

        initialiseMap();
        initialiseWidgetFrame();
        initialiseInventoryFrame();

        ShaderProgram shaderProgram = new ShaderProgram(Gdx.files.internal("assets/shaders/shader.vs"), Gdx.files.internal("assets/shaders/shader.fs"));

        if (!shaderProgram.isCompiled()) {

            System.err.println("Couldn't compile shader: " + shaderProgram.getLog());
        }

        batch.setShader(shaderProgram);
    }

    private void initialiseInventoryFrame() {

        int width = 700, height = 400;

        View inventoryView = new View(new Vector2(Gdx.graphics.getWidth() / 2 - width / 2, Gdx.graphics.getHeight() / 2 - height / 2), new Vector2(width, height));

        width = 500;
        height = 300;

        player.inventory = new Inventory(

                new Vector2(inventoryView.getSize().x / 2 - width / 2, inventoryView.getSize().y / 2 - height / 2),
                new Vector2(width, height),
                6,
                4,
                new TextureRegion(Game.SpriteSheet, 80, 160, 16, 16),
                new TextureRegion(Game.SpriteSheet, 96, 160, 16, 16)
        );

        player.inventory.placeItem(keys.get(keys.size() - 1));

        inventoryView.addSubview(player.inventory);
        inventoryFrame.addGameObject(inventoryView);
    }

    private void initialiseWidgetFrame() {

        button = new KeyButton(new Vector2(), new Vector2(32, 32), new TextureRegion(Game.GUISheet, 0, 0, 16, 16), Input.Keys.G) {

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

        widgetFrame.addGameObject(button);
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

                    MathUtils.random(room.centreX - (room.width - 2) / 2 + 0.5f, room.centreX + (room.width - 2) / 2 - 0.5f),
                    MathUtils.random(room.centreY - (room.height - 2) / 2 + 0.5f, room.centreY + (room.height - 2) / 2 - 0.5f)
            );

            if (i < requiredItems.length) {

               itemToAdd = requiredItems[i];
            }
            else {

                Potion potion = new Potion(position,

                        new Vector2(Game.PotionSize, Game.PotionSize)
                );

                for (int j = 0; j < MathUtils.random(1, 5); j++) {

                    potion.addChemical(new Chemical());
                }

                itemToAdd = potion;
            }

            System.out.println("Placing random loot (" + itemToAdd + ") at position: " + position + " inside room: " + room);

            position.scl(Map.TileSizeInPixelsInWorldSpace);

            itemToAdd.setMap(map);

            addGameObject(itemToAdd);
        }

        System.out.println("---- Finished adding random loot ----\n");
    }

    private void initialiseMap() {

        map = new Map(25, 25, 5, 3, 15, 162, player.getSize(), 1, 3);

        pathfindingGrid = new PathfindingGrid(map.getWidth(), map.getHeight());

        updatePathfindingGrid();

        player.host = this;
        player.setMap(map);

        addGameObject(player);

        for (MapGenerator.Room room : map.getRooms()) {

            if (room.isLocked()) {

                keys.add(room.getKey());

                addRandomLootInRoom(room, (int) Math.sqrt((room.width - 2) * (room.height - 2)));
            }
        }

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

            //position.add(0.5f, 0.5f);

            Vector2 positionOfEnemy = position.cpy().scl(Map.TileSizeInPixelsInWorldSpace);

            Enemy enemyToAdd = null;

            switch (MathUtils.random(2)) {

                case 0:

                    enemyToAdd = new HumanLikeEnemy(positionOfEnemy, new Vector2(Game.EntitySize, Game.EntitySize), 6, player);

                    enemyToAdd.attachTool(new Cudgel(() -> {}));

                    break;

                case 1:

                    enemyToAdd = new Snake(position, new Vector2(Game.EntitySize * 2, Game.EntitySize), 4, player);

                    break;

                case 2:

                    enemyToAdd = new Caterpillar(positionOfEnemy, new Vector2(Game.EntitySize, Game.EntitySize), 5, player, gameObjects);

                    break;
            }

            enemyToAdd.inventory = new Inventory(null, null, 6, 4, null, null);

            Potion potion = new Potion(position,

                    new Vector2(Game.PotionSize, Game.PotionSize)
            );

            for (int j = 0; j < MathUtils.random(1, 5); j++) {

                potion.addChemical(new Chemical());
            }

            enemyToAdd.inventory.placeItem(potion);

            if (!keys.isEmpty()) {

                //enemyToAdd.inventory.placeItem(keys.get(keys.size() - 1));
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

    /**
     * Updates the viewport and the projection matrix of the player's camera.
     */
    public void updateMatrices() {

        player.camera.viewportWidth = Gdx.graphics.getWidth();
        player.camera.viewportHeight = Gdx.graphics.getHeight();
        player.camera.update();

        widgetFrame.setProjectionMatrix(widgetFrame.getProjectionMatrix().setToOrtho(0.0f, Gdx.graphics.getWidth(), 0.0f, Gdx.graphics.getHeight(), -1.0f, 1.0f));
        inventoryFrame.setProjectionMatrix(inventoryFrame.getProjectionMatrix().setToOrtho(0.0f, Gdx.graphics.getWidth(), 0.0f, Gdx.graphics.getHeight(), -1.0f, 1.0f));
    }

    @Override
    public void addGameObject(GameObject gameObject) {

        super.addGameObject(gameObject);

        if (gameObject instanceof Collidable) {

            ((Collidable) gameObject).setMap(map);
        }

        if (gameObject instanceof Item) {

            ((Item) gameObject).toggleTimer();
        }
    }

    @Override
    public void removeGameObject(GameObject gameObject) {

        super.removeGameObject(gameObject);

        if (gameObject instanceof Item) {

            ((Item) gameObject).toggleTimer();
        }
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

                if (intersect(x, y, width, height, entity.getPosition().x, entity.getPosition().y, entity.getSize().x, entity.getSize().y)) {

                    if (entity.currentTool instanceof Shield && entity.flip() != excludable.flip()) {

                        break;
                    }

                    entity.takeDamage(attackDamage);

                    entity.paralyse();

                    entity.applyForce(attackDirection.nor().scl(7.5f));

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

        if (!buffer.isEmpty()) {

            gameObjects.addAll(buffer);

            buffer.clear();
        }

        if (!removeBuffer.isEmpty()) {

            gameObjects.removeAll(removeBuffer);

            removeBuffer.clear();
        }

        enemiesAtHold.forEach((barrier, array) -> {

            if (barrier.getState() == 0) {

                array.forEach((enemy -> addGameObject(enemy)));

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

        map.draw(batch, scaleFactor);

        player.draw(batch, player.getPosition(), player.getSize());

        for (Iterator<GameObject> iterator = gameObjects.iterator(); iterator.hasNext();) {

            GameObject gameObject = iterator.next();

            gameObject.tick();

            if (gameObject instanceof Drawable) {

                if (!(gameObject instanceof Player)) {

                    ((Drawable) gameObject).draw(batch, gameObject.getPosition(), gameObject.getSize());
                }
            }

            if (gameObject instanceof Entity) {

                Entity entity = (Entity) gameObject;

                if (entity.health == 0) {

                    if (entity instanceof Player) {

                        // TODO: Game over

                        continue;
                    }

                    entity.die();

                    // TODO: Temp, will be removed after die animation has been played.
                    iterator.remove();

                    continue;
                }

                if (gameObject instanceof Player) {

                    boolean closeToBarrier = false;

                    for (Barrier barrier : barriers) {

                        tmp.set(gameObject.getPosition().x - barrier.position.x * Map.TileSizeInPixelsInWorldSpace, gameObject.getPosition().y - barrier.position.y * Map.TileSizeInPixelsInWorldSpace);

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
            else if (gameObject instanceof Item) {

                Item item = ((Item) gameObject);

                for (GameObject gameObjectToCheckAgainst : gameObjects) {

                    if (gameObjectToCheckAgainst instanceof Entity) {

                        Entity entity = ((Entity) gameObjectToCheckAgainst);

                        float length = map.lengthBetweenCoordinates(item.getPosition(), entity.getPosition());

                        if (length < 0.5) {

                            item.forceApplied.set(0.0f, 0.0f);

                            //System.out.println("Item timer: " + item.getTimer());

                            if (item.getTimer() > 0.75f) {

                                if (entity.inventory.placeItem(item)) {

                                    System.out.println("Entity (" + entity + ") picked up item: " + item);

                                    removeGameObject(item);
                                }
                            }
                        }
                        else if (length < 2) {

                            if (entity.inventory.sufficientPlaceForItem(item) && item.forceApplied.x == 0.0f && item.forceApplied.y == 0.0f) {

                                System.out.println("Applying force to item: " + item);

                                item.applyForce(entity.getPosition().cpy().sub(item.getPosition()).scl(2.0f));
                            }
                        }
                    }
                }
            }
        }

        batch.end();

        widgetFrame.tick();
        inventoryFrame.tick();
    }

    private int distanceToBarrierInOrderToRebuild = 2;
    private Barrier closestsBarrier = null;


    @Override
    public void dispose() {

        super.dispose();

        player.dispose();
    }
}

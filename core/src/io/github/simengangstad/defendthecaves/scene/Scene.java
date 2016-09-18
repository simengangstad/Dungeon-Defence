package io.github.simengangstad.defendthecaves.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import io.github.simengangstad.defendthecaves.Container;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.GameObject;
import io.github.simengangstad.defendthecaves.audio.Jukebox;
import io.github.simengangstad.defendthecaves.pathfinding.PathfindingGrid;
import io.github.simengangstad.defendthecaves.procedural.MapGenerator;
import io.github.simengangstad.defendthecaves.scene.entities.Enemy;
import io.github.simengangstad.defendthecaves.scene.entities.Player;
import io.github.simengangstad.defendthecaves.scene.gui.Pointer;
import io.github.simengangstad.defendthecaves.scene.gui.SlotItem;
import io.github.simengangstad.defendthecaves.scene.gui.SpeechBubble;
import io.github.simengangstad.defendthecaves.scene.items.*;
import io.github.simengangstad.defendthecaves.StartScreen;

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
    public Map map;

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
    private Player player;

    /**
     * The barriers; where the enemies spawn.
     */
    private Barrier[] barriers;

    /**
     * A list of the amount of enemies behind each barrier breaking the barrier down.
     */
    private HashMap<Barrier, ArrayList<Enemy>> enemiesAtHold = new HashMap<>();

    /**
     * A reference to the keys which open the locked rooms in the map.
     */
    private ArrayList<Key> keys = new ArrayList<>();

    /**
     * The maximum amount one can zoom out of the map.
     */
    private final int MaxZoom = 400;

    final WaveSystem waveSystem;

    /**
     * Tmp
     */
    private final Vector2 tmp = new Vector2();

    private int distanceToBarrierInOrderToRebuild = 2;

    private Barrier closestsBarrier = null;

    private Vector2 screenShake = new Vector2();

    private float shakeTime = 0.0f;

    private int shakeIntensity = 0;

    /**
     * Stage for items that are supposedly in the scene (health bars etc.)
     */
    public Stage sceneStage = new Stage();

    private Pointer pointer;

    private boolean resetted = false;

    public boolean justUnfrozen = false;

    private boolean freeze = false;

    private int score = 0;

    private Label scoreLabel = new Label("", Game.LabelStyle16);

    private SpeechBubble dieLabel = new SpeechBubble();

    private TextButton exitButton = new TextButton("Exit", Game.UISkin);
    private TextButton backButton = new TextButton("Back", Game.UISkin);

    private Jukebox jukebox = new Jukebox();

    /**
     * Used to make sure that a new track from the scary group is only requested once when the
     * new wave is deployed.
     */
    private int jukeboxWaveCount = 0;

    /**
     * Instantiates the scene with a player.
     */
    public Scene() {

        super();

        // TODO: Why must we do this here as well???
        Gdx.input.setInputProcessor(inputMultiplexer);

        this.player = new Player(new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        Gdx.input.setCursorCatched(true);

        dieLabel.setText("Dammit, you died! Press enter to try again.");
        dieLabel.setVisible(false);
        dieLabel.setWidth(200.0f);

        scoreLabel.setPosition(10, Gdx.graphics.getHeight() - 20);

        exitButton.setHeight(50.0f);
        exitButton.setWidth(100.0f);
        exitButton.setVisible(false);
        exitButton.setPosition(Gdx.graphics.getWidth() / 2.0f - exitButton.getWidth() / 2.0f, Gdx.graphics.getHeight() / 2.0f - exitButton.getHeight() / 2.0f - 100);


        backButton.setHeight(50.0f);
        backButton.setWidth(100.0f);
        backButton.setVisible(false);
        backButton.setPosition(Gdx.graphics.getWidth() / 2.0f - backButton.getWidth() / 2.0f, Gdx.graphics.getHeight() / 2.0f - backButton.getHeight() / 2.0f - 50);

        jukebox.addMusicToGroup("normal", Gdx.audio.newMusic(Gdx.files.internal("assets/music/Normal/Black Sapphire.mp3")));
        jukebox.addMusicToGroup("normal", Gdx.audio.newMusic(Gdx.files.internal("assets/music/Normal/Chimera.mp3")));
        jukebox.addMusicToGroup("normal", Gdx.audio.newMusic(Gdx.files.internal("assets/music/Normal/After The Fall.mp3")));

        jukebox.addMusicToGroup("calm", Gdx.audio.newMusic(Gdx.files.internal("assets/music/Calm/Wood Elves.mp3")));
        jukebox.addMusicToGroup("calm", Gdx.audio.newMusic(Gdx.files.internal("assets/music/Calm/Homecoming.mp3")));

        jukebox.addMusicToGroup("high", Gdx.audio.newMusic(Gdx.files.internal("assets/music/High/Battle Lines.mp3")));
        jukebox.addMusicToGroup("high", Gdx.audio.newMusic(Gdx.files.internal("assets/music/High/In Pursuit.mp3")));

        jukebox.addMusicToGroup("scary", Gdx.audio.newMusic(Gdx.files.internal("assets/music/Scary/Creepy Hollow.mp3")));
        jukebox.addMusicToGroup("scary", Gdx.audio.newMusic(Gdx.files.internal("assets/music/Scary/Electro Zombies.mp3")));
        jukebox.addMusicToGroup("scary", Gdx.audio.newMusic(Gdx.files.internal("assets/music/Scary/In Doubt.mp3")));
        jukebox.addMusicToGroup("scary", Gdx.audio.newMusic(Gdx.files.internal("assets/music/Scary/Outcast.mp3")));
        jukebox.addMusicToGroup("scary", Gdx.audio.newMusic(Gdx.files.internal("assets/music/Scary/Prairie Dogs.mp3")));

        init();

        waveSystem = new WaveSystem(1, 1, 10, map, player, keys, barriers, enemiesAtHold1 -> this.enemiesAtHold = enemiesAtHold1);
        waveSystem.requestWave();

        lightShader = new LightShader(Map.TileSizeInPixelsInWorldSpace / Game.SizeOfTileInPixelsInSpritesheet / 2, map.getWidth(), map.getHeight());
        lightShader.setAmbientColour(1.0f, 1.0f, 1.0f);
        lightShader.updateLights(lights.size());
        lightShader.handle.begin();
        lightShader.updateMap(map);
        lightShader.handle.end();
        batch.setShader(lightShader.handle);
        recompileShader = false;

        addInputProcessor(sceneStage);

        pointer = new Pointer();
        pointer.size.set(Map.TileSizeInPixelsInWorldSpace / 2.0f, Map.TileSizeInPixelsInWorldSpace / 2.0f);
    }

    private void init() {

        stage.clear();
        sceneStage.clear();

        sceneStage.addActor(dieLabel);
        sceneStage.addActor(exitButton);
        sceneStage.addActor(backButton);
        scoreLabel.setText("Score: 0");
        sceneStage.addActor(scoreLabel);

        if (resetted) {

            for (GameObject gameObject : gameObjects) {

                removeGameObject(gameObject);
            }
        }

        super.clearBuffers();

        score = 0;

        enemiesAtHold.clear();
        keys.clear();
        lights.clear();
        initialiseMap();
        player.map = map;
        addGameObject(player);
        player.initialise();

        if (!resetted) {

            resetted = true;
        }
        else {

            waveSystem.barriers = barriers;
            waveSystem.reset();
            waveSystem.requestWave();
        }

        jukebox.setFadeTime(10);
        jukebox.constructShuffleListFromGroup("normal");
        jukebox.play();
    }

    /**
     * Adds random loot to a room. The total amount of added loot will be
     * the amount of random loot plus the required items.
     *
     * @param amountOfRandomLoot The amount of loot to be added.
     * @param requiredItems      The required items which will be added regardless.
     */
    private void addRandomLootInRoom(MapGenerator.Room room, int amountOfRandomLoot, Item... requiredItems) {

        System.out.println("\n ---- Adding random loot ----");

        for (int i = 0; i < amountOfRandomLoot + requiredItems.length; i++) {

            Item itemToAdd = null;

            Vector2 position = new Vector2(MathUtils.random((float) (room.centreX - Math.floor((room.width - 2) / 2.0f) + 0.5f), (float) (room.centreX + Math.ceil((room.width - 2) / 2.0f) - 0.5f)), MathUtils.random((float) (room.centreY - Math.floor((room.height - 2) / 2.0f) + 0.5f), (float) (room.centreY + Math.ceil((room.height - 2) / 2.0f) - 0.5f))).scl(Map.TileSizeInPixelsInWorldSpace);

            if (i < requiredItems.length) {

                itemToAdd = requiredItems[i];
            }
            else {

                switch (MathUtils.random(5)) {

                    case 0:

                        itemToAdd = new HealthPotion(position);

                        break;

                    case 1:

                        itemToAdd = new ToxicPotion(position);

                        break;

                    case 2:

                        itemToAdd = new ExplosivePotion(position);

                        break;

                    case 3:

                        itemToAdd = new StringItem(position);

                        break;

                    case 4:

                        itemToAdd = new Coal(position);

                        break;

                    case 5:

                        itemToAdd = new Wood(position);

                        break;
                }
            }

            System.out.println("Placing random loot (" + itemToAdd + ") at position: " + "(" + (position.x / Map.TileSizeInPixelsInWorldSpace) + ", " + (position.y / Map.TileSizeInPixelsInWorldSpace) + ")" + " inside room: " + room);

            itemToAdd.position.set(position);
            itemToAdd.map = map;

            addGameObject(itemToAdd);
        }

        System.out.println("---- Finished adding random loot ----\n");
    }

    public void fillLockedRoomsWithLoot(boolean addKeys) {

        for (MapGenerator.Room room : map.getRooms()) {

            if (room.isLocked()) {

                if (addKeys) keys.add(room.getKey());

                addRandomLootInRoom(room, (room.width) * (room.height));
            }
        }
    }

    private void initialiseMap() {

        int width = 20;
        int height = 20;

        map = new Map(width, height, /*(width * height) / (width + height)*/ 3, 3, 7, 757, player.size, 1, 7);

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

        fillLockedRoomsWithLoot(true);

        boolean spawnPositionFound = false;

        while (!spawnPositionFound) {

            for (MapGenerator.Room room : map.getRooms()) {

                if (!room.isLocked()) {

                    for (int x = room.centreX - ((room.width - 2) / 2); x < room.centreX + ((room.width - 2) / 2); x++) {

                        for (int y = room.centreY - ((room.height - 2) / 2); y < room.centreY + ((room.height - 2) / 2); y++) {

                            if (!map.isSolid(x, y) && MathUtils.random(100) < 50) {

                                player.position.set(x * Map.TileSizeInPixelsInWorldSpace + player.size.x / 2.0f, y * map.TileSizeInPixelsInWorldSpace + player.size.y / 2.0f);
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
        }
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

    public void addExplosion(Explosion explosion, GameObject sender) {

        addGameObject(explosion);

        System.out.println("Adding explosion!");

        explosion.setExplosionCallback(() -> {

            damage((int) explosion.intensity, explosion.position, explosion.radius);

            int originX = (int) (explosion.position.x);
            int originY = (int) (explosion.position.y);
            int radius = (int) (explosion.radius);

            Iterator iterator = gameObjects.iterator();

            while (iterator.hasNext()) {

                GameObject gameObject = (GameObject) iterator.next();

                if (!(gameObject instanceof Entity) && !gameObject.equals(sender) && !(gameObject instanceof Explosion)) {

                    float delta = radius + gameObject.size.x / 2.0f;
                    float xs = (gameObject.position.x - originX) * (gameObject.position.x - originX);
                    float ys = (gameObject.position.y - originY) * (gameObject.position.y - originY);

                    if (Math.sqrt(xs + ys) < delta) {

                        /*if (gameObject instanceof Potion) {

                            ((Potion) gameObject).breakPotion();
                        }
                        else if (gameObject instanceof StepTrap) {

                            ((StepTrap) gameObject).step();a
                        }
                        else {

                            removeGameObject(gameObject);
                        }*/

                        if (gameObject instanceof Torch) {

                            removeLight(((Torch) gameObject).light);
                        }

                        removeGameObject(gameObject);
                    }
                }
            }

            for (int x = (originX - radius) / Map.TileSizeInPixelsInWorldSpace; x <= (originX + radius) / Map.TileSizeInPixelsInWorldSpace; x++) {

                for (int y = (originY - radius) / Map.TileSizeInPixelsInWorldSpace; y <= (originY + radius) / Map.TileSizeInPixelsInWorldSpace; y++) {

                    if (map.isBreakable(x, y)) {

                        System.out.println("1: " + x + ", " + y + " - " + map.get(x, y));

                        if (map.get(x, y) == Map.SolidIntact) {

                            map.set(x, y, map.get(x, y) + 2);
                        }
                        else if (map.get(x, y) > Map.SolidIntact) {

                            map.set(x, y, Map.Open);

                            for (int i = 0; i < MathUtils.random(2, 4); i++) {

                                addGameObject(new Rock(new Vector2(x * Map.TileSizeInPixelsInWorldSpace + Map.TileSizeInPixelsInWorldSpace / 2.0f, y * Map.TileSizeInPixelsInWorldSpace + Map.TileSizeInPixelsInWorldSpace / 2.0f)));
                            }
                        }

                        System.out.println("2: " + x + ", " + y + " - " + map.get(x, y));
                    }
                }
            }
        });

        explosion.start();
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

    public void scheduleScreenShake(float shakeTime, int shakeIntensity) {

        this.shakeTime = shakeTime;
        this.shakeIntensity = shakeIntensity;
    }

    /**
     * Damages every entity with the attack damage of the explosion within the radius of the origin.
     */
    public void damage(int attackDamage, Vector2 origin, float radius) {

        Vector2 direction = Game.vector2Pool.obtain();

        for (GameObject gameObject : gameObjects) {

            if (gameObject instanceof Entity) {

                Entity entity = (Entity) gameObject;

                float delta = radius + entity.size.x / 2.0f;
                float xs = (entity.position.x - origin.x) * (entity.position.x - origin.x);
                float ys = (entity.position.y - origin.y) * (entity.position.y - origin.y);
                boolean onRightSide = origin.x < entity.position.x;

                direction.set(entity.position.x - origin.x, entity.position.y - origin.y);

                if (Math.sqrt(xs + ys) < delta) {

                    entity.applyForce(direction.nor().scl(3.0f), false, 0.0f);

                    if (entity.currentItem instanceof Shield && ((entity.flip() && onRightSide) || (!entity.flip() && !onRightSide))) {

                        continue;
                    }

                    float scaleFactor = 1.0f;

                    if (gameObject instanceof Player) {

                        scaleFactor = 1.0f / 3.0f;
                    }

                    entity.takeDamage((int) (attackDamage * scaleFactor));

                    entity.paralyse();

                    System.out.println("Damage" + "(" + attackDamage + ") applied to entity due to explosion: " + entity + " - current health: " + entity.health);
                }
            }
        }

        Game.vector2Pool.free(direction);
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

                if (entity == excludable) {

                    continue;
                }

                if (intersect(x, y, width, height, entity.position.x, entity.position.y, entity.size.x, entity.size.y)) {

                    if (entity.currentItem instanceof Shield && entity.flip() != excludable.flip()) {

                        break;
                    }

                    Axe.Hit.play();

                    entity.takeDamage(attackDamage);

                    entity.paralyse();

                    entity.applyForce(attackDirection.nor().scl(3.0f), false, 0.0f);

                    System.out.println("Damage" + "(" + attackDamage + ") applied to entity: " + entity + " - current health: " + entity.health);

                    break;
                }
            }
        }
    }

    /**
     * Checks intersection between two rectangles with a centre and half size.
     */
    public boolean intersect(float x1, float y1, float w1, float h1, float x2, float y2, float w2, float h2) {

        if (Math.abs(x1 - x2) < w1 / 2.0f + w2 / 2.0f) {

            if (Math.abs(y1 - y2) < h1 / 2.0f + h2 / 2.0f) {

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
        ((OrthographicCamera) sceneStage.getCamera()).zoom = player.camera.zoom;

        return true;
    }

    public void pushEntities(Entity sender, Vector2 origin, float radius, Vector2 direction, float scalar) {

        direction = direction.nor();

        for (GameObject gameObject : gameObjects) {

            if (!(gameObject instanceof Entity) || gameObject == sender) {

                continue;
            }

            Entity entity = (Entity) gameObject;

            if (Math.sqrt((gameObject.position.x - origin.x) * (gameObject.position.x - origin.x) + (gameObject.position.y - origin.y) * (gameObject.position.y - origin.y)) < radius) {

                entity.applyForce(direction.scl(scalar), false, 3.0f);
            }
        }

    }

    private void fetchItems() {

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

                            List<SlotItem> items = player.inventory.getItemList(x, y);

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
    }

    private void updateStateOfBarriers() {

        enemiesAtHold.forEach((barrier, array) -> {

            if (barrier.getState() <= 0) {

                array.forEach(this::addGameObject);

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
            else if ((1.5f / 3.0f <= barrier.getState() / barrier.TimeToDemolishBarrier && barrier.getState() / barrier.TimeToDemolishBarrier < 3.0f / 3.0f) && map.get((int) barrier.position.x, (int) barrier.position.y) != Map.SpawnSlightlyBroken) {

                map.set((int) barrier.position.x, (int) barrier.position.y, Map.SpawnSlightlyBroken);

                System.out.println("Barrier (" + barrier + ") is slightly broken.");
            }
            else if ((barrier.getState() / barrier.TimeToDemolishBarrier) <= 0.0f && map.get((int) barrier.position.x, (int) barrier.position.y) != Map.SpawnBroken) {

                map.set((int) barrier.position.x, (int) barrier.position.y, Map.SpawnBroken);

                System.out.println("Barrier (" + barrier + ") is broken.");
            }

        });
    }

    private void updateStateOfGameObjects() {

        // Behind the entities
        for (GameObject gameObject : gameObjects) {

            if (gameObject instanceof StepTrap) {

                gameObject.draw(batch);

                for (GameObject gameObjectToCheckAgainst : gameObjects) {

                    if (gameObjectToCheckAgainst instanceof Entity) {

                        Entity entity = ((Entity) gameObjectToCheckAgainst);

                        if (entity.intersects((Collidable) gameObject, false)) {

                            ((StepTrap) gameObject).step();
                        }
                    }
                }
            }
        }

        boolean inRangeOfPlacedItem = false;

        for (Iterator<GameObject> iterator = gameObjects.iterator(); iterator.hasNext(); ) {

            GameObject gameObject = iterator.next();

            if (!freeze) {

                if (gameObject instanceof Collidable) {

                    Collidable collidable = (Collidable) gameObject;

                    try {

                        collidable.tick();
                    }
                    catch (RuntimeException runtimeException) {

                        runtimeException.printStackTrace();
                        System.err.println("Map was not set for collidable: " + collidable + ". Setting!");

                        collidable.map = map;
                        collidable.host = this;

                        collidable.tick();
                    }
                }
                else {

                    gameObject.tick();
                }
            }

            if (!(gameObject instanceof Player) && !(gameObject instanceof Explosion) && !(gameObject instanceof StepTrap)) {

                gameObject.draw(batch);
            }

            if (gameObject instanceof Entity) {

                Entity entity = (Entity) gameObject;

                if (entity.health == 0) {

                    if (entity instanceof Player) {

                        dieLabel.setVisible(true);

                        return;
                    }
                    else {

                        score++;

                        scoreLabel.setText("Score: " + score);

                        waveSystem.addDeadEnemy();

                        if (waveSystem.getEnemiesLeft() == 0) {

                            jukebox.requestSongFromGroup("calm");
                        }
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

                            player.displayMessage("If only I had some stones to block out those bastards.");

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
            else if (gameObject instanceof Liquid) {

                if (((Liquid) gameObject).getToxicity() > 0) {

                    for (GameObject go : gameObjects) {

                        if (go instanceof Entity) {

                            if (((Collidable) gameObject).intersects((Collidable) go, false)) {

                                ((Entity) go).takeDamage((int) ((Liquid) gameObject).getToxicity() / 10, 0.5f);
                            }
                        }
                    }
                }
            }
            else if (gameObject instanceof Explosion) {

                continue;
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

                                if (entity.intersects(item, false)) {

                                    item.collides(entity);
                                }
                            }
                            else {

                                if (item.canBePickedUp) {

                                    float length = map.lengthBetweenCoordinates(item.position, entity.position);

                                    if (item.getTimer() > 0.75f) {

                                        if (length < 0.5) {

                                            item.forceApplied.set(0.0f, 0.0f);

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
                }
                else {

                    float length = map.lengthBetweenCoordinates(item.position, player.position);

                    if (length < 1.25f) {

                        inRangeOfPlacedItem = true;

                        player.displayMessage("This might come handy (press f to pick up)");

                        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {

                            if (player.inventory.sufficientPlaceForItem(item)) {

                                player.addItem(item);

                                System.out.println("Entity (" + player + ") picked up item: " + item);

                                removeGameObject(item);
                            }
                            else {

                                player.displayMessage("Darn it, not enough place in my ruck sack...", 2.0f);
                            }
                        }
                    }
                }
            }
        }

        player.inRangeOfStationaryItem = inRangeOfPlacedItem;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        justUnfrozen = false;

        return super.touchUp(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        int mouseX = screenX;
        int mouseY = Math.abs(screenY - (Gdx.graphics.getHeight() - 1));

        if (    exitButton.isVisible() &&
                exitButton.getX() < mouseX && mouseX < exitButton.getX() + exitButton.getWidth() &&
                exitButton.getY() < mouseY && mouseY < exitButton.getY() + exitButton.getHeight()) {

            Game.container = new StartScreen();

            jukebox.stop();

            return true;
        }
        else if (backButton.isVisible() &&
                backButton.getX() < mouseX && mouseX < backButton.getX() + backButton.getWidth() &&
                backButton.getY() < mouseY && mouseY < backButton.getY() + backButton.getHeight()) {

            freeze = !freeze;

            exitButton.setVisible(freeze);
            backButton.setVisible(freeze);

            justUnfrozen = true;

            return true;
        }

        return false;
    }

    @Override
    /**
     * Updates the state of the scene.
     */
    public void tick() {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && !player.isDisplayingInventory()) {

            freeze = !freeze;

            exitButton.setVisible(freeze);
            backButton.setVisible(freeze);
        }

        if (freeze) {

            batch.setColor(0.2f, 0.2f, 0.2f, 1.0f);
        }
        else {

            batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        }

        if (recompileShader) {

            lightShader.handle.begin();
            lightShader.updateLights(lights.size());
            lightShader.handle.end();

            batch.setShader(lightShader.handle);

            recompileShader = false;

            System.out.println("<---- Recompiling shader! ---->");
        }

        if (0.0f < shakeTime) {

            float x = (MathUtils.random(shakeIntensity) - shakeIntensity / 2);
            float y = (MathUtils.random(shakeIntensity) - shakeIntensity / 2);

            screenShake.set(x, y);

            shakeTime -= Gdx.graphics.getDeltaTime();
        }
        else {

            screenShake.set(0.0f, 0.0f);
            shakeIntensity = 0;
            shakeTime = 0.0f;
        }

        super.clearBuffers();

        batch.getTransformMatrix().setToTranslation(screenShake.x, screenShake.y, 0.0f);

        if (Gdx.input.isKeyPressed(Input.Keys.I)) {

            scrolled(1);
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.K)) {

            scrolled(-1);
        }

        fetchItems();
        if (player.health != 0 && !freeze) waveSystem.tick();
        updateStateOfBarriers();

        batch.setProjectionMatrix(player.camera.combined);
        batch.begin();
        lightShader.uploadUniforms(lights);

        map.playerPosition = player.position;

        map.drawFloor(batch, scaleFactor);
        map.drawWalls(batch, scaleFactor);

        updateStateOfGameObjects();

        if (Game.Debug) player.displayMessage("Time: " + waveSystem.getRemainingTime());

        if (waveSystem.getRemainingTime() < 10 && jukeboxWaveCount != waveSystem.getWave()) {

            jukeboxWaveCount++;

            jukebox.requestSongFromGroup("scary");
        }

        player.draw(batch);

        for (GameObject gameObject : gameObjects) {

            if (gameObject instanceof Explosion) {

                gameObject.draw(batch);
            }
        }

        batch.end();

        sceneStage.draw();
        stage.draw();

        if (dieLabel.isVisible()) {

            dieLabel.setPosition(Gdx.graphics.getWidth() / 2.0f - dieLabel.getWidth() / 2.0f, Gdx.graphics.getHeight() / 2.0f - dieLabel.getHeight() / 2.0f);

            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {

                init();

                dieLabel.setVisible(false);
            }
        }

        pointer.position.set(Gdx.input.getX(), -(Gdx.input.getY() - (Gdx.graphics.getHeight() - 1)));

        stage.getBatch().begin();
        dieLabel.draw(stage.getBatch(), 1.0f);
        pointer.draw((SpriteBatch) stage.getBatch());
        stage.getBatch().end();

        jukebox.tick();
    }

    public void inWorldSpace(Vector3 screenCoords) {

        player.camera.unproject(screenCoords, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void inScreenSpace(Vector3 worldCoords) {

        player.camera.project(worldCoords, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    float totalTimer = 0.0f;
    float rebuildingTimer = 0.0f;
    final float interval = 0.5f;
    boolean rebuilding = false;

    @Override
    public void dispose() {

        super.dispose();

        jukebox.dispose();
        sceneStage.dispose();
        player.dispose();
    }
}
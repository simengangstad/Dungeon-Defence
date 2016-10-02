package io.github.simengangstad.defendthecaves.scene.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import io.github.simengangstad.defendthecaves.Callback;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.procedural.MapGenerator;
import io.github.simengangstad.defendthecaves.scene.*;
import io.github.simengangstad.defendthecaves.scene.crafting.CraftableItemsView;
import io.github.simengangstad.defendthecaves.scene.crafting.Inventory;
import io.github.simengangstad.defendthecaves.scene.items.*;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author simengangstad
 * @since 10/11/15
 */
public class Player extends Entity implements InputProcessor {

    /**
     * The camera of the player.
     */
    public final OrthographicCamera camera;

    private final TextureRegion overlayTextureRegion = new TextureRegion(Game.SpriteSheet, 80, 80, 16, 16);

    private final Vector3 tmpVec = new Vector3();

    private final Vector2 tmpVec2 = new Vector2();

    private boolean displayingInventory = false;

    private boolean selectedSolid = false;

    private final Vector2 tmpPosition = new Vector2(), tmpDoorPosition = new Vector2();

    private boolean canOpen = false;

    private List<Object> tmpList = new ArrayList<Object>();

    private Key tmpKey;

    public boolean inRangeOfBarrier = false;

    private boolean displayingEnteringMessage = false;

    private float miningTimer = 0.0f;

    private static final float BreakInterval = 1.0f;

    private final Vector2 lastPositionMined = new Vector2().set(-1, -1);

    private int lastState = 0;

    private ItemBar itemBar;

    private Light light;

    private CraftableItemsView craftableItemsView;

    public static Sound[] Walking = new Sound[] {Gdx.audio.newSound(Gdx.files.internal("assets/sfx/walking1.ogg")), Gdx.audio.newSound(Gdx.files.internal("assets/sfx/walking2.ogg"))};

    private int walkStep = 0;

    private float timeToNextStep = 0.0f;

    public static final Sound Rocks = Gdx.audio.newSound(Gdx.files.internal("assets/sfx/rocks.wav"));

    private boolean initMouse = false;

    private float mouseTimer = 0.0f;

    /**
     * Initializes the player with a camera.
     */
    public Player(OrthographicCamera camera) {

        super(new Vector2(),
                new Vector2(80.0f, 80.0f),
                TextureUtil.getAnimation(Game.PlayerStationary, 16, 0.2f, Animation.PlayMode.NORMAL),
                TextureUtil.getAnimation(Game.PlayerMoving, 16, 0.075f, Animation.PlayMode.NORMAL));

        this.camera = camera;

        light = new Light(position, new Vector3(0.15f, 0.15f, 0.15f), 20.0f);

        light.flicker = false;
    }

    @Override
    public void create() {

        super.create();

        Vector2 size = new Vector2(75 * 5, 75 * 4);

        inventory = new Inventory(new Vector2(Gdx.graphics.getWidth() / 2.0f - 50.0f, Gdx.graphics.getHeight() / 2.0f - size.y / 2.0f), size, 5, 4);

        size.set(75*3, 75*2);

        craftableItemsView = new CraftableItemsView(new Vector2(Gdx.graphics.getWidth() / 2.0f - size.x - 100.0f, Gdx.graphics.getHeight() / 2.0f - size.y / 2.0f), size, 3, 2, this);
        inventory.host = this;
        inventory.toBack();
        craftableItemsView.toBack();

        host.addInputProcessor(this);

        itemBar = new ItemBar(inventory, this);
        itemBar.setPosition(Gdx.graphics.getWidth() / 2.0f - ItemBar.SlotSize * inventory.columns / 2.0f, 0.0f);
        itemBar.toBack();

        ((Scene) host).addLight(light);

        mouseTimer = 0.0f;

        inventory.setVisible(displayingInventory);
        if (inventory.speechBubble != null && !inventory.isVisible()) {

            inventory.speechBubble.setVisible(displayingInventory);
            inventory.label.setVisible(displayingInventory);
        }

        craftableItemsView.setVisible(displayingInventory);
        if (craftableItemsView.speechBubble != null && !craftableItemsView.isVisible()) {

            craftableItemsView.speechBubble.setVisible(displayingInventory);
            craftableItemsView.label.setVisible(displayingInventory);
        }
        itemBar.setVisible(!displayingInventory);
    }

    public void initialise() {

        inventory.clear();

        Axe axe = new Axe(new Callback() {
            @Override
            public void callback() {

            }
        });

        addItemAtLocation(0, 0, new Torch(position.cpy()));
        addItemAtLocation(1, 0, new Shield());
        addItemAtLocation(2, 0, axe);
        addItemAtLocation(3, 0, new Crossbow());


        for (int i = 0; i < 20; i++) {

            addItem(new Rock(new Vector2()));
            addItem(new Coal(new Vector2(0.0f, 0.0f)));
            addItem(new Wood(new Vector2(0.0f, 0.0f)));
            addItem(new StringItem(new Vector2(0.0f, 0.0f)));
        }


        HealthPotion potion = new HealthPotion(new Vector2());

        addItem(potion);

        ExplosivePotion potion1 = new ExplosivePotion(new Vector2());

        addItem(potion1);

        ExplosivePotion potion3 = new ExplosivePotion(new Vector2());

        addItem(potion3);

        ToxicPotion potion2 = new ToxicPotion(new Vector2());

        addItem(potion2);

        adjustHealth(100);

        forceApplied.set(0.0f, 0.0f);
        host.stage.addActor(inventory);
        host.stage.addActor(craftableItemsView);
        host.stage.addActor(itemBar);

        if (inventory.speechBubble != null)             host.stage.addActor(inventory.speechBubble);
        if (inventory.label != null)                    host.stage.addActor(inventory.label);
        if (craftableItemsView.speechBubble != null)    host.stage.addActor(craftableItemsView.speechBubble);
        if (craftableItemsView.label != null)           host.stage.addActor(craftableItemsView.label);
    }

    @Override
    public boolean keyDown(int keycode) {

        if (displayingEnteringMessage && keycode == Input.Keys.F) {

            if (canOpen) {

                map.set((int) tmpDoorPosition.x, (int) tmpDoorPosition.y, Map.DoorUnlocked);

                inventory.removeItem(tmpKey);

                canOpen = false;

                return true;
            }
            else {

                displayMessage("Dammit, I don't have the key...", 3.0f);
            }
        }

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {

        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        if (displayingInventory) {

            return inventory.touchDown(screenX, screenY, pointer, button) || craftableItemsView.touchDown(screenX, screenY, pointer, button);
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {

        if (currentItemPointer == 0 && amount == -1) {

            amount = inventory.columns - 1;
        }

        shuffleItem((currentItemPointer + amount) % inventory.columns);

        return true;
    }

    @Override
    protected void collides() {

    }

    public boolean isDisplayingInventory() {

        return displayingInventory;
    }

    private void toggleInventory() {

        displayingInventory = !displayingInventory;

        System.out.println("Toggling inventory: " + displayingInventory);
    }

    @Override
    public void tick() {

        if (health == 0) {

            return;
        }

        if (mouseTimer < 1.5f) {

            mouseTimer += Gdx.graphics.getDeltaTime();
        }
        else {

            initMouse = true;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {

            toggleInventory();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.C) && currentItem != null) {

            Vector3 vector3 = Game.vector3Pool.obtain();
            Vector2 vector = Game.vector2Pool.obtain();

            vector3.set(Gdx.input.getX(), Gdx.input.getY(), 0.0f);
            camera.unproject(vector3, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            vector.set(vector3.x - position.x, vector3.y - position.y).nor();

            currentItem.throwItem(vector);

            Game.vector2Pool.free(vector);
            Game.vector3Pool.free(vector3);

            currentItem = null;
        }

        if (inventory.containsKeys()) {

            if (tmpList.size() != inventory.getAmountOfKeys()) {

                tmpList.clear();

                inventory.getAllItemsByType(Key.class, tmpList);
            }
        }

        boolean inRangeOfRoom = false;

        for (MapGenerator.Room room : map.getRooms()) {

            if (room.isLocked() && map.get(room.getEntrance(0).x, room.getEntrance(0).y) != Map.DoorUnlocked) {

                if (map.lengthBetweenTiles((int) (position.x / Map.TileSizeInPixelsInWorldSpace), (int) (position.y / Map.TileSizeInPixelsInWorldSpace), room.getEntrance(0).x, room.getEntrance(0).y) < 2) {

                    inRangeOfRoom = true;

                    displayMessage("Hmm... Maybe I can go inside here (press f).");

                    if (!displayingEnteringMessage) {

                        displayingEnteringMessage = true;

                        tmpDoorPosition.set(room.getEntrance(0).x, room.getEntrance(0).y);

                        for (Object object : tmpList) {

                            Key key = (Key) object;

                            if (key.positionOfDoor.equals(room.getEntrance(0))) {

                                canOpen = true;

                                tmpKey = key;
                            }
                        }
                    }

                    break;
                }
            }
        }

        if (!inRangeOfRoom) {

            displayingEnteringMessage = false;
            canOpen = false;

            if (!inRangeOfStationaryItem && !inRangeOfBarrier) {

                hideMessage();
            }
        }

        if (!displayingInventory) {

            delta.set(0.0f, 0.0f);

            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {

                shuffleItem(0);
            }
            else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {

                shuffleItem(1);
            }
            else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {

                shuffleItem(2);
            }
            else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {

                shuffleItem(3);
            }
            else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {

                shuffleItem(4);
            }
            else if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {

                shuffleItemToNextIndex();
            }

            if (Gdx.input.isKeyPressed(Input.Keys.W)) {

                delta.add(0.0f, 1.0f);
            }

            if (Gdx.input.isKeyPressed(Input.Keys.S)) {

                delta.add(0.0f, -1.0f);
            }

            if (Gdx.input.isKeyPressed(Input.Keys.A)) {

                delta.add(-1.0f, 0.0f);
            }

            if (Gdx.input.isKeyPressed(Input.Keys.D)) {

                delta.add(1.0f, 0.0f);
            }

            if (delta.x != 0.0f || delta.y != 0.0f) {

                timeToNextStep -= Gdx.graphics.getDeltaTime();

                if (timeToNextStep < 0) {

                    if (Game.PlaySound) Walking[(walkStep++) % 1].play(0.15F);

                    while (timeToNextStep < 0) {

                        timeToNextStep += 0.2f;
                    }
                }
            }
            else {

                timeToNextStep = 0;
            }

            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && initMouse && !((Scene) host).justUnfrozen) {

                tmpVec.set(camera.unproject(tmpVec.set(Gdx.input.getX(), Gdx.input.getY(), 0.0f)));

                tmpPosition.set(tmpVec.x, tmpVec.y);

                tmpPosition.sub(position);

                interact(tmpPosition);

                tmpVec2.set(tmpVec.x, tmpVec.y);

                int x, y;

                if (map.lengthBetweenCoordinates(position, tmpVec2) <= 1) {

                    x = map.toTileCoordinate(tmpVec2.x);
                    y = map.toTileCoordinate(tmpVec2.y);

                }
                else {

                    tmpVec2.set(tmpVec.x - position.x, tmpVec.y - position.y).nor();

                    x = map.toTileCoordinate(position.x + tmpVec2.x * Map.TileSizeInPixelsInWorldSpace);
                    y = map.toTileCoordinate(position.y + tmpVec2.y * Map.TileSizeInPixelsInWorldSpace);
                }

                if (map.isBreakable(x, y) && currentItem instanceof Axe && selectedSolid) {

                    if ((lastPositionMined.x != x || lastPositionMined.y != y) && (lastPositionMined.x != -1 && lastPositionMined.y != -1)) {

                        map.set((int) lastPositionMined.x, (int) lastPositionMined.y, lastState);
                    }

                    lastState = map.get(x, y);
                    lastPositionMined.set(x, y);

                    if (BreakInterval < miningTimer) {

                        map.set(x, y, map.get(x, y) + 1);

                        System.out.println("Setting the state of breakable (" + x + ", " + y + ") to: " + map.get(x, y));

                        miningTimer = 0.0f;

                        if (map.get(x, y) == Map.SolidBroken + 1) {

                            map.set(x, y, Map.Open);

                            lastPositionMined.set(-1, -1);

                            miningTimer = 0.0f;

                            if (Game.PlaySound) Rocks.play(0.25f);

                            for (int i = 0; i < MathUtils.random(2, 4); i++) {

                                host.addGameObject(new Rock(new Vector2(x * Map.TileSizeInPixelsInWorldSpace + Map.TileSizeInPixelsInWorldSpace / 2.0f, y * Map.TileSizeInPixelsInWorldSpace + Map.TileSizeInPixelsInWorldSpace / 2.0f)));
                            }

                            host.addGameObject(new Coal(new Vector2(x * Map.TileSizeInPixelsInWorldSpace + Map.TileSizeInPixelsInWorldSpace / 2.0f, y * Map.TileSizeInPixelsInWorldSpace + Map.TileSizeInPixelsInWorldSpace / 2.0f)));
                        }
                    }

                    miningTimer += Gdx.graphics.getDeltaTime();
                }
                else {

                    if (lastPositionMined.x != -1.0f && lastPositionMined.y != -1.0f) {

                        map.set((int) lastPositionMined.x, (int) lastPositionMined.y, lastState);

                        lastPositionMined.set(-1.0f, -1.0f);
                    }
                }
            }
            else {

                if (lastPositionMined.x != -1.0f && lastPositionMined.y != -1.0f) {

                    map.set((int) lastPositionMined.x, (int) lastPositionMined.y, lastState);

                    lastPositionMined.set(-1.0f, -1.0f);
                }

                miningTimer = 0.0f;
            }

            // Left
            if (Gdx.input.getX() < Gdx.graphics.getWidth() / 2.0f && facingRight) {

                facingRight = false;
            }
            // Right
            else if (Gdx.graphics.getWidth() / 2.0f < Gdx.input.getX() && !facingRight) {

                facingRight = true;
            }

            tmpVec.set(Gdx.input.getX(), Gdx.input.getY(), 0.0f);

            tmpVec.set(camera.unproject(tmpVec));

            if (currentItem instanceof RotatableWeapon) {

                ((RotatableWeapon) currentItem).setRotation((int) (Math.atan((tmpVec.y - position.y) / (tmpVec.x - position.x)) * 180 / Math.PI));
            }
        }

        super.tick();

        updateCameraPosition();

        int animationIndex = currentAnimation.getKeyFrameIndex(stateTime % currentAnimation.getAnimationDuration());

        if (currentAnimation == stationaryAnimation) {

            raiseItem = animationIndex == 1 || animationIndex == 2;
        }
        else if (currentAnimation == movingAnimation) {

            raiseItem = animationIndex == 1 || animationIndex == 2 || animationIndex == 4;
        }
    }

    @Override
    public void draw(SpriteBatch batch) {

        tmpPosition.set(position);

        super.draw(batch);

        if (map.isBreakable((int) (tmpVec.x / Map.TileSizeInPixelsInWorldSpace), (int) (tmpVec.y / Map.TileSizeInPixelsInWorldSpace))) {

            selectedSolid = true;
        }
        else {

            selectedSolid = false;
        }

        batch.draw(overlayTextureRegion, ((int) (tmpVec.x / Map.TileSizeInPixelsInWorldSpace)) * Map.TileSizeInPixelsInWorldSpace, ((int) (tmpVec.y / Map.TileSizeInPixelsInWorldSpace)) * Map.TileSizeInPixelsInWorldSpace, Map.TileSizeInPixelsInWorldSpace, Map.TileSizeInPixelsInWorldSpace);

        inventory.setVisible(displayingInventory);
        if (inventory.speechBubble != null && !inventory.isVisible()) {

            inventory.speechBubble.setVisible(displayingInventory);
            inventory.label.setVisible(displayingInventory);
        }

        craftableItemsView.setVisible(displayingInventory);
        if (craftableItemsView.speechBubble != null && !craftableItemsView.isVisible()) {

            craftableItemsView.speechBubble.setVisible(displayingInventory);
            craftableItemsView.label.setVisible(displayingInventory);
        }
        itemBar.setVisible(!displayingInventory);
/*
        if (unlockButton.visible) {

            unlockButton.tick();

            unlockButton.draw(batch, position, unlockButton.getSize());
        }
*/
    }

    public void updateCameraPosition() {

        camera.position.set(Math.round(position.x), Math.round(position.y), 0.0f);
        camera.update();

        position.set(camera.position.x, camera.position.y);
    }
}

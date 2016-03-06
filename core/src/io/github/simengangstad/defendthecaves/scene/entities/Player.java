package io.github.simengangstad.defendthecaves.scene.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.procedural.MapGenerator;
import io.github.simengangstad.defendthecaves.scene.Entity;
import io.github.simengangstad.defendthecaves.scene.Map;
import io.github.simengangstad.defendthecaves.scene.items.RotatableWeapon;
import io.github.simengangstad.defendthecaves.scene.TextureUtil;
import io.github.simengangstad.defendthecaves.scene.crafting.CraftingInventory;
import io.github.simengangstad.defendthecaves.scene.items.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author simengangstad
 * @since 10/11/15
 */
public class Player extends Entity implements InputProcessor {

    /**
     * The camera of the player.
     */
    public final OrthographicCamera camera;

    private final TextureRegion mousePointerTextureRegion = new TextureRegion(Game.SpriteSheet, 96, 80, 16, 16);

    private final TextureRegion overlayTextureRegion = new TextureRegion(Game.SpriteSheet, 80, 80, 16, 16);

    private final Vector3 tmpVec = new Vector3();

    private final Vector2 tmpVec2 = new Vector2();

    private boolean displayingInventory = false;

    private boolean selectedSolid = false;

    private final Vector2 tmpPosition = new Vector2(), tmpDoorPosition = new Vector2();

    private boolean canOpen = false;

    private List<Object> tmpList = new ArrayList<>();

    private Key tmpKey;

    public boolean inRangeOfBarrier = false;

    private boolean displayingEnteringMessage = false;

    private float miningTimer = 0.0f;

    private static final float FirstBreak = 0.0f, SecondBreak = 4.0f, ThirdBreak = 8.0f;

    private final Vector2 lastPositionMined = new Vector2().set(-1, -1);

    /**
     * Initializes the player with a camera.
     */
    public Player(OrthographicCamera camera) {

        super(new Vector2(),
                new Vector2(80.0f, 80.0f),
                TextureUtil.getAnimation(Game.PlayerStationary, 16, 0.2f, Animation.PlayMode.NORMAL),
                TextureUtil.getAnimation(Game.PlayerMoving, 16, 0.075f, Animation.PlayMode.NORMAL));

        this.camera = camera;
    }

    @Override
    public void create() {

        super.create();

        inventory = new CraftingInventory(10);
        inventory.setSize(650, 400);
        inventory.host = this;

        Axe axe = new Axe(() -> {});

        addItemAtLocation(1, 0, new Shield());
        addItemAtLocation(2, 0, axe);

        for (int i = 0; i < 19; i++) {

            addItem(new Coal(new Vector2(0.0f, 0.0f)));
            addItem(new Wood(new Vector2(0.0f, 0.0f)));
            addItem(new StringItem(new Vector2(0.0f, 0.0f)));
        }

        addItem(new Shield());
        addItem(new Torch(new Vector2(position)));
        addItem(new Rock(new Vector2()));
        addItem(new Rock(new Vector2()));
        addItem(new Rock(new Vector2()));

        host.addInputProcessor(this);

        host.stage.addActor(inventory);
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
        return false;
    }

    @Override
    protected void collides() {

    }

    private void toggleInventory() {

        inventory.toFront();

        displayingInventory = !displayingInventory;

        System.out.println("Toggling inventory: " + displayingInventory);
    }

    @Override
    public void tick() {

        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {

            toggleInventory();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.C) && currentItem != null) {

            Vector2 vector = Game.vector2Pool.obtain();

            vector.set(!flip() ? 1 : -1, 0.0f);

            currentItem.throwItem(vector);

            Game.vector2Pool.free(vector);

            currentItem = null;
        }

        if (inventory.containsKeys()) {

            if (tmpList.size() != inventory.getAmountOfKeys()) {

                tmpList.clear();

                inventory.getAllItemsByType(Key.class, tmpList);
            }
        }

        inventory.setPosition(Gdx.graphics.getWidth() / 2.0f - inventory.getWidth() / 2.0f, Gdx.graphics.getHeight() / 2.0f - inventory.getHeight() / 2.0f);

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
                }

                break;
            }
            else {

                continue;
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

            if (Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT)) {

                shuffleItem();
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

            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {

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

                if (map.isSolid(x, y) && currentItem instanceof Axe && selectedSolid) {

                    if ((lastPositionMined.x != x || lastPositionMined.y != y) && (lastPositionMined.x != -1 && lastPositionMined.y != -1)) {

                        map.set((int) lastPositionMined.x, (int) lastPositionMined.y, Map.SolidIntact);
                    }

                    lastPositionMined.set(x, y);

                    if (FirstBreak < miningTimer && miningTimer < SecondBreak) {

                        map.set(x, y, Map.SolidSlightlyBroken);
                    }
                    else if (SecondBreak < miningTimer && miningTimer < ThirdBreak) {

                        map.set(x, y, Map.SolidBroken);
                    }
                    else if (ThirdBreak < miningTimer) {

                        map.set(x, y, Map.Open);

                        lastPositionMined.set(-1, -1);

                        miningTimer = 0.0f;

                        for (int i = 0; i < MathUtils.random(2, 4); i++) {

                            host.addGameObject(new Rock(new Vector2(x * Map.TileSizeInPixelsInWorldSpace + Map.TileSizeInPixelsInWorldSpace / 2.0f, y * Map.TileSizeInPixelsInWorldSpace + Map.TileSizeInPixelsInWorldSpace / 2.0f)));
                        }
                    }

                    miningTimer += Gdx.graphics.getDeltaTime();
                }
                else {

                    if (lastPositionMined.x != -1.0f && lastPositionMined.y != -1.0f) {

                        map.set((int) lastPositionMined.x, (int) lastPositionMined.y, Map.SolidIntact);
                    }
                }
            }
            else {

                if (lastPositionMined.x != -1.0f && lastPositionMined.y != -1.0f) {

                    map.set((int) lastPositionMined.x, (int) lastPositionMined.y, Map.SolidIntact);
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

            super.tick();

            updateCameraPosition();

            int animationIndex = currentAnimation.getKeyFrameIndex(stateTime % currentAnimation.getAnimationDuration());

            if (currentAnimation == stationaryAnimation) {

                raiseItem = animationIndex == 1 || animationIndex == 2;
            }
            else if (currentAnimation == movingAnimation) {

                raiseItem = animationIndex == 1 || animationIndex == 2 || animationIndex == 4;
            }

            tmpVec.set(Gdx.input.getX(), Gdx.input.getY(), 0.0f);

            tmpVec.set(camera.unproject(tmpVec));

            if (currentItem instanceof RotatableWeapon) {

                ((RotatableWeapon) currentItem).setRotation((int) (Math.atan((tmpVec.y - position.y) / (tmpVec.x - position.x)) * 180 / Math.PI));
            }
        }
    }

    @Override
    public void draw(SpriteBatch batch) {

        tmpPosition.set(position);

        if (currentItem != null) {

            if (raiseItem) {

                currentItem.walkingOffset = (size.y / 16.0f);
            }
            else {

                currentItem.walkingOffset = 0.0f;
            }
        }

        super.draw(batch);

        if (map.isSolid((int) (tmpVec.x / Map.TileSizeInPixelsInWorldSpace), (int) (tmpVec.y / Map.TileSizeInPixelsInWorldSpace))) {

            if (map.get((int) (tmpVec.x / Map.TileSizeInPixelsInWorldSpace), (int) (tmpVec.y / Map.TileSizeInPixelsInWorldSpace)) < Map.SpawnIntact) {

                selectedSolid = true;
            }
        }
        else {

            selectedSolid = false;
        }

        batch.draw(overlayTextureRegion, ((int) (tmpVec.x / Map.TileSizeInPixelsInWorldSpace)) * Map.TileSizeInPixelsInWorldSpace, ((int) (tmpVec.y / Map.TileSizeInPixelsInWorldSpace)) * Map.TileSizeInPixelsInWorldSpace, Map.TileSizeInPixelsInWorldSpace, Map.TileSizeInPixelsInWorldSpace);

        inventory.setVisible(displayingInventory);
/*
        if (unlockButton.visible) {

            unlockButton.tick();

            unlockButton.draw(batch, position, unlockButton.getSize());
        }
*/
        batch.draw(mousePointerTextureRegion, tmpVec.x - (Map.TileSizeInPixelsInWorldSpace / 2.0f) / 2.0f, tmpVec.y - (Map.TileSizeInPixelsInWorldSpace / 2.0f) / 2.0f, (Map.TileSizeInPixelsInWorldSpace / 2.0f), (Map.TileSizeInPixelsInWorldSpace / 2.0f));
    }

    public void updateCameraPosition() {

        camera.position.set(Math.round(position.x), Math.round(position.y), 0.0f);
        camera.update();

        position.set(camera.position.x, camera.position.y);
    }
}

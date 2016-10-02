package io.github.simengangstad.defendthecaves.scene.crafting;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.StringBuilder;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.scene.Entity;
import io.github.simengangstad.defendthecaves.scene.Item;
import io.github.simengangstad.defendthecaves.scene.gui.SlotItem;
import io.github.simengangstad.defendthecaves.scene.gui.SlotView;
import io.github.simengangstad.defendthecaves.scene.gui.SpeechBubble;
import io.github.simengangstad.defendthecaves.scene.items.Key;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The inventory of entites and other objects. Consits of {@link Item} and extends {@link WidgetGroup} so
 * the inventory can be presented to the user.
 *
 * @author simengangstad
 * @since 17/01/16
 */
public class Inventory extends SlotView implements InputProcessor {

    /**
     * The entity which carries the inventory.
     */
    public Entity host;

    /**
     * The maximum amount of items that can be stacked together in one slot.
     */
    protected final int MaxAmountOfItemsInSlot = 16;

    /**
     * The amount of item lists in the inventory.
     */
    private int size = 0;

    /**
     * The item(s) which the user is currently intercting with.
     */
    protected ArrayList<Item> currentItems = new ArrayList<Item>();

    /**
     * Reference to the position the {@link Inventory#currentItems} was placed at.
     */
    protected int lastColumn = 0, lastRow = 0;

    /**
     * The keys in the inventory.
     */
    private int keys = 0;

    /**
     * Labels showing the amount of items within each slot.
     */
    protected Label[][] labels;

    /**
     * Used for the labels.
     */
    protected StringBuilder stringBuilder = new StringBuilder();

    /**
     * The texture region for the slot item that can be used for removing items from the inventory.
     */
    private final TextureRegion trashTextureRegion = new TextureRegion(Game.SpriteSheet, 112, 208, 16, 16);

    private final Label binLabel = new Label("Bin", Game.LabelStyle12);

    public static final Sound Trashing = Gdx.audio.newSound(Gdx.files.internal("assets/sfx/rpg/misc/random6.ogg"));

    /**
     * A label showing {@link Item#getInformation()} about the items.
     */
    public SpeechBubble speechBubble = null;

    private ArrayList<Item> tmpList = new ArrayList<Item>();

    public Label label = null;

    /**
     * Initialises the inventory with a origin and a size used for drawing the inventory.
     */
    public Inventory(Vector2 origin, Vector2 size, int columns, int rows) {

        super(origin, size, columns, rows);

        binLabel.setSize(50, 20);
        binLabel.setAlignment(Align.center);
        binLabel.setPosition(Gdx.graphics.getWidth() - slotWidth + (slotWidth / style.slot.getMinWidth()) + 5.0f, (slotHeight / style.slot.getMinHeight()) + slotHeight - (slotHeight / style.slot.getMinHeight()) * 2 + 10.0f);
        binLabel.setVisible(true);

        labels = new Label[columns][rows];

        for (int x = 0; x < labels.length; x++) {

            for (int y = 0; y < labels[0].length; y++) {

                labels[x][y] = new Label("", Game.LabelStyle8);
            }
        }
    }

    /**
     * @return If the inventory is full.
     */
    public boolean isFull() {

        return size == rows * columns;
    }

    public boolean containsKeys() {

        return keys > 0;
    }

    public int getAmountOfKeys() {

        return keys;
    }

    public boolean placeItems(int x, int y, ArrayList<Item> items) {

        for (Item item : items) {

            if (!placeItem(x, y, item)) {

                return false;
            }
        }

        return true;
    }

    public boolean placeItem(int x, int y, Item item) {

        if (!isValidPosition(x, y)) {

            System.err.println("Inventory: Not a valid position to place item at: (" + x + ", " + y + "). Must place within the boundary: (" + (columns - 1) + ", " + (rows - 1) + ").");
        }

        Class itemType = getItemType(x, y);

        if (((item.stackable && itemType == item.getClass()) && getItemList(x, y).size() < MaxAmountOfItemsInSlot) || itemType == null) {

            if (itemType == null) {

                size++;
            }

            if (item instanceof Key) {

                keys++;
            }

            getItemList(x, y).add(item);

            // System.out.println("Inventory: Placing item (" + item + ") at position (" + x + ", " + y + ")");

            return true;
        }
        else {

            System.out.println("Inventory: Can't place item at given position (" + x + ", " + y + ") as the item(s) on the given position is of another type.");

            return false;
        }
    }

    /**
     * @return If the given item can be added to the inventory.
     */
    public boolean sufficientPlaceForItem(Item item) {

        for (int x = 0;  x < items.length; x++) {

            for (int y = 0; y < items[0].length; y++) {

                Class itemType = getItemType(x, y);

                if (((item.stackable && itemType == item.getClass()) && getItemList(x, y).size() < MaxAmountOfItemsInSlot) || itemType == null) {

                    return true;
                }
            }
        }

        return false;
    }

    public boolean placeItems(ArrayList<Item> items) {

        for (Item item : items) {

            if (!placeItem(item)) {

                return false;
            }
        }

        return true;
    }

    /**
     * Places an item (if possible) in the inventory.
     *
     * @return If the placement was successful.
     */
    public boolean placeItem(Item item) {

        boolean foundPlace = false;

        for (int y = 0; y < items[0].length; y++) {

            for (int x = 0;  x < items.length; x++) {

                Class itemType = getItemType(x, y);

                if (((item.stackable && itemType == item.getClass()) && getItemList(x, y).size() < MaxAmountOfItemsInSlot) || itemType == null) {

                    if (itemType == null) {

                        size++;
                    }

                    if (item instanceof Key) {

                        keys++;
                    }

                    getItemList(x, y).add(item);

                    if (Game.Debug) System.out.println("Inventory: Placing item (" + item + ") at position (" + x + ", " + y + ")");

                    foundPlace = true;

                    break;
                }
            }

            if (foundPlace) {

                break;
            }
        }

        return foundPlace;
    }

    public ArrayList<SlotItem> getItemList(int x, int y) {

        if (!isValidPosition(x, y)) {

            System.err.println("Inventory: Not a valid position to retrieve an item list from: (" + x + ", " + y + ").");

            return null;
        }

        return items[x][y];
    }

    public Class getItemType(int x, int y) {

        if (!isValidPosition(x, y)) {

            System. err.println("Inventory: Not a valid position to retrieve an item type from: (" + x + ", " + y + ").");

            return null;
        }

        if (!items[x][y].isEmpty()) {

            return items[x][y].get(0).getClass();
        }

        return null;
    }

    /**
     * Adds all items of the given type to the list.
     */
    public void getAllItemsByType(Class type, List<Object> list) {

        list.clear();

        for (int x = 0; x < items.length; x++) {

            for (int y = 0; y < items[0].length; y++) {

                if (getItemType(x, y) == type) {

                    list.addAll(getItemList(x, y));
                }
            }
        }
    }

    /**
     * Removes the given item from the inventory.
     */
    public void removeItem(Item item) {

        for (int x = 0; x < items.length; x++) {

            for (int y = 0; y < items[0].length; y++) {

                if (getItemType(x, y) == item.getClass()) {

                    ArrayList<SlotItem> items = getItemList(x, y);

                    Iterator iterator = items.iterator();

                    while (iterator.hasNext()) {

                        Item next = (Item) iterator.next();

                        if (next.equals(item)) {

                            iterator.remove();

                            return;
                        }
                    }
                }
            }
        }
    }

    /**
     * Obtains items from the given position.
     *
     * @param amount The amount of items that shall be obtained.
     *
     * @return The requested items.
     */
    public ArrayList<Item> obtainItem(int x, int y, int amount) {

        if (!isValidPosition(x, y)) {

            System.err.println("Inventory: Not a valid position to obtain an item list from: (" + x + ", " + y + ").");

            return null;
        }

        ArrayList<Item> newItems = new ArrayList<Item>();

        if (items[x][y].size() <= amount) {

            amount = items[x][y].size();

            size--;
        }

        for (int i = amount - 1; i >= 0; i--) {

            newItems.add((Item) items[x][y].get(i));

            items[x][y].remove(i);
        }

        if (getItemType(x, y) == Key.class) {

            keys--;
        }

        return newItems;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        if (label == null) {

            label = new Label("Inventory", Game.LabelStyle16);
            label.setPosition(getX() + getWidth() / 2.0f - label.getPrefWidth() / 2.0f, getY() + getHeight());
            label.toFront();

            getStage().addActor(label);
        }

        if (speechBubble == null) {

            speechBubble = new SpeechBubble();
            speechBubble.setWidth(160.0f);
            speechBubble.toFront();

            getStage().addActor(speechBubble);
        }

        style.slot.draw(batch, Gdx.graphics.getWidth() - slotWidth, 0.0f, slotWidth, slotHeight);

        float width = slotWidth - (slotWidth / style.slot.getMinWidth()) * 2;
        float height = slotHeight - (slotHeight / style.slot.getMinHeight()) * 2;

        binLabel.draw(batch, 1.0f);

        batch.draw(trashTextureRegion, Gdx.graphics.getWidth() - slotWidth + (slotWidth / style.slot.getMinWidth()), (slotHeight / style.slot.getMinHeight()), width, height);

        label.setVisible(true);

        // ----- DRAWING THE LABELS -----

        super.draw(batch, parentAlpha);

        for (int x = 0; x < columns; x++) {

            for (int y = 0; y < rows; y++) {

                labels[x][y].setVisible(false);

                if (!getItemList(x, y).isEmpty()) {

                    labels[x][y].setPosition(getX() + x * slotWidth + slotWidth - labels[x][y].getPrefWidth() - 2.0f, getY() + y * slotHeight + 11.0f);
                    labels[x][y].setVisible(true);
                    labels[x][y].toFront();

                    stringBuilder.setLength(0);
                    stringBuilder.append(getItemList(x, y).size());
                    labels[x][y].setText(stringBuilder);

                    labels[x][y].draw(batch, parentAlpha);
                }
            }
        }

        // ----- MOVING THE ITEMS -----

        speechBubble.setVisible(false);


        int x = Gdx.input.getX();
        int y = Math.abs(Gdx.input.getY() - (Gdx.graphics.getHeight() - 1));

        int column = (int) ((x - getX()) / slotWidth);
        int row = (int) ((y - getY()) / slotHeight);

        if (isValidPosition(column, row) && x >= getX() && x < getX() + getWidth() && y >= getY() && y < getY() + getHeight()) {

            if (!getItemList(column, row).isEmpty() && currentItems.isEmpty()) {

                if (isVisible()) speechBubble.setVisible(true);

                stringBuilder.setLength(0);
                stringBuilder.append(((Item) getItemList(column, row).get(0)).getInformation());

                speechBubble.setText(stringBuilder);
            }

            lastColumn = column;
            lastRow = row;
        }

        if (!currentItems.isEmpty()) {

            float scale = 1.25f;

            currentItems.get(0).draw(batch, x - slotWidth * scale / 2.0f, y - slotHeight * scale / 2.0f, slotWidth * scale, slotHeight * scale);
        }

        speechBubble.setPosition(x - speechBubble.getWidth() / 2.0f, y + 15.0f);
    }

    @Override
    public boolean keyDown(int keycode) {
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

        if (button == Input.Buttons.LEFT) {

            int x = screenX;
            int y = Math.abs(screenY - (Gdx.graphics.getHeight() - 1));

            int column = (int) ((x - getX()) / slotWidth);
            int row = (int) ((y - getY()) / slotHeight);

            if (isValidPosition(column, row)) {

                if (currentItems.isEmpty()) {

                    if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {

                        // Adds the top item in the slot to the current items held by the mouse
                        currentItems.add(obtainItem(column, row, 1).get(0));
                    }
                    else {

                        // Adds all the items in the slot to the current items held by the mouse
                        currentItems.addAll(obtainItem(column, row, getItemList(column, row).size()));
                    }
                }
                else {

                    if (getItemType(column, row) == currentItems.get(0).getClass() && currentItems.get(0).stackable) {

                        if (getItemList(column, row).size() + currentItems.size() > MaxAmountOfItemsInSlot) {

                            for (int i = getItemList(column, row).size(); i < MaxAmountOfItemsInSlot; i++) {

                                tmpList.add(currentItems.get(currentItems.size() - 1));
                                currentItems.remove(currentItems.size() - 1);
                            }
                        }
                        else {

                            tmpList.addAll(currentItems);
                            currentItems.clear();
                        }

                        placeItems(column, row, tmpList);
                        tmpList.clear();
                    }
                    else if (getItemType(column, row) == null) {

                        placeItems(column, row, currentItems);
                        currentItems.clear();
                    }
                    else {


                        // Switches the items held by the mouse with the ones in the slot
                        tmpList.addAll(obtainItem(column, row, getItemList(column, row).size()));

                        placeItems(column, row, currentItems);

                        currentItems.clear();
                        currentItems.addAll(tmpList);

                        tmpList.clear();
                    }
                }

                return true;
            }
            else if (Gdx.graphics.getWidth() - slotWidth < x && x < Gdx.graphics.getWidth() && 0.0f < y && y < slotHeight) {

                if (Game.PlaySound) Trashing.play();
                currentItems.clear();
            }
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
        return false;
    }
}

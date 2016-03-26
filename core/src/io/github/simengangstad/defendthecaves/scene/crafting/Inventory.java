package io.github.simengangstad.defendthecaves.scene.crafting;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.StringBuilder;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.scene.Entity;
import io.github.simengangstad.defendthecaves.scene.Item;
import io.github.simengangstad.defendthecaves.scene.gui.SpeechBubble;
import io.github.simengangstad.defendthecaves.scene.items.Weapon;
import io.github.simengangstad.defendthecaves.scene.items.Key;
import io.github.simengangstad.defendthecaves.scene.items.Shield;

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
public class Inventory extends Table {

    /**
     * The entity which carries the inventory.
     */
    public Entity host;

    /**
     * The dimension of the inventory.
     */
    public int columns, rows;

    /**
     * The items in the inventory.
     */
    private final ArrayList<Item>[][] items;

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
    protected ArrayList<Item> currentItems = new ArrayList<>();

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
     * A label showing {@link Item#getInformation()} about the items.
     */
    protected SpeechBubble speechBubble = new SpeechBubble();

    /**
     * The grafical style of the inventory; its background and slots.
     */
    protected InventoryStyle style;

    /**
     * Initialises the inventory with a origin and a size used for drawing the inventory.
     */
    public Inventory(Vector2 origin, Vector2 size, int columns, int rows) {

        if (origin != null && size != null) super.setBounds(origin.x, origin.y, size.x, size.y);

        this.columns = columns;
        this.rows = rows;

        items = new ArrayList[columns][rows];

        for (int x = 0;  x < columns; x++) {

            for (int y = 0; y < rows; y++) {

                items[x][y] = new ArrayList<>();
            }
        }

        labels = new Label[columns][rows];

        for (int x = 0; x < labels.length; x++) {

            for (int y = 0; y < labels[0].length; y++) {

                labels[x][y] = new Label("", Game.UISkin);
            }
        }

        speechBubble.setWidth(160.0f);

        setStyle(Game.UISkin.get(InventoryStyle.class));
    }

    public InventoryStyle getStyle() {

        return style;
    }

    public void setStyle (InventoryStyle style) {

        if (style == null) throw new IllegalArgumentException("style cannot be null.");

        this.style = style;

        Drawable background = style.background;

        setBackground(background);
    }

    /**
     * @return If the inventory is full.
     */
    public boolean isFull() {

        return size == rows * columns - 1;
    }

    public boolean isValidPosition(int x, int y) {

        return 0 <= x && x < columns && 0 <= y && y < rows;
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

            System.out.println("Inventory: Placing item (" + item + ") at position (" + x + ", " + y + ")");

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

                    System.out.println("Inventory: Placing item (" + item + ") at position (" + x + ", " + y + ")");

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

    public ArrayList<Item> getItemList(int x, int y) {

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

                    ArrayList<Item> items = getItemList(x, y);

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

        ArrayList<Item> newItems = new ArrayList<>();

        if (items[x][y].size() <= amount) {

            amount = items[x][y].size();

            size--;
        }

        for (int i = amount - 1; i >= 0; i--) {

            newItems.add(items[x][y].get(i));

            items[x][y].remove(i);
        }

        if (getItemType(x, y) == Key.class) {

            keys--;
        }

        return newItems;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        super.draw(batch, parentAlpha);

        for (int x = 0; x < columns; x++) {

            for (int y = 0; y < rows; y++) {

                style.slot.draw(batch, getX() + x * (getWidth() / columns), getY() + y * (getHeight() / rows), getWidth() / columns, getHeight() / rows);

                labels[x][y].setVisible(false);

                if (!getItemList(x, y).isEmpty()) {

                    float width = getWidth() / columns - (getWidth() / columns / style.slot.getMinWidth()) * 2;
                    float height = getHeight() / rows - (getHeight() / rows / style.slot.getMinHeight()) * 2;

                    Item item = getItemList(x, y).get(0);

                    item.draw(
                            (SpriteBatch) batch,
                            getX() + x * (getWidth() / columns) + (getWidth() / columns / style.slot.getMinWidth()),
                            getY() + y * (getHeight() / rows) + (getHeight() / columns / style.slot.getMinHeight()),
                            width,
                            height,
                            false
                    );

                    labels[x][y].setPosition(getX() + x * width + width - 15.0f, getY() + y * height + 10.0f);
                    labels[x][y].setVisible(true);
                    labels[x][y].setFontScale(0.3f);

                    stringBuilder.setLength(0);
                    stringBuilder.append(getItemList(x, y).size());
                    labels[x][y].setText(stringBuilder);

                    labels[x][y].draw(batch, parentAlpha);
                }
            }
        }

        int x = Gdx.input.getX();
        int y = Math.abs(Gdx.input.getY() - (Gdx.graphics.getHeight() - 1));

        int column = (int) ((x - getX()) / (getWidth() / columns));
        int row = (int) ((y - getY()) / (getHeight() / rows));

        if (getX() <= x && x < getX() + getWidth() && getY() <= y && y < getY() + getHeight()) {


/*
            batch.draw(
                    overlayTextureRegion,
                    getX() + column * (getWidth() / columns) + (getWidth() / columns / 16) * 1,
                    getY() + row * (getHeight() / rows) + (getHeight() / rows / 16) * 1,
                    getWidth() / columns - (getWidth() / columns / 16) * 2,
                    getHeight() / rows - (getHeight() / rows / 16) * 2);
*/
            if (!getItemList(column, row).isEmpty()) {

                speechBubble.setVisible(true);

                stringBuilder.setLength(0);
                stringBuilder.append(getItemList(column, row).get(0).getInformation());

                speechBubble.setText(stringBuilder);

                if (currentItems.isEmpty() && Gdx.input.isButtonPressed(0)) {

                    if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {

                        currentItems.addAll(obtainItem(column, row, getItemList(column, row).size()));
                    }
                    else {

                        currentItems.add(obtainItem(column, row, 1).get(0));
                    }

                    lastColumn = column;
                    lastRow = row;
                }
            }
        }

        if (!currentItems.isEmpty() && !Gdx.input.isButtonPressed(0)) {

            if (isValidPosition(column, row) && ((currentItems.get(0).stackable && getItemType(column, row) == currentItems.get(0).getClass() && getItemList(column, row).size() < MaxAmountOfItemsInSlot) || getItemType(column, row) == null)) {

                placeItems(column, row, currentItems);
            }
            else {

                placeItems(lastColumn, lastRow, currentItems);
            }

            currentItems.clear();
        }

        if (currentItems.isEmpty()) {

            float width = getWidth() / columns - (getWidth() / columns / 20) * 2;
            float height = getHeight() / rows - (getHeight() / rows / 20) * 2;

            int scale = 1;

            if (currentItems.get(0) instanceof Weapon || currentItems.get(0) instanceof Shield) {

                width -= (getWidth() / columns / 20) * 2;
                height -= (getHeight() / rows / 20) * 2;

                scale = 2;
            }



            currentItems.get(0).draw(
                    (SpriteBatch) batch,
                    x + (getWidth() / columns / 20) * 1 - (width / 2.0f) * scale,
                    y + (getHeight() / rows / 20) * 1 - (height / 2.0f) * scale,
                    width * scale,
                    height * scale,
                    false
            );
        }

        // Speech bubble
        speechBubble.setPosition(x, y);
        speechBubble.draw(batch, parentAlpha);
    }

    public static class InventoryStyle {

        public Drawable background, slot;

        public InventoryStyle() {

        }

        public InventoryStyle(Drawable background, Drawable slot) {

            this.background = background;
            this.slot = slot;
        }
    }
}

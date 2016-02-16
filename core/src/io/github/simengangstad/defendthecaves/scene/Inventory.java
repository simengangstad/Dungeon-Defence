package io.github.simengangstad.defendthecaves.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.scene.item.Key;
import io.github.simengangstad.defendthecaves.scene.item.Shield;

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
     * The dimension of the inventory.
     */
    public final int columns, rows;

    /**
     * The items in the inventory.
     */
    private final ArrayList<Item>[][] items;

    /**
     * The amount of item lists in the inventory.
     */
    private int size = 0;

    /**
     * The item which the user is currently intercting with.
     */
    private Item currentItem = null;

    /**
     * Reference to the position the {@link Inventory#currentItem} was placed at.
     */
    private int lastColumn = 0, lastRow = 0;

    /**
     * The keys in the inventory.
     */
    private int keys = 0;

    /**
     * The grafical style of the inventory; its background and slots..
     */
    private InventoryStyle style;

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

        setStyle(Game.UISkin.get(InventoryStyle.class));
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

    public boolean placeItem(int x, int y, Item item) {

        if (!isValidPosition(x, y)) {

            System.err.println("Inventory: Not a valid position to place item at: (" + x + ", " + y + "). Must place within the boundary: (" + (columns - 1) + ", " + (rows - 1) + ").");
        }

        Class itemType = getItemType(x, y);

        if ((item.stackable && itemType == item.getClass()) || itemType == null) {

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

                if ((item.stackable && itemType == item.getClass()) || itemType == null) {

                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Places an item (if possible) in the inventory.
     *
     * @return If the placement was successful.
     */
    public boolean placeItem(Item item) {

        boolean foundPlace = false;

        for (int x = 0;  x < items.length; x++) {

            for (int y = 0; y < items[0].length; y++) {

                Class itemType = getItemType(x, y);

                if ((item.stackable && itemType == item.getClass()) || itemType == null) {

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

                if (!getItemList(x, y).isEmpty()) {

                    float width = getWidth() / columns - (getWidth() / columns / style.slot.getMinWidth()) * 2;
                    float height = getHeight() / rows - (getHeight() / rows / style.slot.getMinHeight()) * 2;

                    int scale = 1;
                    int offsetScale = 0;

                    Item item = getItemList(x, y).get(0);

                    // TODO: Make suitable icons for the weapons in the inventory
                    if (item instanceof Weapon || item instanceof Shield) {

                        width -= (getWidth() / columns / style.slot.getMinWidth()) * 2;
                        height -= (getHeight() / rows / style.slot.getMinHeight()) * 2;

                        scale = 1;
                        offsetScale = 0;
                    }

                    item.draw(
                            (SpriteBatch) batch,
                            getX() + x * (getWidth() / columns) + (getWidth() / columns / style.slot.getMinWidth()) * 1 + (getWidth() / columns / style.slot.getMinWidth()) * offsetScale - offsetScale * (width) / 2.0f,
                            getY() + y * (getHeight() / rows) + (getHeight() / columns / style.slot.getMinHeight()) * 1 + (getHeight() / columns / style.slot.getMinHeight()) * offsetScale - offsetScale * (height) / 2.0f,
                            width * scale,
                            height * scale
                    );
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
            if (currentItem == null && Gdx.input.isButtonPressed(0) && !getItemList(column, row).isEmpty()) {

                currentItem = obtainItem(column, row, 1).get(0);
            }

            lastColumn = column;
            lastRow = row;
        }

        if (currentItem != null && !Gdx.input.isButtonPressed(0)) {

            if (isValidPosition(column, row) && ((currentItem.stackable && getItemType(column, row) == currentItem.getClass()) || getItemType(column, row) == null)) {

                placeItem(column, row, currentItem);
            }
            else {

                placeItem(lastColumn, lastRow, currentItem);
            }

            currentItem = null;
        }

        if (currentItem != null) {

            float width = getWidth() / columns - (getWidth() / columns / 20) * 2;
            float height = getHeight() / rows - (getHeight() / rows / 20) * 2;

            int scale = 1;

            if (currentItem instanceof Weapon || currentItem instanceof Shield) {

                width -= (getWidth() / columns / 20) * 2;
                height -= (getHeight() / rows / 20) * 2;

                scale = 2;
            }

            currentItem.draw(
                    (SpriteBatch) batch,
                    x + (getWidth() / columns / 20) * 1 - (width / 2.0f) * scale,
                    y + (getHeight() / rows / 20) * 1 - (height / 2.0f) * scale,
                    width * scale,
                    height * scale
            );
        }
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

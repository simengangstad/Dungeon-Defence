package io.github.simengangstad.defendthecaves.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.defendthecaves.gui.View;

import java.util.ArrayList;

/**
 * The inventory of entites and other objects. Consits of {@link Item} and extends {@link View} so
 * the inventory can be presented to the user.
 *
 * @author simengangstad
 * @since 17/01/16
 */
public class Inventory extends View {

    /**
     * The dimension of the inventory.
     */
    private final int columns, rows;

    /**
     * The items in the inventory.
     */
    private final ArrayList<Item>[][] items;

    /**
     * The amount of item lists in the inventory.
     */
    private int size = 0;

    /**
     * The cell and overlay texture regions.
     */
    public TextureRegion cellTextureRegion, overlayTextureRegion;

    /**
     * The item which the user is currently intercting with.
     */
    private Item currentItem = null;

    /**
     * Reference to the position the {@link Inventory#currentItem} was placed at.
     */
    private int lastColumn = 0, lastRow = 0;

    /**
     * Initialises the inventory with a origin and a size used for drawing the inventory.
     */
    public Inventory(Vector2 origin, Vector2 size, int columns, int rows, TextureRegion cellTextureRegion, TextureRegion overlayTextureRegion) {

       super(origin, size, null);

        this.columns = columns;
        this.rows = rows;

        items = new ArrayList[columns][rows];

        for (int x = 0;  x < columns; x++) {

            for (int y = 0; y < rows; y++) {

                items[x][y] = new ArrayList<>();
            }
        }

        this.cellTextureRegion = cellTextureRegion;
        this.overlayTextureRegion = overlayTextureRegion;
    }

    /**
     * @return If the inventory is full.
     */
    public boolean isFull() {

        return size == rows * columns;
    }

    public boolean isValidPosition(int x, int y) {

        return 0 <= x && x < columns && 0 <= y && y < rows;
    }

    public boolean placeItem(Item item, int x, int y) {

        if (!isValidPosition(x, y)) {

            System.err.println("Inventory: Not a valid position to place item at: (" + x + ", " + y + ").");
        }

        Class itemType = getItemType(x, y);

        if (itemType == item.getClass() || itemType == null) {

            if (itemType == null) {

                size++;
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

    public void placeItem(Item item) {

        boolean foundPlace = false;

        for (int x = 0;  x < items.length; x++) {

            for (int y = 0; y < items[0].length; y++) {

                Class itemType = getItemType(x, y);

                if (itemType == item.getClass() || itemType == null) {

                    if (itemType == null) {

                        size++;
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

        return newItems;
    }

    @Override
    public void draw(SpriteBatch batch, Vector2 position, Vector2 size) {

        if (!visible) return;

        if (cellTextureRegion == null) {

            return;
        }

        for (int x = 0; x < columns; x++) {

            for (int y = 0; y < rows; y++) {

                batch.draw(cellTextureRegion, position.x + x * (size.x / columns), position.y + y * (size.y / rows), size.x / columns, size.y / rows);

                if (!getItemList(x, y).isEmpty()) {

                    batch.draw(
                            getItemList(x, y).get(0).getTextureRegion(),
                            position.x + x * (size.x / columns) + (size.x / columns / 16) * 1,
                            position.y + y * (size.y / rows) + (size.y / rows / 16) * 1,
                            size.x / columns - (size.x / columns / 16) * 2,
                            size.y / rows - (size.y / rows / 16) * 2);
                }
            }
        }

        int x = Gdx.input.getX();
        int y = Math.abs(Gdx.input.getY() - (Gdx.graphics.getHeight() - 1));

        int column = (int) ((x - position.x) / (size.x / columns));
        int row = (int) ((y - position.y) / (size.y / rows));

        if (position.x <= x && x < position.x + size.x && position.y <= y && y < position.y + size.y) {

            batch.draw(
                    overlayTextureRegion,
                    position.x + column * (size.x / columns) + (size.x / columns / 16) * 1,
                    position.y + row * (size.y / rows) + (size.y / rows / 16) * 1,
                    size.x / columns - (size.x / columns / 16) * 2,
                    size.y / rows - (size.y / rows / 16) * 2);

            if (currentItem == null && Gdx.input.isButtonPressed(0) && !getItemList(column, row).isEmpty()) {

                currentItem = obtainItem(column, row, 1).get(0);
            }

            lastColumn = column;
            lastRow = row;
        }

        if (currentItem != null && !Gdx.input.isButtonPressed(0)) {

            if (isValidPosition(column, row) && (getItemType(column, row) == currentItem.getClass() || getItemType(column, row) == null)) {

                placeItem(currentItem, column, row);
            }
            else {

                placeItem(currentItem, lastColumn, lastRow);
            }

            currentItem = null;
        }

        if (currentItem != null) {

            batch.draw(currentItem.getTextureRegion(), x - (size.x / columns) / 2 + (size.x / columns / 16) * 1, y - (size.y / rows) / 2 + (size.y / rows / 16) * 1, size.x / columns - (size.x / columns / 16) * 2, size.y / rows - (size.y / rows / 16) * 2);
        }
    }

    @Override
    public void tick() {

        if (!visible) return;
    }
}

package io.github.simengangstad.defendthecaves.scene.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.StringBuilder;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.scene.CraftingSystem;
import io.github.simengangstad.defendthecaves.scene.Item;
import io.github.simengangstad.defendthecaves.scene.Weapon;
import io.github.simengangstad.defendthecaves.scene.item.Shield;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author simengangstad
 * @since 16/02/16
 */
public class CraftingInventory extends Inventory {

    public Item result = null;

    private final int widthOfGap;

    private ArrayList<Item> items = new ArrayList<>();

    private boolean cameFromCraftingArea = false;

    private final Vector3 tmpCraftingVector = new Vector3();

    private final Vector2 lastPosition = new Vector2();

    private int slotSize = 70, offset = 50;

    private final int MaxAmount = 8;

    private Label label = new Label("Places left: 8", Game.UISkin);

    private Label[][] labels;

    private SpeechBubble speechBubble = new SpeechBubble();

    private boolean currentItemWasCrafted = false;

    public CraftingInventory(int widthOfGap) {

        super(new Vector2(), new Vector2(), 4, 5);

        this.widthOfGap = widthOfGap;

        labels = new Label[columns][rows];

        for (int x = 0; x < labels.length; x++) {

            for (int y = 0; y < labels[0].length; y++) {

                labels[x][y] = new Label("", Game.UISkin);
            }
        }

        speechBubble.setWidth(160.0f);
    }

    private boolean isInsideCraftingArea(float x, float y) {

        return getX() + offset <= x && x < getX() + offset + slotSize * 3 && getY() + offset <= y && y < getY() + offset + slotSize * 3;
    }

    public boolean insideItem(Item item, float width, float height, float x, float y) {

        return item.inventoryPosition.x - width / 2.0f <= x && x < item.inventoryPosition.x + width / 2.0f && item.inventoryPosition.y - height / 2.0f <= y && y < item.inventoryPosition.y + height / 2.0f;
    }

    private StringBuilder stringBuilder = new StringBuilder();

    @Override
    public void draw(Batch batch, float parentAlpha) {

        // Background
        style.background.draw(batch, getX(), getY(), getWidth(), getHeight());


        // Crafting area
        float posXCraftingArea = getX() + offset;
        float posYCraftingArea = getY() + offset;

        float sizeXCraftingArea = slotSize * 3;
        float sizeYCraftingArea = slotSize * 3;

        style.slot.draw(batch, posXCraftingArea, posYCraftingArea, sizeXCraftingArea, sizeYCraftingArea);

        for (Item item : items) {

            item.draw(

                    (SpriteBatch) batch,
                    item.inventoryPosition.x - slotSize / 2.0f,
                    item.inventoryPosition.y - slotSize / 2.0f,
                    slotSize,
                    slotSize
            );
        }


        // Label
        label.setText("Places left: " + (MaxAmount - items.size()));
        label.setPosition(posXCraftingArea + sizeXCraftingArea / 2.0f - label.getPrefWidth() / 2.0f, posYCraftingArea - 30.0f);
        label.draw(batch, parentAlpha);

        // Result
        float posXResult = getX() + offset + slotSize;
        float posYResult = getY() + getHeight() - slotSize - offset;

        style.slot.draw(batch, posXResult, posYResult, slotSize, slotSize);

        if (result != null) {

            result.draw((SpriteBatch) batch, posXResult, posYResult, slotSize, slotSize);
        }


        // Inventory
        float sizeXInventory = slotSize * columns;
        float sizeYInventory = slotSize * rows;

        float posXInventory = (getX() + (getWidth() - widthOfGap) / 2.0f + widthOfGap) + ((getWidth() - widthOfGap) / 2.0f - sizeXInventory) / 2.0f;
        float posYInventory = getY() + (getHeight() - sizeYInventory) / 2.0f;

        for (int x = 0; x < columns; x++) {

            for (int y = 0; y < rows; y++) {

                style.slot.draw(batch, posXInventory + x * (sizeXInventory / columns), posYInventory + y * (sizeYInventory / rows), sizeXInventory / columns, sizeYInventory / rows);

                labels[x][y].setVisible(false);

                if (!getItemList(x, y).isEmpty()) {

                    float width = sizeXInventory / columns - (sizeXInventory / columns / style.slot.getMinWidth()) * 2;
                    float height = sizeYInventory / rows - (sizeYInventory / rows / style.slot.getMinHeight()) * 2;

                    Item item = getItemList(x, y).get(0);

                    item.draw(
                            (SpriteBatch) batch,
                            posXInventory + x * slotSize + (slotSize / style.slot.getMinWidth()),
                            posYInventory + y * slotSize + (slotSize / style.slot.getMinHeight()),
                            width,
                            height
                    );

                    labels[x][y].setPosition(posXInventory + x * slotSize + slotSize - 15.0f, posYInventory + y * slotSize + 10.0f);
                    labels[x][y].setVisible(true);

                    stringBuilder.setLength(0);
                    stringBuilder.append(getItemList(x, y).size());
                    labels[x][y].setText(stringBuilder);

                    labels[x][y].draw(batch, parentAlpha);
                }
            }
        }

        int x = Gdx.input.getX();
        int y = Math.abs(Gdx.input.getY() - (Gdx.graphics.getHeight() - 1));

        int column = (int) ((x - posXInventory) / (sizeXInventory / columns));
        int row = (int) ((y - posYInventory) / (sizeYInventory / rows));

        speechBubble.setVisible(false);

        if (posXInventory <= x && x < posXInventory + sizeXInventory && posYInventory <= y && y < posYInventory + sizeYInventory) {
            /*
            batch.draw(
                    overlayTextureRegion,
                    posXInventory + column * (sizeXInventory / columns) + (sizeXInventory / columns / 16) * 1,
                    posYInventory + row * (sizeYInventory / rows) + (sizeYInventory / rows / 16) * 1,
                    sizeXInventory / columns - (sizeXInventory / columns / 16) * 2,
                    sizeYInventory / rows - (sizeYInventory / rows / 16) * 2);
            */
            if (!getItemList(column, row).isEmpty()) {

                speechBubble.setVisible(true);

                stringBuilder.setLength(0);
                stringBuilder.append(getItemList(column, row).get(0).getInformation());

                speechBubble.setText(stringBuilder);

                if (Gdx.input.isButtonPressed(0) && currentItem == null) {

                    currentItem = obtainItem(column, row, 1).get(0);

                    cameFromCraftingArea = false;
                    currentItemWasCrafted = false;

                    lastColumn = column;
                    lastRow = row;
                }
            }
        }
        else if (posXCraftingArea <= x && x < posXCraftingArea + sizeXCraftingArea && posYCraftingArea <= y && y < posYCraftingArea + sizeYCraftingArea) {

            tmpCraftingVector.set(x, y, 0.0f);

            Iterator iterator = items.iterator();

            while (iterator.hasNext()) {

                Item item = (Item) iterator.next();

                if (insideItem(item, slotSize, slotSize, tmpCraftingVector.x, tmpCraftingVector.y)) {

                    speechBubble.setVisible(true);

                    stringBuilder.setLength(0);
                    stringBuilder.append(item.getInformation());

                    speechBubble.setText(stringBuilder);

                    if (currentItem == null && Gdx.input.isButtonPressed(0)) {

                        currentItem = item;

                        lastPosition.set(tmpCraftingVector.x, tmpCraftingVector.y);

                        cameFromCraftingArea = true;
                        currentItemWasCrafted = false;

                        iterator.remove();

                        computeResultFromCrafting();

                        break;
                    }
                }
            }
        }
        else if (posXResult <= x && x < posXResult + slotSize && posYResult <= y && y < posYResult + slotSize) {

            if (Gdx.input.isButtonPressed(0) && currentItem == null) {

                currentItem = result;

                result = null;

                currentItemWasCrafted = true;

                items.clear();
            }
        }

        if (currentItem != null && !Gdx.input.isButtonPressed(0)) {

            if (isValidPosition(column, row) && ((currentItem.stackable && getItemType(column, row) == currentItem.getClass()) || getItemType(column, row) == null)) {

                if (currentItemWasCrafted) {

                    currentItem.parent = host;
                    currentItem.map = host.map;
                }

                placeItem(column, row, currentItem);
            }
            else if (isInsideCraftingArea(x, y) && items.size() < MaxAmount) {

                currentItem.inventoryPosition.set(x, y);

                items.add(currentItem);

                computeResultFromCrafting();
            }
            else {

                if (cameFromCraftingArea) {

                    currentItem.inventoryPosition.set(lastPosition);

                    items.add(currentItem);

                    computeResultFromCrafting();

                }
                else {

                    placeItem(lastColumn, lastRow, currentItem);
                }
            }

            currentItem = null;
        }

        if (currentItem != null) {

            float scale = 1.25f;

            currentItem.draw(
                    (SpriteBatch) batch,
                    x - slotSize * scale / 2.0f,
                    y - slotSize * scale / 2.0f,
                    slotSize * scale,
                    slotSize * scale
            );
        }

        // Speech bubble
        speechBubble.setPosition(x, y);
        speechBubble.draw(batch, parentAlpha);
    }

    private void computeResultFromCrafting() {

        result = CraftingSystem.obtainItemFromGivenItems(items);
    }
}

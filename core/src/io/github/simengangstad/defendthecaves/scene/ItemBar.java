package io.github.simengangstad.defendthecaves.scene;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.StringBuilder;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.scene.crafting.CraftingInventory;

/**
 * @author simengangstad
 * @since 25/03/16
 */
public class ItemBar extends Table {

    private CraftingInventory inventory;

    private int amountOfItems;

    private Label[] labels;

    private StringBuilder stringBuilder = new StringBuilder();

    public ItemBar(CraftingInventory inventory) {

        this.inventory = inventory;
        amountOfItems = inventory.columns;

        labels = new Label[amountOfItems];

        for (int i = 0; i < amountOfItems; i++) {

            labels[i] = new Label("", Game.UISkin);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        setBackground(inventory.getBackground());

        float sizeXInventory = CraftingInventory.slotSize * amountOfItems;

        float posXInventory = getX();
        float posYInventory = getY();

        for (int i = 0; i < amountOfItems; i++) {

            inventory.getStyle().slot.draw(batch, posXInventory + i * (sizeXInventory / amountOfItems), posYInventory, CraftingInventory.slotSize, CraftingInventory.slotSize);

            labels[i].setVisible(false);

            if (!inventory.getItemList(i, 0).isEmpty()) {

                float width = CraftingInventory.slotSize - (CraftingInventory.slotSize / inventory.getStyle().slot.getMinWidth()) * 2;
                float height = CraftingInventory.slotSize - (CraftingInventory.slotSize / inventory.getStyle().slot.getMinHeight()) * 2;

                Item item = inventory.getItemList(i, 0).get(0);

                item.draw((SpriteBatch) batch, posXInventory + i * CraftingInventory.slotSize + (CraftingInventory.slotSize / inventory.getStyle().slot.getMinWidth()), posYInventory + (CraftingInventory.slotSize / inventory.getStyle().slot.getMinHeight()), width, height, false);

                labels[i].setPosition(posXInventory + i * CraftingInventory.slotSize + CraftingInventory.slotSize - labels[i].getPrefWidth() - 3.0f, posYInventory + 16.0f);
                labels[i].setVisible(true);
                labels[i].setFontScale(0.3f);

                stringBuilder.setLength(0);
                stringBuilder.append(inventory.getItemList(i, 0).size());
                labels[i].setText(stringBuilder);

                labels[i].draw(batch, parentAlpha);
            }
        }
    }
}

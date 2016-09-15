package io.github.simengangstad.defendthecaves.scene;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.StringBuilder;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.scene.crafting.Inventory;
import io.github.simengangstad.defendthecaves.scene.entities.Player;

/**
 * @author simengangstad
 * @since 25/03/16
 */
public class ItemBar extends Table {

    private Inventory inventory;

    private int amountOfItems;

    private Label[] labels;

    private StringBuilder stringBuilder = new StringBuilder();

    private TextureRegion selected = new TextureRegion(Game.GUISheet, 140, 1, 32, 32);

    private Player player;

    public static final int SlotSize = 70;

    public ItemBar(Inventory inventory, Player player) {

        this.inventory = inventory;
        this.player = player;
        amountOfItems = inventory.columns;

        labels = new Label[amountOfItems];

        for (int i = 0; i < amountOfItems; i++) {

            labels[i] = new Label("", Game.UISkin);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        setBackground(inventory.getStyle().background);

        float sizeXInventory = SlotSize * amountOfItems;

        float posXInventory = getX();
        float posYInventory = getY();

        for (int i = 0; i < amountOfItems; i++) {

            if (i == player.currentItemPointer) {

                batch.draw(selected, posXInventory + i * (sizeXInventory / amountOfItems), posYInventory, SlotSize, SlotSize);
            }
            else {

                inventory.getStyle().slot.draw(batch, posXInventory + i * (sizeXInventory / amountOfItems), posYInventory, SlotSize, SlotSize);
            }

            labels[i].setVisible(false);

            if (!inventory.getItemList(i, 0).isEmpty()) {

                float width = SlotSize - (SlotSize / inventory.getStyle().slot.getMinWidth()) * 2;
                float height = SlotSize - (SlotSize / inventory.getStyle().slot.getMinHeight()) * 2;

                Item item = (Item) inventory.getItemList(i, 0).get(0);

                item.draw(batch,
                        posXInventory + i * SlotSize + (SlotSize / inventory.getStyle().slot.getMinWidth()),
                        posYInventory + (SlotSize / inventory.getStyle().slot.getMinHeight()),
                        width,
                        height
                );

                labels[i].setPosition(posXInventory + i * SlotSize + SlotSize - labels[i].getPrefWidth() - 4.0f, posYInventory + 10.0f);
                labels[i].setVisible(true);

                stringBuilder.setLength(0);
                stringBuilder.append(inventory.getItemList(i, 0).size());
                labels[i].setText(stringBuilder);

                labels[i].draw(batch, parentAlpha);
            }
        }
    }
}

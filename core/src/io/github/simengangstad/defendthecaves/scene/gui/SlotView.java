package io.github.simengangstad.defendthecaves.scene.gui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.scene.Craftable;
import io.github.simengangstad.defendthecaves.scene.Item;

import java.util.ArrayList;

/**
 * @author simengangstad
 * @since 07/04/16
 */
public class SlotView extends Widget {

    /**
     * The dimension of the slot view.
     */
    public int columns, rows;

    /**
     * The width and height of the slots.
     */
    protected final float slotWidth, slotHeight;

    /**
     * The slot items in the slot view.
     */
    protected final ArrayList<SlotItem>[][] items;

    /**
     * The items that are slightly faded.
     */
    protected final boolean[][] faded;

    /**
     * The grafical style of the slot view; its background and slots.
     */
    protected SlotViewStyle style;

    /**
     * Initialises the slot view with a origin, a size, the amount of columns and the amount of rows.
     */
    public SlotView(Vector2 origin, Vector2 size, int columns, int rows) {

        if (origin != null && size != null) super.setBounds(origin.x, origin.y, size.x, size.y);

        this.columns = columns;
        this.rows = rows;

        faded = new boolean[columns][rows];
        items = new ArrayList[columns][rows];

        for (int x = 0;  x < columns; x++) {

            for (int y = 0; y < rows; y++) {

                items[x][y] = new ArrayList<SlotItem>();
                faded[x][y] = false;
            }
        }

        setStyle(Game.UISkin.get(SlotViewStyle.class));

        slotWidth = getWidth() / columns;
        slotHeight = getHeight() / rows;
    }

    /**
     * @return The style of the slot view.
     */
    public SlotViewStyle getStyle() {

        return style;
    }

    /**
     * Sets the style of the slot view.
     */
    public void setStyle (SlotViewStyle style) {

        if (style == null) throw new IllegalArgumentException("style cannot be null.");

        this.style = style;
    }

    public boolean isValidPosition(int x, int y) {

        return 0 <= x && x < columns && 0 <= y && y < rows;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        super.draw(batch, parentAlpha);

        for (int x = 0; x < columns; x++) {

            for (int y = 0; y < rows; y++) {

                style.slot.draw(batch, getX() + x * slotWidth, getY() + y * slotHeight, slotWidth, slotHeight);

                if (!items[x][y].isEmpty()) {

                    float width = slotWidth - (slotWidth / style.slot.getMinWidth()) * 2;
                    float height = slotHeight - (slotHeight / style.slot.getMinHeight()) * 2;

                    if (faded[x][y]) {

                        batch.setColor(1.0f, 1.0f, 1.0f, 0.5f);
                    }

                    if (items[x][y].get(0) instanceof Item) {

                        ((Item) items[x][y].get(0)).draw(batch, getX() + x * slotWidth + (slotWidth / style.slot.getMinWidth()), getY() + y * slotHeight + (slotHeight / style.slot.getMinHeight()), width, height);
                    }
                    else {

                        draw(items[x][y].get(0), batch, getX() + x * slotWidth + (slotWidth / style.slot.getMinWidth()), getY() + y * slotHeight + (slotHeight / style.slot.getMinHeight()), width, height);
                    }

                    if (faded[x][y]) {

                        batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
                    }
                }
            }
        }
    }


    /**
     * For drawing in slot views. Can't extend multiple classes, so this is the work around having this here as all slot items are items.
     */
    public void draw(SlotItem slotItem, Batch batch, float x, float y, float width, float height) {

        boolean flipped = slotItem.getSlotTextureRegion().isFlipX();

        if (flipped) {

            slotItem.getSlotTextureRegion().flip(true, false);
        }

        batch.draw(slotItem.getSlotTextureRegion(), x, y, width, height);

        if (flipped) {

            slotItem.getSlotTextureRegion().flip(true, false);
        }
    }

    /**
     * The style of the slot view.
     */
    public static class SlotViewStyle {

        public Drawable background, slot;

        public SlotViewStyle() {

        }

        public SlotViewStyle(Drawable background, Drawable slot) {

            this.background = background;
            this.slot = slot;
        }
    }

}

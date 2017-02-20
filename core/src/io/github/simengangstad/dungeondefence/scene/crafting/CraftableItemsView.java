package io.github.simengangstad.dungeondefence.scene.crafting;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.StringBuilder;
import io.github.simengangstad.dungeondefence.Game;
import io.github.simengangstad.dungeondefence.scene.Craftable;
import io.github.simengangstad.dungeondefence.scene.Entity;
import io.github.simengangstad.dungeondefence.scene.Item;
import io.github.simengangstad.dungeondefence.scene.gui.SlotItem;
import io.github.simengangstad.dungeondefence.scene.gui.SlotView;
import io.github.simengangstad.dungeondefence.scene.gui.SpeechBubble;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author simengangstad
 * @since 07/04/16
 */
public class CraftableItemsView extends SlotView implements InputProcessor {

    /**
     * Reference to the host entity (for access to its inventory).
     */
    private Entity hostEntity;

    private ArrayList<Item> itemsToObtain = new ArrayList<Item>();

    public SpeechBubble speechBubble = null;
    private Color color = new Color(1.0f, 1.0f, 1.0f, 1.0f);

    private float DisplayingTimer = 1.0f;

    private float notEnoughSpaceTimer = 0.0f;

    private static final String NotEnoughSpace = "Not enough space in inventory...";

    private StringBuilder stringBuilder = new StringBuilder();

    public Label label = null;

    public static final Sound CraftSound = Gdx.audio.newSound(Gdx.files.internal("sfx/craft.wav")), ErrorSound = Gdx.audio.newSound(Gdx.files.internal("sfx/error.wav"));

    public CraftableItemsView(Vector2 origin, Vector2 size, int columns, int rows, Entity hostEntity) {

        super(origin, size, columns, rows);

        this.hostEntity = hostEntity;

        int index = 0;
        int y = 0;

        for (Craftable craftable : Craftable.craftables) {

            if (index % columns == 0 && index != 0) {

                y++;
            }

            items[index % columns][y].add(craftable);

            index++;
        }
    }

    private boolean canBeCrafted(Recipe recipe) {

        for (int x = 0; x < hostEntity.inventory.columns; x++) {

            for (int y = 0; y < hostEntity.inventory.rows; y++) {

                if (!hostEntity.inventory.getItemList(x, y).isEmpty()) {

                    for (SlotItem item : hostEntity.inventory.getItemList(x, y)) {

                        recipe.addIngredient((Item) item);
                    }
                }
            }
        }

        if (recipe.isFulfilled()) {

            recipe.clear();

            return true;
        }
        else {

            return false;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        for (int x = 0; x < columns; x++) {

            for (int y = 0; y < rows; y++) {

                if (!items[x][y].isEmpty()) {

                    Recipe recipe = ((Craftable) items[x][y].get(0)).recipe;

                    faded[x][y] = !canBeCrafted(recipe);
                }
            }
        }

        if (label == null) {

            label = new Label("Craftable items", Game.LabelStyle16);
            label.setPosition(getX() + getWidth() / 2.0f - label.getPrefWidth() / 2.0f, getY() + getHeight());

            getStage().addActor(label);
        }

        if (speechBubble == null) {

            speechBubble = new SpeechBubble();
            speechBubble.toFront();
            speechBubble.setSize(250.0f, 100.0f);
            speechBubble.setText(NotEnoughSpace);

            getStage().addActor(speechBubble);
        }

        label.setVisible(true);

        speechBubble.setVisible(false);

        int x = Gdx.input.getX();
        int y = Math.abs(Gdx.input.getY() - (Gdx.graphics.getHeight() - 1));

        int column = (int) ((x - getX()) / slotWidth);
        int row = (int) ((y - getY()) / slotHeight);

        if (isValidPosition(column, row) && x >= getX() && x < getX() + getWidth() && y >= getY() && y < getY() + getHeight()) {

            if (items[column][row].size() != 0) {

                speechBubble.setVisible(true);

                stringBuilder.setLength(0);
                stringBuilder.append(((Craftable) items[column][row].get(0)).information);

                speechBubble.setText(stringBuilder);
            }
        }

        if (0.0f < notEnoughSpaceTimer) {

            speechBubble.setVisible(true);
            speechBubble.setPosition(Gdx.input.getX() - speechBubble.getWidth() / 2.0f, Math.abs(Gdx.input.getY() - (Gdx.graphics.getHeight() - 1)) + 15.0f);

            speechBubble.setText(NotEnoughSpace);

            notEnoughSpaceTimer -= Gdx.graphics.getDeltaTime();
        }
        else {

            speechBubble.setPosition(x - speechBubble.getWidth() / 2.0f, y + 15.0f);
        }

        super.draw(batch, parentAlpha);
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

            itemsToObtain.clear();

            if (isValidPosition(column, row) && !items[column][row].isEmpty()) {

                Recipe recipe = ((Craftable) items[column][row].get(0)).recipe;

                for (int xs = 0; xs < hostEntity.inventory.columns; xs++) {

                    for (int ys = 0; ys < hostEntity.inventory.rows; ys++) {

                        if (recipe.isFulfilled()) {

                            break;
                        }

                        if (!hostEntity.inventory.getItemList(xs, ys).isEmpty()) {

                            if (recipe.isIngredientInRecipe(hostEntity.inventory.getItemType(xs, ys))) {

                                Iterator iterator = hostEntity.inventory.getItemList(xs, ys).iterator();

                                while (iterator.hasNext()) {

                                    Item item = (Item) iterator.next();

                                    if (recipe.addIngredient(item)) {

                                        iterator.remove();
                                    }
                                    else {

                                        break;
                                    }
                                }

                                //itemsToObtain.addAll(hostEntity.inventory.getItemList(xs, ys).stream().filter(item -> ((Craftable) items[column][row].get(0)).recipe.addIngredient((Item) item)).map(item -> (Item) item).collect(Collectors.toList()));
                            }
                        }
                    }

                    if (recipe.isFulfilled()) {

                        break;
                    }
                }

                Item result = recipe.result();

                if (recipe.isFulfilled() && hostEntity.inventory.sufficientPlaceForItem(result)) {

                    for (Item item : itemsToObtain) {

                        hostEntity.inventory.removeItem(item);
                    }

                    hostEntity.addItem(result);

                    if (Game.PlaySound) CraftSound.play(0.25f);
                }
                else {

                    if (!hostEntity.inventory.sufficientPlaceForItem(result)) {

                        notEnoughSpaceTimer = DisplayingTimer;
                    }

                    itemsToObtain.clear();

                    if (Game.PlaySound) ErrorSound.play(0.75f);
                }

                recipe.clear();

                return true;
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

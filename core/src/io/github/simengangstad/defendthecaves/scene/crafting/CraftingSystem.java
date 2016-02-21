package io.github.simengangstad.defendthecaves.scene.crafting;

import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.defendthecaves.scene.Item;
import io.github.simengangstad.defendthecaves.scene.items.*;

import java.util.ArrayList;

/**
 * @author simengangstad
 * @since 17/02/16
 */
public class CraftingSystem {

    private static ArrayList<Recipe> recipes = new ArrayList<>();

    static {

        recipes.add(new Recipe(new Class[] {

                Coal.class,
                Wood.class
        }) {

            @Override
            public Item result() {

                return new Torch(new Vector2());
            }
        });
    }

    public static Item obtainItemFromGivenItems(ArrayList<Item> items) {

        for (Recipe recipe : recipes) {

            if (recipe.amountOfIngredients() == items.size()) {

                recipe.clear();

                for (Item item : items) {

                    recipe.addIngredient(item.getClass());
                }

                if (recipe.isFulfilled()) {

                    return recipe.result();
                }
            }
        }

        return null;
    }
}

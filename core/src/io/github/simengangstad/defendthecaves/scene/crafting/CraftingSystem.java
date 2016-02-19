package io.github.simengangstad.defendthecaves.scene.crafting;

import io.github.simengangstad.defendthecaves.scene.Item;
import io.github.simengangstad.defendthecaves.scene.items.Axe;
import io.github.simengangstad.defendthecaves.scene.items.Cudgel;
import io.github.simengangstad.defendthecaves.scene.items.Potion;

import java.util.ArrayList;

/**
 * @author simengangstad
 * @since 17/02/16
 */
public class CraftingSystem {

    private static Recipe recipe = new Recipe(new Class[] {

            Potion.class,
            Axe.class
    }) {

        @Override
        public Item result() {

            return new Cudgel(() -> {});
        }
    };

    public static Item obtainItemFromGivenItems(ArrayList<Item> items) {

        if (items.size() == 2) {

            recipe.clear();

            for (Item item : items) {

                recipe.addIngredient(item.getClass());
            }

            if (recipe.isFulfilled()) {

                return recipe.result();
            }
        }

        return null;
    }
}

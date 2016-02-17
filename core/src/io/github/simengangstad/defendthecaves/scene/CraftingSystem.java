package io.github.simengangstad.defendthecaves.scene;

import io.github.simengangstad.defendthecaves.scene.item.Axe;
import io.github.simengangstad.defendthecaves.scene.item.Cudgel;
import io.github.simengangstad.defendthecaves.scene.item.Potion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author simengangstad
 * @since 17/02/16
 */
public class CraftingSystem {

    private abstract static class Recipe {

        private final HashMap<Class, Boolean> ingredients = new HashMap<>();

        public Recipe(Class[] items) {

            for (Class item : items) {

                ingredients.put(item, false);
            }
        }

        public abstract Item result();

        public void setForClass(Class item, Boolean value) {

            ingredients.put(item, value);
        }

        public void clear() {

            ingredients.forEach((item, value) -> {

                value = false;
            });
        }

        public boolean isFulfilled() {

            Iterator<Boolean> iterator = ingredients.values().iterator();

            while (iterator.hasNext()) {

                if (iterator.next() == false) {

                    return false;
                }
            }

            return true;
        }
    }

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

                recipe.setForClass(item.getClass(), true);
            }

            if (recipe.isFulfilled()) {

                return recipe.result();
            }
        }

        return null;
    }
}

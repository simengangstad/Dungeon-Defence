package io.github.simengangstad.defendthecaves.scene.crafting;

import io.github.simengangstad.defendthecaves.scene.Item;

import java.util.HashMap;
import java.util.Iterator;

/**
 * @author simengangstad
 * @since 17/02/16
 */
public abstract class Recipe {

    /**
     * A reference to if the ingredients are added to the recipe.
     */
    private final HashMap<Class, Boolean> ingredients = new HashMap<>();

    public Recipe(Class[] items) {

        for (Class item : items) {

            ingredients.put(item, false);
        }
    }

    /**
     * @return The result of the ingredients.
     */
    public abstract Item result();

    /**
     * @return The amount of ingredients in the receipe.
     */
    public int amountOfIngredients() {

        return ingredients.size();
    }

    /**
     * Adds the passed ingredient.
     */
    public void addIngredient(Class ingredient) {

        ingredients.put(ingredient, true);
    }

    /**
     * Removes all the ingredients.
     */
    public void clear() {

        ingredients.forEach((item, value) -> value = false);
    }

    /**
     * @return If the recipe is complete.
     */
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

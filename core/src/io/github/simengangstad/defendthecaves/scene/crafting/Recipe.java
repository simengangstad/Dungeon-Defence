package io.github.simengangstad.defendthecaves.scene.crafting;

import io.github.simengangstad.defendthecaves.scene.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * @author simengangstad
 * @since 17/02/16
 */
public abstract class Recipe {

    public final int id;

    private final int count;

    /**
     * A reference to the ingredients (and the quantity of these ingredients) required for the recipe.
     */
    private final HashMap<Class, Integer> ingredients = new HashMap<Class, Integer>();

    private final HashMap<Class, Integer> addedIngredients = new HashMap<Class, Integer>();

    /**
     * Reference to the actual items.
     */
    private final ArrayList<Item> items = new ArrayList<Item>();

    /**
     * See {@link Recipe#getIngredientsByType(Class)}
     */
    private final ArrayList<Item> tmpIngredients = new ArrayList<Item>();

    public Recipe(int id, Class[] items) {

        this.id = id;

        for (Class item : items) {

            if (ingredients.get(item) == null) {

                ingredients.put(item, 1);
            }
            else {

                ingredients.put(item, ingredients.get(item) + 1);
            }
        }

        count = items.length;
    }

    /**
     * @return If the given type is an ingredient of the recipe.
     */
    public boolean isIngredientInRecipe(Class type) {

        return ingredients.get(type) != null;
    }

    /**
     * @return The result of the ingredients.
     */
    public abstract Item result();

    /**
     * @return The ingredients with the given type.
     */
    public ArrayList<Item> getIngredientsByType(Class type) {

        tmpIngredients.clear();

        for (Item item : items) {

            if (item.getClass().equals(type)) {

                tmpIngredients.add(item);
            }
        }

        return tmpIngredients;
    }

    /**
     * @return The amount of ingredients in the receipe.
     */
    public int amountOfIngredients() {

        return ingredients.size();
    }

    /**
     * @return The total amount of all the quantities of each ingredient added togheter.
     */
    public int amountOfQuantities() {

        return count;
    }

    /**
     * Adds the passed ingredient.
     *
     * @return True if the given ingredient wasn't already added.
     */
    public boolean addIngredient(Item ingredient) {

        if (ingredients.get(ingredient.getClass()) != null) {

            if (addedIngredients.get(ingredient.getClass()) == null || addedIngredients.get(ingredient.getClass()) == 0) {

                addedIngredients.put(ingredient.getClass(), 1);

                items.add(ingredient);

                return true;
            }
            else if (addedIngredients.get(ingredient.getClass()) < ingredients.get(ingredient.getClass())) {

                addedIngredients.put(ingredient.getClass(), addedIngredients.get(ingredient.getClass()) + 1);

                items.add(ingredient);

                return true;
            }
        }

        return false;
    }

    /**
     * Removes all the ingredients.
     */
    public void clear() {

        for (Class item : addedIngredients.keySet()) {

            addedIngredients.put(item, 0);
        }

        items.clear();
    }

    /**
     * @return If the recipe is complete.
     */
    public boolean isFulfilled() {

        Iterator<Class> iterator = ingredients.keySet().iterator();

        while (iterator.hasNext()) {

            Class key = iterator.next();

            if (addedIngredients.get(key) != ingredients.get(key)) {

                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {

        final StringBuilder information = new StringBuilder();

        information.append("Recipe:\n");

        for (Class id : ingredients.keySet()) {

            information.append(id + " - added: " + ingredients.get(id) + "\n");
        }

        return information.toString();
    }
}

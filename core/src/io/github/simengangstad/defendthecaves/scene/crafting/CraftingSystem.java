package io.github.simengangstad.defendthecaves.scene.crafting;

import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.defendthecaves.scene.Item;
import io.github.simengangstad.defendthecaves.scene.items.*;
import sun.jvm.hotspot.debugger.cdbg.PointerType;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author simengangstad
 * @since 17/02/16
 */
public class CraftingSystem {

    private static ArrayList<Recipe> recipes = new ArrayList<>();

    private static ArrayList<Item> itemsCopy = new ArrayList<>();

    static {

        recipes.add(new Recipe(0, new Class[] {

                Coal.class,
                Wood.class
        }) {

            @Override
            public Item result() {

                return new Torch(new Vector2());
            }
        });

        recipes.add(new Recipe(1, new Class[] {

                Wood.class,
                Wood.class,
                Wood.class,
                Potion.class
        }) {

            @Override
            public Item result() {
                return new StepTrap(new Vector2());
            }
        });
    }

    public static class Product {

        public ArrayList<Item> items = new ArrayList<>();
        public Recipe recipe;
    }

    private static Product product = new Product();

    public static Product obtainItemFromGivenItems(ArrayList<Item> items) {

        for (Recipe recipe : recipes) {

            product.items.clear();

            product.recipe = recipe;

            // Check that the items given are the amount of required ingredients times
            // some integer
            if (items.size() % recipe.amountOfIngredients() == 0) {

                // Check that the items given are sufficient for n amount of result items
                itemsCopy.clear();
                itemsCopy.addAll(items);

                boolean noMoreResultingItems = false;
                boolean addedItems = false;

                while (!noMoreResultingItems) {

                    noMoreResultingItems = true;

                    // Grab the ingredients in the list and look for the other corresponding
                    // ingredients for the recipe
                    recipe.clear();

                    Iterator itemIterator = itemsCopy.iterator();

                    // Iterate through and remove the added ingredients so that we don't include
                    // these items in the next iteration.
                    while (itemIterator.hasNext()) {

                        Item next = (Item) itemIterator.next();

                        if (recipe.addIngredient(next)) {

                            itemIterator.remove();

                            if (recipe.isFulfilled()) {

                                Item result = recipe.result();

                                if (recipe.id == 1) {

                                    Potion potion = (Potion) recipe.getIngredientsByType(Potion.class).get(0);

                                    ((StepTrap) result).potion = potion;
                                }

                                product.items.add(result);

                                addedItems = true;

                                noMoreResultingItems = false;

                                break;
                            }
                        }
                    }

                    System.out.println("Something crafting system");
                }

                // If we added items with this recipe, don't look for other combinations
                if (addedItems) {

                    return product;
                }
            }
        }

        return product;
    }
}

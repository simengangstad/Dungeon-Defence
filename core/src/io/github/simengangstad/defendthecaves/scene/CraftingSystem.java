package io.github.simengangstad.defendthecaves.scene;

import io.github.simengangstad.defendthecaves.scene.item.Cudgel;
import io.github.simengangstad.defendthecaves.scene.item.Rock;

/**
 * @author simengangstad
 * @since 17/02/16
 */
public class CraftingSystem {

    private static class Craftable {

        public Class id;
        public boolean added;

        public Craftable(Class id, boolean added) {

            this.id = id;
            this.added = added;
        }
    }

    private static final Craftable[] test = new Craftable[] {

            new Craftable(Cudgel.class, false),
            new Craftable(Rock.class, false)
    };

    public static Item obtainItemFromGivenItems(Item... items) {

        if (items.length == 2) {



            for ()
        }
    }
}

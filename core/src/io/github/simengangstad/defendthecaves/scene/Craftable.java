package io.github.simengangstad.defendthecaves.scene;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.defendthecaves.scene.crafting.Recipe;
import io.github.simengangstad.defendthecaves.scene.gui.SlotItem;
import io.github.simengangstad.defendthecaves.scene.items.*;

import java.util.ArrayList;

/**
 * @author simengangstad
 * @since 07/04/16
 */
public class Craftable implements SlotItem {

    public static ArrayList<Craftable> craftables = new ArrayList<>();

    static {

        craftables.add(new Craftable(new Recipe(0, new Class[] {

                Coal.class,
                Wood.class
            }
        ) {

            @Override
            public Item result() {

                return new Torch(new Vector2());
            }
        }, Torch.animation.getKeyFrame(0.0f), Torch.CraftInformation));


        craftables.add(new Craftable(new Recipe(1, new Class[] {

                ExplosivePotion.class,
                Wood.class,
                Wood.class,
                Wood.class
        }
        ) {

            @Override
            public Item result() {

                return new StepTrap(new Vector2());
            }
        }, StepTrap.animation.getKeyFrame(0.0f), StepTrap.CraftInformation));


        // Arrows

        // Stone
        craftables.add(new Craftable(new Recipe(2, new Class[] {

                Rock.class,
                Wood.class,
                StringItem.class
        }) {

            @Override
            public Item result() {

                return new Arrow(new Vector2(), new Vector2(), 0, 0, null, null);
            }
        }, Arrow.projectingAnimations[0].getKeyFrame(0.0f), Arrow.StoneCraftInformation));


        // Explosive
        craftables.add(new Craftable(new Recipe(3, new Class[] {

                ExplosivePotion.class,
                Rock.class,
                Wood.class,
                StringItem.class
        }) {

            @Override
            public Item result() {

                return new Arrow(new Vector2(), new Vector2(), 1, 0, null, null);
            }
        }, Arrow.explosiveArrow, Arrow.ExplosiveCraftInformation));

        // Toxic
        craftables.add(new Craftable(new Recipe(4, new Class[] {

                ToxicPotion.class,
                Rock.class,
                Wood.class,
                StringItem.class
        }) {

            @Override
            public Item result() {

                return new Arrow(new Vector2(), new Vector2(), 2, 0, null, null);
            }
        }, Arrow.toxicArrow, Arrow.ToxicCraftInformation));
    }

    /**
     * The recipe of the craftable item.
     */
    public final Recipe recipe;

    /**
     * The texture region of the craftable item.
     */
    public final TextureRegion textureRegion;

    public final String information;

    public String craftInformation = "";

    public Craftable(Recipe recipe, TextureRegion textureRegion, String information) {

        this.recipe = recipe;
        this.textureRegion = textureRegion;
        this.information = information;
    }

    @Override
    public TextureRegion getSlotTextureRegion() {

        return textureRegion;
    }
}

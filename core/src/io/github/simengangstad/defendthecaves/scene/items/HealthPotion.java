package io.github.simengangstad.defendthecaves.scene.items;

import com.badlogic.gdx.math.Vector2;

/**
 * @author simengangstad
 * @since 07/04/16
 */
public class HealthPotion extends Potion {

    public HealthPotion(Vector2 position) {

        super(position);

        addChemical(new Chemical(50, -50, -50));

        information = "Health potion";
    }
}

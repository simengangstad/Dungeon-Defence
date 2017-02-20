package io.github.simengangstad.dungeondefence.scene.items;

import com.badlogic.gdx.math.Vector2;

/**
 * @author simengangstad
 * @since 07/04/16
 */
public class ExplosivePotion extends Potion {

    public ExplosivePotion(Vector2 position) {

        super(position);

        addChemical(new Chemical(-50, 50, 50));

        information = "Explosive potion";
    }
}

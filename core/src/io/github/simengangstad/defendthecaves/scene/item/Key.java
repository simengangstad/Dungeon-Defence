package io.github.simengangstad.defendthecaves.scene.item;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.pathfinding.Coordinate;
import io.github.simengangstad.defendthecaves.scene.Item;

/**
 * @author simengangstad
 * @since 17/01/16
 */
public class Key extends Item {

    public final Coordinate positionOfDoor;

    public Key(Vector2 position, Coordinate positionOfDoor) {

        super(position, new Vector2(Game.EntitySize, Game.EntitySize / 2.0f), new TextureRegion(Game.SpriteSheet, 0, 208, 16, 16), false);

        this.positionOfDoor = positionOfDoor;
    }

    @Override
    public void interact(Vector2 direciton) {

    }
}

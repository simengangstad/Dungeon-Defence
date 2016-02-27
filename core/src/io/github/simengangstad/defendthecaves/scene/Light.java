package io.github.simengangstad.defendthecaves.scene;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * @author simengangstad
 * @since 25/02/16
 */
public class Light {

    public final Vector2 position;

    private final float[] positionArray = new float[2];

    public final Vector3 colour;

    private final float[] colourArray = new float[3];

    public final float range;

    private final float[] rangeArray = new float[1];

    public Light(Vector2 position, Vector3 colour, float range) {

        this.position = position;
        this.colour = colour;
        this.range = range;
    }

    public float[] getPositionArray() {

        positionArray[0] = position.x;
        positionArray[1] = position.y;

        return positionArray;
    }

    public float[] getColourArray() {

        colourArray[0] = colour.x;
        colourArray[1] = colour.y;
        colourArray[2] = colour.z;

        return colourArray;
    }

    public float[] getRangeArray() {

        rangeArray[0] = range;

        return rangeArray;
    }
}

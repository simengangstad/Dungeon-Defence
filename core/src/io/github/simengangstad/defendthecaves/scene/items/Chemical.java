package io.github.simengangstad.defendthecaves.scene.items;

import com.badlogic.gdx.math.MathUtils;

/**
 * @author simengangstad
 * @since 23/01/16
 */
public class Chemical {

    public final static int LowerBoundary = -50, UpperBoundary = 50;

    /**
     * Properties of a chemical from a scale of -50 to 50, where 50 is extreme conditions of the
     * property and -50 is the opposite of the given property.
     */
    public final int stability, toxicity, flammability;

    public Chemical(int stability, int toxicity, int flammability) {

        this.stability = MathUtils.clamp(stability, LowerBoundary, UpperBoundary);
        this.toxicity = MathUtils.clamp(toxicity, LowerBoundary, UpperBoundary);
        this.flammability = MathUtils.clamp(flammability, LowerBoundary, UpperBoundary);
    }

    /**
     * Generates a random chemical.
     */
    public Chemical() {

        this(MathUtils.random(LowerBoundary, UpperBoundary), MathUtils.random(LowerBoundary, UpperBoundary), MathUtils.random(LowerBoundary, UpperBoundary));
    }
}

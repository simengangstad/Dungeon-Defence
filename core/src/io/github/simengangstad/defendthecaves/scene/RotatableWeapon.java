package io.github.simengangstad.defendthecaves.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.defendthecaves.Callback;

/**
 * @author simengangstad
 * @since 29/11/15
 */
public abstract class RotatableWeapon extends Weapon {

    /**
     * The maximum and minimum rotation.
     */
    private final int minRotation, maxRotation;

    /**
     * Rotation of the weapon in degrees.
     */
    protected int rotation = 0;


    private boolean initialised = false;

    /**
     * The different texture regions for the given rotations.
     */
    private TextureRegion[] weaponTextureRegions, attackTextureRegions;

    /**
     * The current texture regions.
     */
    protected TextureRegion currentWeaponTextureRegion;

    /**
     * The last mouse position.
     */
    private int lastMouseXPosition, lastMouseYPosition;


    // Attacking

    protected boolean attackTextureRegionIsFlipped = false;

    private int weaponIndexBeforeAttacking = 0;

    public RotatableWeapon(int attackDamage, float attackDuration, Callback interactionCallback, int minRotation, int maxRotation) {

        super(attackDamage, attackDuration, interactionCallback);

        this.maxRotation = maxRotation;
        this.minRotation = minRotation;
    }

    protected void setTextures(TextureRegion[] weaponTextureRegions, TextureRegion[] attackTextureRegions) {

        this.weaponTextureRegions = weaponTextureRegions;
        this.attackTextureRegions = attackTextureRegions;

        currentWeaponTextureRegion = weaponTextureRegions[0];

        initialised = true;
    }

    public void setRotation(int rotation) {

        this.rotation = rotation;
    }

    @Override
    public void interact(Vector2 location) {

        super.interact(location);

        // Check that we only apply logic one time per attack duration by checking
        // that we just set the state time.
        if (getStateTime() == interactionDuration) {

            attackTextureRegionIsFlipped = flip();
        }
    }

    @Override
    public void tick() {

        super.tick();

        if (!initialised) return;

        if (lastMouseXPosition != Gdx.input.getX() || lastMouseYPosition != Gdx.input.getY() && !isInteracting()) {

            int angle = rotation;

            if (flip) {

                angle = -angle;
            }

            int delta = (maxRotation - minRotation) / weaponTextureRegions.length;
            int textureRegionIndex;

            if (maxRotation < angle) {

                textureRegionIndex = weaponTextureRegions.length - 1;
            }
            else if (angle < minRotation) {

                textureRegionIndex = 0;
            }
            else {

                int index = (angle - minRotation) / delta;

                index = Math.max(index, 0);
                index = Math.min(index, weaponTextureRegions.length - 1);

                textureRegionIndex = index;
            }

            currentWeaponTextureRegion = weaponTextureRegions[textureRegionIndex];

            weaponIndexBeforeAttacking = textureRegionIndex;

            lastMouseXPosition = Gdx.input.getX();
            lastMouseYPosition = Gdx.input.getY();
        }
        else if (isInteracting()) {

            int index = (int) Math.abs(getStateTime() / (interactionDuration / attackTextureRegions.length) - attackTextureRegions.length);

            currentWeaponTextureRegion = attackTextureRegions[index];
        }
        else {

            currentWeaponTextureRegion = weaponTextureRegions[weaponIndexBeforeAttacking];
        }
    }

    @Override
    public TextureRegion getTextureRegion() {

        return currentWeaponTextureRegion;
    }
}

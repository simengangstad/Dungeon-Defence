package io.github.simengangstad.dungeondefence.scene.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.simengangstad.dungeondefence.Callback;

/**
 * @author simengangstad
 * @since 29/11/15
 */
public abstract class RotatableWeapon extends Weapon {

    /**
     * The maximum and minimum rotation.
     */
    protected final int minRotation, maxRotation;

    /**
     * Rotation of the weapon in degrees.
     */
    protected int rotation = 0;

    /**
     * The different texture regions for the given rotations.
     */
    protected TextureRegion[] weaponTextureRegions, attackTextureRegions;

    /**
     * The current texture regions.
     */
    protected TextureRegion currentWeaponTextureRegion;

    /**
     * The last mouse position.
     */
    protected int lastMouseXPosition, lastMouseYPosition;

    /**
     * The index of the texture region before the attack took place. Used for setting the
     * {@link RotatableWeapon#currentWeaponTextureRegion} back to the texture region
     * that initially was.
     */
    protected int weaponIndexBeforeAttacking = 0;

    /**
     * Initializes the rotatable weapon.
     */
    public RotatableWeapon(int attackDamage, float attackDuration, Callback interactionCallback, int minRotation, int maxRotation, TextureRegion[] weaponTextureRegions, TextureRegion[] attackTextureRegions) {

        super(attackDamage, attackDuration, null, interactionCallback);

        this.maxRotation = maxRotation;
        this.minRotation = minRotation;

        this.weaponTextureRegions = weaponTextureRegions;
        this.attackTextureRegions = attackTextureRegions;

        currentWeaponTextureRegion = weaponTextureRegions[0];
    }

    /**
     * Sets the rotation of the rotatable weapon.
     */
    public void setRotation(int rotation) {

        this.rotation = rotation;
    }

    @Override
    public void tick() {

        super.tick();

        if (weaponTextureRegions == null) {

            throw new RuntimeException("Texture regions not set");
        }

        if (lastMouseXPosition != Gdx.input.getX() || lastMouseYPosition != Gdx.input.getY() && !isAttacking()) {

            int angle = rotation;

            if (flip()) {

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
        else if (isAttacking()) {

            int index = (int) Math.abs(getTimeLeftOfAttack() / (attackDuration / attackTextureRegions.length) - attackTextureRegions.length);

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

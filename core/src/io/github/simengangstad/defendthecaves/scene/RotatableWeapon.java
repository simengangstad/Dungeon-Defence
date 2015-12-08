package io.github.simengangstad.defendthecaves.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

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

    /**
     * The different texture regions for the given rotations.
     */
    private final TextureRegion[] weaponTextureRegions, attackTextureRegions;

    /**
     * The current texture regions.
     */
    protected TextureRegion currentWeaponTextureRegion, currentAttackTextureRegion;

    /**
     * The last mouse position.
     */
    private int lastMouseXPosition, lastMouseYPosition;

    protected boolean attackTextureRegionIsFlipped = false;
    protected int currentAttackTextureRegionIndex = 0;
    protected int weaponIndex = 0;
    private int lastWeaponIndex = 0;


    public RotatableWeapon(int attackDamage, int attackRange, int minRotation, int maxRotation, TextureRegion[] weaponTextureRegions, TextureRegion[] attackTextureRegions) {

        super(attackDamage, attackRange, 0.3f);

        this.maxRotation = maxRotation;
        this.minRotation = minRotation;

        this.weaponTextureRegions = weaponTextureRegions;
        this.attackTextureRegions = attackTextureRegions;

        currentWeaponTextureRegion = weaponTextureRegions[weaponIndex];
        currentAttackTextureRegion = attackTextureRegions[0];
    }

    public void setRotation(int rotation) {

        this.rotation = rotation;
    }

    @Override
    public void attack(Vector2 location) {

        super.attack(location);

        // Check that we only apply logic one time per attack duration by checking
        // that we just set the state time.
        if (getStateTime() == attackDuration) {

            time = duration;

            attackTextureRegionIsFlipped = flip();

            int angle = rotation + 90;

            if (flip) {

                angle = -(angle - 180);
            }

            int delta = (maxRotation - minRotation) / weaponTextureRegions.length;

            if (maxRotation < angle) {

                ///currentAttackTextureRegionIndex = weaponTextureRegions.length - 1;
            }
            else if (angle < minRotation) {

                //currentAttackTextureRegionIndex = 0;
            }
            else {

                int index = (angle - minRotation) / delta;

                index = Math.max(index, 0);
                index = Math.min(index, weaponTextureRegions.length - 1);

                //currentAttackTextureRegionIndex = index;
            }
        }
    }

    private float duration = 0.01f;
    private float time = 0.0f;

    @Override
    public void tick() {

        super.tick();

        if (0.0f < time - Gdx.graphics.getDeltaTime()) {

            time -= Gdx.graphics.getDeltaTime();
        }
        else {

            time = 0.0f;
        }

        if (lastMouseXPosition != Gdx.input.getX() || lastMouseYPosition != Gdx.input.getY()) {

            int angle = rotation + 90;

            if (flip) {

                angle = -(angle - 180);
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

            //currentWeaponTextureRegion = weaponTextureRegions[textureRegionIndex];

            lastMouseXPosition = Gdx.input.getX();
            lastMouseYPosition = Gdx.input.getY();
        }

        if (isAttacking()) {

            if (lastWeaponIndex == 0) {

                weaponIndex = Math.round((weaponTextureRegions.length - 1) * (Math.abs(time - duration)) / duration);
            }
            else {

                weaponIndex = Math.round((weaponTextureRegions.length - 1) * time / duration);
            }
        }
        else {


            if (weaponIndex == weaponTextureRegions.length - 1) {

                lastWeaponIndex = weaponTextureRegions.length - 1;
            }
            else {

                lastWeaponIndex = 0;
            }
        }

        currentAttackTextureRegion = attackTextureRegions[currentAttackTextureRegionIndex];
    }

    @Override
    public TextureRegion getTextureRegion() {

        return weaponTextureRegions[weaponIndex];
    }
}

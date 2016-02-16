package io.github.simengangstad.defendthecaves.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import io.github.simengangstad.defendthecaves.Callback;
import io.github.simengangstad.defendthecaves.Game;

/**
 * @author simengangstad
 * @since 28/11/15
 */
public abstract class Weapon extends Item {

    /**
     * If the weapon is in the process of attacking.
     */
    protected boolean attacking = false;

    /**
     * The duration of the attack.
     */
    public final float attackDuration;

    /**
     * The time left of the attack.
     */
    protected float timeLeftOfAttack = 0.0f;

    /**
     * The callback called when the attack has finished.
     */
    private final Callback callback;

    /**
     * The attack damage.
     */
    public final int attackDamage;

    /**
     * The direction of the attack.
     */
    private final Vector2 direction = new Vector2();

    /**
     * If the drawable is flipped during attack.
     */
    protected boolean isFlippedDuringAttack = false;

    /**
     * Initializes the weapon.
     */
    public Weapon(int attackDamage, float attackDuration, TextureRegion textureRegion, Callback callback) {

        super(new Vector2(), new Vector2(Game.EntitySize, Game.EntitySize), textureRegion, false);

        this.attackDuration = attackDuration;
        this.callback = callback;

        this.attackDamage = attackDamage;
    }

    @Override
    public void interact(Vector2 direction) {

        if (0 < getTimeLeftOfAttack()) {

            return;
        }

        this.direction.set(direction.nor());

        attacking = true;

        isFlippedDuringAttack = flip();

        timeLeftOfAttack = attackDuration;
    }

    /**
     * @return If the weapon is currently attacking.
     */
    public boolean isAttacking() {

        return attacking;
    }

    /**
     * @return The time left of the attacki.
     */
    public float getTimeLeftOfAttack() {

        return timeLeftOfAttack;
    }

    @Override
    public boolean flip() {

        if (0 < getTimeLeftOfAttack()) {

            return isFlippedDuringAttack;
        }

        return parent.flip();
    }

    /**
     * Updates the state of the weapon.
     */
    public void tick() {

        super.tick();

        if (attacking) {

            timeLeftOfAttack -= Gdx.graphics.getDeltaTime();

            if (timeLeftOfAttack < 0.0f) {

                attacking = false;

                timeLeftOfAttack = 0.0f;

                // Hurt the entities that are in boundaries of the weapon if hit
                ((Scene) parent.host).damage(attackDamage, direction, parent.position.x + (flip() ? -size.x / 2.0f : size.x / 2.0f), parent.position.y, size.x, size.y, parent);

                callback.callback();
            }
        }
    }

    @Override
    public void draw(SpriteBatch batch) {

        super.draw(batch);

        if (Game.DebubDraw) {

            batch.draw(Game.debugDrawTexture, parent.position.x - (flip() ? size.x : 0), parent.position.y - size.y / 2.0f, size.x, size.y);
            batch.draw(Game.debugDrawTexture, parent.position.x - (flip() ? this.size.x : 0), parent.position.y - this.size.y / 2.0f, this.size.x, this.size.y);
        }
    }
}

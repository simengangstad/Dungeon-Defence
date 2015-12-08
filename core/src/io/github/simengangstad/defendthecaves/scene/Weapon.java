package io.github.simengangstad.defendthecaves.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.components.Drawable;

/**
 * @author simengangstad
 * @since 28/11/15
 */
public abstract class Weapon implements Drawable {

    public Entity parent;

    /**
     * The attack damage.
     */
    public final int attackDamage;

    /**
     * The attack range.
     */
    public final float attackRange;

    /**
     * If the drawable should be flipped.
     */
    public boolean flip = false;

    /**
     * If the weapon is attacking.
     */
    private boolean attacking = false;

    /**
     * The duration of the attack.
     */
    public final float attackDuration;

    /**
     * The time left of the attack.
     */
    private float stateTime = 0.0f;

    public Weapon(int attackDamage, int attackRange, float attackDuration) {

        this.attackDamage = attackDamage;
        this.attackRange = attackRange;
        this.attackDuration = attackDuration;
    }

    public boolean isAttacking() {

        return attacking;
    }

    public float getStateTime() {

        return stateTime;
    }

    @Override
    public boolean flip() {

        return flip;
    }

    /**
     * Makes the weapon attack.
     */
    public void attack(Vector2 direction) {

        if (0 < stateTime) {

            return;
        }

        tmpDirection.set(direction.nor());

        float xDelta;

        if (flip()) {

            xDelta = parent.getSize().x / 2.0f;
        }
        else {

            xDelta = parent.getSize().x / 2.0f - attackRange;
        }

        parent.host.damage(this, parent.getPosition().x + xDelta, parent.getPosition().y + parent.getSize().y / 2.0f - attackRange / 2.0f, attackRange, attackRange, parent);

        attacking = true;

        stateTime = attackDuration;
    }

    /**
     * Updates the state of the weapon in an attack.
     */
    public void tick() {

        if (attacking) {

            stateTime -= Gdx.graphics.getDeltaTime();
        }

        if (stateTime < 0.0f) {

            attacking = false;
        }
    }

    private Vector2 tmpDirection = new Vector2();

    @Override
    public void draw(SpriteBatch batch, Vector2 position, Vector2 size) {

        if (Game.DebubDraw) {

            float xDelta;

            if (flip()) {

                xDelta = parent.getSize().x / 2.0f;
            }
            else {

                xDelta = parent.getSize().x / 2.0f - attackRange;
            }

            batch.draw(Game.debugDrawTexture, parent.getPosition().x + xDelta, parent.getPosition().y + parent.getSize().y / 2.0f - attackRange / 2.0f, attackRange, attackRange);
        }
    }
}

package io.github.simengangstad.defendthecaves.scene;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.defendthecaves.Callback;
import io.github.simengangstad.defendthecaves.Game;

/**
 * @author simengangstad
 * @since 28/11/15
 */
public abstract class Weapon extends Tool {

    /**
     * The attack damage.
     */
    public final int attackDamage;

    private final Vector2 direction = new Vector2();

    public Weapon(int attackDamage, float attackDuration, Callback interactionCallback) {

        super(attackDuration, interactionCallback);

        this.attackDamage = attackDamage;
    }

    @Override
    protected void interacting(Vector2 interactingDirection) {

        direction.set(interactingDirection.nor());
    }

    @Override
    protected void doneInteracting() {

        ((Scene) parent.host).damage(attackDamage, direction, parent.getPosition().x + (!flip() ? -size.x / 2.0f : size.x / 2.0f), parent.getPosition().y, size.x, size.y, parent);
    }
}

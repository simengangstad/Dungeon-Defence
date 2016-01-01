package io.github.simengangstad.defendthecaves.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.components.Drawable;
import io.github.simengangstad.defendthecaves.components.GameObject;

import java.util.ArrayList;

/**
 * @author simengangstad
 * @since 28/11/15
 */
public abstract class Entity extends GameObject implements Drawable {

    // Weapons

    protected final ArrayList<Tool> tools = new ArrayList<>();

    protected Tool currentTool = null;

    private float IntervalBetweenInteractions = 0.25f;

    private float timeLeftBeforeBeginAbleToInteract = 0.0f;


    // Properties

    public int health = 100;

    public int speed = 300;


    // Taking damage

    private final float DamageTime = 0.4f;

    private float timeLeftOfTakingDamage = 0.0f;


    // Paralysis which happens after the entity has taken damage

    private float TimeParalyzed = 0.5f;

    private float timeLeftOfParalysis = 0;


    // Variables relating the flicker overlay once taken damage.

    private static int uniformLocation = -1;

    private final int AmountOfFlickers = 3;

    private final float FlickersInterval = DamageTime / (AmountOfFlickers * 2);


    private Vector2 tmpVector = new Vector2();

    public Entity(Vector2 position, Vector2 size) {

        super(position, size);
    }

    /**
     * Shuffles the tool so that the next tool in the tool list becomes the current one.
     */
    public void shuffleTool() {

        currentTool = tools.get((tools.indexOf(currentTool) + 1) % tools.size());
    }

    public void attachTool(Tool tool) {

        tools.add(tool);

        tool.parent = this;

        currentTool = tool;
    }

    public void detachTool(Tool tool) {

        tools.remove(tool);

        tool.parent = null;
    }

    public void interact(Vector2 interactionDirection) {

        if (timeLeftBeforeBeginAbleToInteract == 0.0f) {

            timeLeftBeforeBeginAbleToInteract = IntervalBetweenInteractions;

            currentTool.interact(interactionDirection);
        }
    }

    public void paralyse() {

        timeLeftOfParalysis = TimeParalyzed;
    }

    public boolean isParalysed() {

        return 0 < timeLeftOfParalysis;
    }

    public void takeDamage(int damage) {

        health -= damage;

        timeLeftOfTakingDamage = DamageTime;
    }

    @Override
    public void tick() {

        if (0.0f < timeLeftOfParalysis) {

            timeLeftOfParalysis -= Gdx.graphics.getDeltaTime();
        }
        else if (timeLeftOfParalysis < 0.0f) {

            timeLeftOfParalysis = 0.0f;
        }

        if (0.0f < timeLeftBeforeBeginAbleToInteract) {

            timeLeftBeforeBeginAbleToInteract -= Gdx.graphics.getDeltaTime();
        }
        else if (timeLeftBeforeBeginAbleToInteract < 0.0f) {

            timeLeftBeforeBeginAbleToInteract = 0.0f;
        }

        currentTool.tick();
    }

    @Override
    public void draw(SpriteBatch batch, Vector2 position, Vector2 size) {

        // Set the uniform location of the white overlay if not previous set.
        if (uniformLocation == -1) uniformLocation = batch.getShader().getUniformLocation("u_flash");

        if (0.0f < timeLeftOfTakingDamage) timeLeftOfTakingDamage -= Gdx.graphics.getDeltaTime();

        if (flip()) getTextureRegion().flip(true, false);

        if (Game.DebubDraw) {

            batch.draw(Game.debugDrawTexture, position.x - size.x / 2.0f, position.y - size.y / 2.0f, size.x, size.y);
        }

        // Shadow under the entity
        batch.draw(getShadowTextureRegion(), position.x - size.x / 2.0f, position.y - (size.y / 16) - size.y / 2.0f, size.x, size.y);


        // If the entity is taking a damage we apply a white overlay over the entity and its weapons
        // in the form of a white flash.
        if (0.0f < timeLeftOfTakingDamage && (int) (timeLeftOfTakingDamage / FlickersInterval) % 2 == 1) {

            batch.flush();

            batch.getShader().setUniformi(uniformLocation, 1);
        }

        batch.draw(getTextureRegion(), position.x - size.x / 2.0f, position.y - size.y / 2.0f, size.x, size.y);

        tmpVector.set(position.x - size.x / 2.0f, position.y - size.y / 2.0f);

        currentTool.draw(batch, tmpVector, size);

        // We reset so that the overlay is only applied to this entity.
        if (0.0f < timeLeftOfTakingDamage && (int) (timeLeftOfTakingDamage / FlickersInterval) % 2 == 1) {

            batch.flush();

            batch.getShader().setUniformi(uniformLocation, 0);
        }

        if (flip()) getTextureRegion().flip(true, false);
    }

    protected abstract TextureRegion getShadowTextureRegion();
}

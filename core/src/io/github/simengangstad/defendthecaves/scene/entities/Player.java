package io.github.simengangstad.defendthecaves.scene.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.Map;
import io.github.simengangstad.defendthecaves.scene.Entity;
import io.github.simengangstad.defendthecaves.scene.Weapon;
import io.github.simengangstad.defendthecaves.scene.weapons.Axe;

/**
 * @author simengangstad
 * @since 10/11/15
 */
public class Player extends Entity {

    /**
     * The camera of the player.
     */
    public final Camera camera;

    /**
     * The speed of the player in pixels per second.
     */
    public int speed = 300;

    /**
     * The map the player is located in.
     */
    private Map map;

    /**
     * Vector used for calculating direction.
     */
    private Vector2 tmpDirection = new Vector2();

    /**
     * The texture for the stationary animation.
     */
    private Texture stationaryTexture;

    /**
     * The animation when the player is standing still.
     */
    private Animation stationaryAnimation;

    /**
     * The texture for the walking animations.
     */
    private Texture walkingTexture;

    /**
     * The animation when the player is moving.
     */
    private Animation walkingAnimation;

    /**
     * If the player is facing right.
     */
    private boolean facingRight = true;

    /**
     * If the player is going backwards. Used for playing the walking loop backwards.
     */
    private boolean goingBackwards = false;

    /**
     * The current animation playing.
     */
    private Animation currentAnimation;

    /**
     * The current texture region.
     */
    private TextureRegion currentTextureRegion;

    /**
     * The state time.
     */
    private float stateTime = 0.0f;

    /**
     * If the player is attacking or not.
     */
    private boolean attacking = false;

    /**
     * The tile size of the player sprites in the sprite sheet.
     */
    private final int TileSize = 16;

    private Axe axe;

    private Vector3 tmpVec = new Vector3();

    private boolean raiseAxe = false;

    private TextureRegion shadow = new TextureRegion(Game.spriteSheet, 0, 0, 16, 16);

    /**
     * Initializes the player with a camera.
     */
    public Player(Camera camera) {

        super(new Vector2(), new Vector2(80.0f, 80.0f));

        this.camera = camera;

        axe = new Axe();

        attachWeapon(axe);

        walkingTexture = new Texture("assets/animations/PlayerWalking.png");

        Array<TextureRegion> walkingTextureRegions = new Array<>();

        for (int i = 0; i < walkingTexture.getWidth() / TileSize; i++) {

            walkingTextureRegions.add(new TextureRegion(walkingTexture, i * TileSize, 0, TileSize, TileSize));
        }

        walkingAnimation = new Animation(0.075f, walkingTextureRegions);


        stationaryTexture = new Texture("assets/animations/PlayerStationary.png");

        Array<TextureRegion> stationaryTextureRegions = new Array<>();

        for (int i = 0; i < stationaryTexture.getWidth() / TileSize; i++) {

            stationaryTextureRegions.add(new TextureRegion(stationaryTexture, i * TileSize, 0, TileSize, TileSize));
        }

        stationaryAnimation = new Animation(0.2f, stationaryTextureRegions);

        currentTextureRegion = stationaryAnimation.getKeyFrame(stateTime, true);
    }

    /**
     * Sets the map the player resolves its collision against.
     */
    public void setMap(Map map) {

        this.map = map;
    }

    @Override
    public void tick() {

        if (map == null) {

            throw new RuntimeException("Need an assigned map in order to resolve collisions.");
        }

        tmpDirection.set(0.0f, 0.0f);

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {

            tmpDirection.add(0.0f, 1.0f);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S)) {

            tmpDirection.add(0.0f, -1.0f);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {

            tmpDirection.add(-1.0f, 0.0f);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {

            tmpDirection.add(1.0f, 0.0f);
        }

        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {

            tmpVec = camera.unproject(tmpVec.set(Gdx.input.getX(), Gdx.input.getY(), 0.0f));

            tmpPosition.set(tmpVec.x, tmpVec.y);

            tmpPosition.sub(getPosition());

            axe.attack(tmpPosition);
        }

        // Left
        if (Gdx.input.getX() < Gdx.graphics.getWidth() / 2.0f && facingRight) {

            facingRight = false;
        }
        // Right
        else if (Gdx.graphics.getWidth() / 2.0f < Gdx.input.getX() && !facingRight) {

            facingRight = true;
        }

        if (tmpDirection.x != 0.0f || tmpDirection.y != 0.0f) {

            if (currentAnimation != walkingAnimation && !attacking) {

                currentAnimation = walkingAnimation;

                stateTime = 0.0f;
            }

            // If facing right and going backwards
            if (facingRight && tmpDirection.x < 0.0f) {

                goingBackwards = true;
            }
            // if facing left and going backwards
            else if (!facingRight && 0.0f < tmpDirection.x) {

                goingBackwards = true;
            }
            else {

                goingBackwards = false;
            }

            tmpDirection.nor();

            map.resolveCollision(getPosition(), tmpDirection, speed);

            updateCameraPosition();
        }
        else {

            if (currentAnimation != stationaryAnimation && !attacking) {

                currentAnimation = stationaryAnimation;

                goingBackwards = false;

                stateTime = 0.0f;
            }
        }


        if (!goingBackwards) {

            stateTime += Gdx.graphics.getDeltaTime();
        }
        else {

            // Increment the state time by the whole animation if less than zero and decrement by
            // delta time to run the animation backwards.
            if (stateTime - Gdx.graphics.getDeltaTime() < 0.0f) {

                stateTime += walkingAnimation.getAnimationDuration();
            }

            stateTime -= Gdx.graphics.getDeltaTime();
        }

        int animationIndex = currentAnimation.getKeyFrameIndex(stateTime % currentAnimation.getAnimationDuration());

        if (currentAnimation == stationaryAnimation) {

            if (animationIndex == 1 || animationIndex == 2) {

                raiseAxe = true;
            }
            else {

                raiseAxe = false;
            }
        }
        else if (currentAnimation == walkingAnimation) {

            if (animationIndex == 1 || animationIndex == 2 || animationIndex == 4) {

                raiseAxe = true;
            }
            else {

                raiseAxe = false;
            }
        }

        currentTextureRegion = currentAnimation.getKeyFrame(stateTime, true);

        axe.flip = facingRight;

        tmpVec.set(Gdx.input.getX(), Gdx.input.getY(), 0.0f);

        tmpVec.set(camera.unproject(tmpVec));

        axe.setRotation((int) (Math.atan((tmpVec.y - getPosition().y) / (tmpVec.x - getPosition().x)) * 180 / Math.PI));

        super.tick();
    }

    Vector2 tmpPosition = new Vector2();

    @Override
    public void draw(SpriteBatch batch, Vector2 position, Vector2 size) {

        if (flip()) {

            getTextureRegion().flip(true, false);
        }

        // Shadow under the player
        batch.draw(Game.spriteSheet, position.x, position.y - (size.y / 16), size.x, size.y, 0, 0, 16, 16, false, false);

        batch.draw(getTextureRegion(), position.x, position.y, size.x, size.y);

        if (flip()) {

            getTextureRegion().flip(true, false);
        }

        tmpPosition.set(position);

        if (raiseAxe) {

            tmpPosition.add(0.0f, (size.y / 16.0f));
        }

        axe.draw(batch, tmpPosition, size);
    }

    @Override
    public boolean flip() {

        return !facingRight;
    }

    public void updateCameraPosition() {

        camera.position.set(Math.round(getPosition().x), Math.round(getPosition().y), 0.0f);
        camera.update();

        getPosition().set(camera.position.x, camera.position.y);
    }

    @Override
    public TextureRegion getTextureRegion() {

        return currentTextureRegion;
    }

    @Override
    protected TextureRegion getShadowTextureRegion() {

        return shadow;
    }

    @Override
    public void dispose() {

        stationaryTexture.dispose();
        walkingTexture.dispose();
    }
}

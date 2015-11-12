package io.github.simengangstad.defendthecaves;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;

/**
 * @author simengangstad
 * @since 10/11/15
 */
public class Player {

    public Camera camera;

    public int speed = 100;

    private Vector2 tmpDirection = new Vector2();

    public Player(Camera camera) {

        this.camera = camera;
    }

    public void tick(int[][] collidables, int tileSize, int playerSize) {

        float x = camera.position.x + playerSize / 2.0f;
        float y = camera.position.y + playerSize / 2.0f;

        float movement = speed * Gdx.graphics.getDeltaTime();

        tmpDirection.setZero();

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {

            tmpDirection.add(0.0f, movement);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S)) {

            tmpDirection.add(0.0f, -movement);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {

            tmpDirection.add(-movement, 0.0f);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {

            tmpDirection.add(movement, 0.0f);
        }

        if (collidables[(int) (x + tmpDirection.x + playerSize / 2.0f) / tileSize][(int) (y + tmpDirection.y) / tileSize] >= 10 ||
                collidables[(int) (x + tmpDirection.x - playerSize / 2.0f) / tileSize][(int) (y + tmpDirection.y) / tileSize] >= 10) {

            tmpDirection.x = 0;

        }

        if (collidables[(int) (x + tmpDirection.x) / tileSize][(int) (y + tmpDirection.y + playerSize / 2.0f) / tileSize] >= 10 ||
                collidables[(int) (x + tmpDirection.x) / tileSize][(int) (y + tmpDirection.y - playerSize / 2.0f) / tileSize] >= 10) {

            tmpDirection.y = 0;
        }

        camera.translate(tmpDirection.x, tmpDirection.y, 0.0f);
        camera.update();
    }
}

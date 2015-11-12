package io.github.simengangstad.defendthecaves;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Game extends ApplicationAdapter {

    SpriteBatch batch;
    Player player;

    Texture spriteSheet;
    MapGenerator generator;
    int[][] map;
    int mapSize = 60, playerSize = 10;

    @Override
	public void create () {

        // TODO: wider corridors

        player = new Player(new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        batch = new SpriteBatch();

        spriteSheet = new Texture("assets/spritesheet.png");

        generator = new MapGenerator(50, 50);
        generator.requestedAmountOfRooms = 15;
        generator.lowerBoundry = 7;
        generator.upperBoundry = 13;

        map = generator.generate();

        Vector2 spawnPosition = generator.spawnPoints.get(MathUtils.random(generator.spawnPoints.size() - 1));

        player.camera.position.set(spawnPosition.x * mapSize, spawnPosition.y * mapSize, 0.0f);
    }

    @Override
    public void resize(int width, int height) {

        player.camera.viewportWidth = width;
        player.camera.viewportHeight = height;
        player.camera.update();
    }

    @Override
	public void render () {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Gdx.graphics.setTitle("FPS: " + Gdx.graphics.getFramesPerSecond());

        player.tick(map, mapSize, playerSize);

        batch.setProjectionMatrix(player.camera.combined);

        batch.begin();

        for (int y = map.length - 1; y >= 0; y--) {

            for (int x = 0; x < map[0].length; x++) {

                switch (map[x][y]) {

                    case MapGenerator.Wall:

                        batch.draw(spriteSheet, x * mapSize, y * mapSize, mapSize, mapSize, 0, 0, 2, 2, false, false);

                        break;

                    case MapGenerator.Floor:

                        batch.draw(spriteSheet, x * mapSize, y * mapSize, mapSize, mapSize, 2, 0, 2, 2, false, false);

                        break;

                    case MapGenerator.Stone:

                        batch.draw(spriteSheet, x * mapSize, y * mapSize, mapSize, mapSize, 6, 0, 2, 2, false, false);

                        break;

                    case MapGenerator.CorridorWall:

                        batch.draw(spriteSheet, x * mapSize, y * mapSize, mapSize, mapSize, 4, 0, 2, 2, false, false);

                        break;
                }
            }
        }

        batch.draw(spriteSheet, player.camera.position.x, player.camera.position.y, playerSize, playerSize, 6, 0, 2, 2, false, false);

        batch.end();
    }

    @Override
    public void dispose() {

        batch.dispose();
    }
}

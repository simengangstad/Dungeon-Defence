package io.github.simengangstad.dungeondefence.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.simengangstad.dungeondefence.Game;
import io.github.simengangstad.dungeondefence.GameObject;

/**
 * An item which can be drawn and collide with the map.
 *
 * @author simengangstad
 * @since 09/02/16
 */
public abstract class Collidable extends GameObject {

    /**
     * Checks collision of every tile within the game object (every tile that isn't transparent) against
     * the surroundings. When this is not enabled collision is checked against the bounds {@link GameObject#size}
     * of the collidable.
     */
    public boolean tiledCollisionTests = false;

    /**
     * The reference to where the collidable has collidable tiles if {@link Collidable#tiledCollisionTests} is enabled for
     * each texture region of a game object.
     */
    protected boolean[][][] bakedTiledCollisionMaps;

    /**
     * The reference to the current map.
     */
    protected int bakedTiledCollisionMapIndex = 0;

    /**
     * The map the entity is located in.
     */
    public Map map;

    /**
     * The movement of the entity during the frames.
     */
    protected Vector2 delta = new Vector2();

    /**
     * Pixels/second.
     */
    public int speed = 300;

    /**
     * The force applied on the collidable.
     */
    protected final Vector2 forceApplied = new Vector2();

    /**
     * The forces acting on the collidable regardless of force applied.
     * The body is in equilibrium with 0 velocity at default state.
     */
    private static final float Drag = 5.0f, Weight = -6.0f;

    /**
     * If weight is acting on the collidable.
     */
    private boolean weightActing = false;

    /**
     * The duration of the forces acting on the collidable.
     */
    private float duration = 0.0f;

    /**
     * If the force along the axes are positive or negative.
     */
    private boolean horizontalPositive = false, verticalPositive = false;

    /**
     * Applies a vector force to the collidable which gets decreased by a drag force.
     */
    public void applyForce(Vector2 force, boolean weightActing, float duration) {

        forceApplied.set(force);

        horizontalPositive = 0 < forceApplied.x;
        verticalPositive = 0 < forceApplied.y;

        this.weightActing = weightActing;

        this.duration = duration;
    }

    /**
     * Computes tiled collison maps from the regions and the given width and height of each region.
     */
    public void computeTiledCollisionMaps(TextureRegion[] regions, int width, int height) {

        bakedTiledCollisionMaps = new boolean[regions.length][width][height];

        for (int i = 0; i < bakedTiledCollisionMaps.length; i++) {

            regions[i].getTexture().getTextureData().prepare();

            Pixmap pixmap = regions[i].getTexture().getTextureData().consumePixmap();

            for (int y = regions[i].getRegionY(); y < regions[i].getRegionY() + height; y++) {

                for (int x = regions[i].getRegionX(); x < regions[i].getRegionX() + width; x++) {

                    bakedTiledCollisionMaps[i][x - regions[i].getRegionX()][y - regions[i].getRegionY()] = pixmap.getPixel(x, y) != 0x00000000;
                }
            }
        }
    }

    @Override
        public void tick() {

        if (map == null) {

            this.map =  ((Scene) host).map;

            throw new RuntimeException("Need an assigned map in order to resolve collisions.");
        }

        if (forceApplied.x != 0.0f) {

            float dragPerFrame = Gdx.graphics.getDeltaTime() * Drag;

            forceApplied.x += (horizontalPositive ? -dragPerFrame : dragPerFrame);

            if (horizontalPositive) {

                if (forceApplied.x < 0.0f) {

                    forceApplied.x = 0.0f;
                }
            }
            else {

                if (0.0f < forceApplied.x) {

                    forceApplied.x = 0.0f;
                }
            }
        }

        if (weightActing) {

            if (0.0f < duration) {

                duration -= Gdx.graphics.getDeltaTime();

                float weightPerFrame = Gdx.graphics.getDeltaTime() * Weight;

                forceApplied.y += weightPerFrame;
            }
            else {

                forceApplied.y = 0.0f;
            }
        }
        else {

            if (forceApplied.y != 0.0f) {

                float dragPerFrame = Gdx.graphics.getDeltaTime() * Drag;

                if (verticalPositive) {

                    forceApplied.y -= dragPerFrame;

                    if (forceApplied.y < 0.0f) {

                        forceApplied.y = 0.0f;
                    }
                }
                else {

                    forceApplied.y += dragPerFrame;

                    if (0.0f < forceApplied.y) {

                        forceApplied.y = 0.0f;
                    }
                }
            }
        }

        delta.add(forceApplied.x, forceApplied.y);
    }

    public void checkTiledCollsionOnly() {

        if (delta.x != 0.0f || delta.y != 0.0f) {

            if (tiledCollisionTests) {

                // Only collision test, not response

                Vector2 tileSize = Game.vector2Pool.obtain();
                Vector2 tilePostion = Game.vector2Pool.obtain();

                tileSize.set(size.x / Game.SizeOfTileInPixelsInSpritesheet, size.y / Game.SizeOfTileInPixelsInSpritesheet);

                for (int x = 0; x < bakedTiledCollisionMaps[bakedTiledCollisionMapIndex].length; x++) {

                    for (int y = 0; y < bakedTiledCollisionMaps[bakedTiledCollisionMapIndex][0].length; y++) {

                        if (bakedTiledCollisionMaps[bakedTiledCollisionMapIndex][x][y]) {

                            tilePostion.set(position.x - size.x / 2.0f + (size.x / Game.SizeOfTileInPixelsInSpritesheet) * x + tileSize.x / 2.0f, position.y - size.y / 2.0f + (size.y / Game.SizeOfTileInPixelsInSpritesheet) * y + tileSize.y / 2.0f);

                            if (map.collides(tilePostion, tileSize, delta)) {

                                collides();

                                break;
                            }
                        }
                    }
                }

                Game.vector2Pool.free(tileSize);
                Game.vector2Pool.free(tilePostion);
            }
        }
    }

    public void checkCollsion() {

        if (delta.x != 0.0f || delta.y != 0.0f) {

            if (tiledCollisionTests) {

                // Only collision test, not response

                Vector2 tileSize = Game.vector2Pool.obtain();
                Vector2 tilePostion = Game.vector2Pool.obtain();

                tileSize.set(size.x / Game.SizeOfTileInPixelsInSpritesheet, size.y / Game.SizeOfTileInPixelsInSpritesheet);

                for (int x = 0; x < bakedTiledCollisionMaps[bakedTiledCollisionMapIndex].length; x++) {

                    for (int y = 0; y < bakedTiledCollisionMaps[bakedTiledCollisionMapIndex][0].length; y++) {

                        if (bakedTiledCollisionMaps[bakedTiledCollisionMapIndex][x][y]) {

                            tilePostion.set(position.x - size.x / 2.0f + (size.x / Game.SizeOfTileInPixelsInSpritesheet) * x + tileSize.x / 2.0f, position.y - size.y / 2.0f + (size.y / Game.SizeOfTileInPixelsInSpritesheet) * y + tileSize.y / 2.0f);

                            if (map.collides(tilePostion, tileSize, delta)) {

                                collides();

                                break;
                            }
                        }
                    }
                }

                Game.vector2Pool.free(tileSize);
                Game.vector2Pool.free(tilePostion);
            }
            else {

                if (map.resolveCollision(position, delta, speed)) {

                    collides();
                }
            }
        }
    }

    /**
     * Gets called when the collidable collides with a solid tile within the map.
     */
    protected abstract void collides();

    /**
     * Checks intersection between this collidable and another one.
     */
    public boolean intersects(Collidable other, boolean tiledCollisionTests) {

        if (this.tiledCollisionTests && tiledCollisionTests) {

            if (bakedTiledCollisionMaps == null) {

                throw new RuntimeException("Baked tiled collision map not computed yet!");
            }

            for (int x = 0; x < bakedTiledCollisionMaps[bakedTiledCollisionMapIndex].length; x++) {

                for (int y = 0; y < bakedTiledCollisionMaps[bakedTiledCollisionMapIndex][0].length; y++) {

                    if (bakedTiledCollisionMaps[bakedTiledCollisionMapIndex][x][y]) {

                        float posX = position.x - size.x / 2.0f + (size.x / Game.SizeOfTileInPixelsInSpritesheet) * x;
                        float posY = position.y - size.y / 2.0f + (size.y / Game.SizeOfTileInPixelsInSpritesheet) * y;

                        if(Math.abs(posX - other.position.x) < (size.x / Game.SizeOfTileInPixelsInSpritesheet) / 2.0f + other.size.x / 2.0f) {

                            if(Math.abs(posY - other.position.y) < (size.x / Game.SizeOfTileInPixelsInSpritesheet) / 2.0f + other.size.y / 2.0f) {

                                return true;
                            }
                        }
                    }
                }
            }
        }
        else {

            if(Math.abs(position.x - other.position.x) < size.x / 2.0f + other.size.x / 2.0f) {

                if(Math.abs(position.y - other.position.y) < size.y / 2.0f + other.size.y / 2.0f) {

                    return true;
                }
            }
        }

        return false;
    }
}

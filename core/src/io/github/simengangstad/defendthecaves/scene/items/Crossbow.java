package io.github.simengangstad.defendthecaves.scene.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import io.github.simengangstad.defendthecaves.Game;
import io.github.simengangstad.defendthecaves.scene.Scene;

import java.util.ArrayList;
import java.util.List;

/**
 * @author simengangstad
 * @since 08/04/16
 */
public class Crossbow extends RotatableWeapon {

    private static TextureRegion[] textureRegions = new TextureRegion[6], attackTextureRegions = new TextureRegion[6 * 3];

    static {

        int index = 0;

        for (int x = 320; x < 320 + 32 * textureRegions.length; x += 32) {

            textureRegions[index] =  new TextureRegion(Game.SpriteSheet, x, 304, 32, 32);

            index++;
        }

        index = 0;

        for (int y = 0;  y < attackTextureRegions.length / 6; y++) {

            for (int x = 320; x < 320 + 32 * attackTextureRegions.length / 3; x += 32) {

                attackTextureRegions[index + y * attackTextureRegions.length / 3] = new TextureRegion(Game.SpriteSheet, x, 304 + 32 * y, 32, 32);

                index++;
            }

            index = 0;
        }
    }

    boolean recoil = false;

    private Vector2 force = new Vector2();

    private Vector3 tmp = new Vector3();

    private List<Object> tmpList = new ArrayList<>();

    public static final Sound Fire = Gdx.audio.newSound(Gdx.files.internal("assets/sfx/crossbow.wav"));

    public Crossbow() {

        super(40, 0.15f, null, -45, 45, textureRegions, attackTextureRegions);

        super.stackable = true;

        size.set(Game.EntitySize, Game.EntitySize);

        information = "Crossbow\nAttack damage: " + attackDamage;

        callback = () -> recoil = false;
    }

    @Override
    public TextureRegion getSlotTextureRegion() {

        return textureRegions[0];
    }

    @Override
    public void tick() {

        super.tick();

        if (isAttacking()) {

            if (!recoil) {

                tmp.set(Gdx.input.getX(), Gdx.input.getY(), 0.0f);
                ((Scene) host).inWorldSpace(tmp);

                force.set(-(tmp.x - parent.position.x), -(tmp.y - parent.position.y)).nor().scl(3.0f);

                parent.applyForce(force, false, 1.0f);

                recoil = true;

                force.scl(-1).nor().scl(23.0f);

                tmp.set(position, 0.0f);

                switch (weaponIndexBeforeAttacking) {

                    // Up to down

                    case 0:

                        tmp.y -= 5.0f;

                        break;

                    case 1:

                        tmp.y += Game.EntitySize / 2.0f;

                        break;

                    case 2:

                        tmp.y += Game.EntitySize / 2.0f + 5.0f;

                        break;

                    case 3:

                        tmp.y += 20.0f;

                        break;

                    case 4:

                        tmp.y += 22.5f;

                        break;

                    case 5:

                        tmp.y += 35.0f;

                        break;
                }

                parent.inventory.getAllItemsByType(Arrow.class, tmpList);

                if (tmpList.size() > 0) {

                    Arrow arrow = (Arrow) tmpList.get(tmpList.size() - 1);

                    arrow.map = parent.map;
                    arrow.position.set(tmp.x, tmp.y);
                    arrow.velocity.set(force);
                    arrow.rotationIndex = weaponIndexBeforeAttacking;
                    arrow.gameObjectList = host.getGameObjects();
                    arrow.avoidable = parent;
                    arrow.flip = force.x > 0;
                    arrow.projectingAnimation = Arrow.projectingAnimations[weaponIndexBeforeAttacking];

                    parent.inventory.removeItem(arrow);

                    host.addGameObject(arrow);
                }
            }

            int index = (int) Math.abs(getTimeLeftOfAttack() / (attackDuration / (attackTextureRegions.length / 6)) - (attackTextureRegions.length / 6));

            currentWeaponTextureRegion = attackTextureRegions[weaponIndexBeforeAttacking + index * 6];
        }
    }

    @Override
    protected float xOffset() {

        return 0;
    }

    @Override
    protected float yOffset() {

        return -(size.y / 16) * 2;
    }
}

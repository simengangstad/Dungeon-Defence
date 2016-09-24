package io.github.simengangstad.defendthecaves.scene;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

/**
 * @author simengangstad
 * @since 27/01/16
 */
public class TextureUtil {

    public static Array<TextureRegion> frames = new Array<TextureRegion>();

    public static Animation getAnimation(Texture texture, int x, int y, int width, int widthOfFrames, int heightOfFrame, float frameDuration, Animation.PlayMode playMode) {

        frames.clear();

        for (int xs = x; xs < x + width; xs += widthOfFrames) {

            frames.add(new TextureRegion(texture, xs, y, widthOfFrames, heightOfFrame));
        }

        return new Animation(frameDuration, frames, playMode);
    }

    public static Animation getAnimation(Texture texture, int widthOfFrames, float frameDuration, Animation.PlayMode playMode) {

        return getAnimation(texture, 0, 0, texture.getWidth(), widthOfFrames, texture.getHeight(), frameDuration, playMode);
    }
}

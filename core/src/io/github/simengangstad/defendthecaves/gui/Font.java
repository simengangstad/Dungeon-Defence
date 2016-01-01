package io.github.simengangstad.defendthecaves.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;

/**
 * @author simengangstad
 * @since 20.12.14
 */
public class Font
{
    private static Texture texture;

    private static boolean initialized = false;

    private static HashMap<Character, TextureRegion> glyphs;

    private static void initialize()
    {
        Font.glyphs = new HashMap<>();

        Font.texture = new Texture(Gdx.files.internal("assets/glyphs.png"));

        TextureRegion[][] textureRegions = TextureRegion.split(texture, Font.texture.getWidth() / 8, Font.texture.getHeight() / 8);

        Font.glyphs.put('a', textureRegions[0][0]);
        Font.glyphs.put('b', textureRegions[0][1]);
        Font.glyphs.put('c', textureRegions[0][2]);
        Font.glyphs.put('d', textureRegions[0][3]);
        Font.glyphs.put('e', textureRegions[0][4]);
        Font.glyphs.put('f', textureRegions[0][5]);
        Font.glyphs.put('g', textureRegions[0][6]);
        Font.glyphs.put('h', textureRegions[0][7]);
        Font.glyphs.put('i', textureRegions[1][0]);
        Font.glyphs.put('j', textureRegions[1][1]);
        Font.glyphs.put('k', textureRegions[1][2]);
        Font.glyphs.put('l', textureRegions[1][3]);
        Font.glyphs.put('m', textureRegions[1][4]);
        Font.glyphs.put('n', textureRegions[1][5]);
        Font.glyphs.put('o', textureRegions[1][6]);
        Font.glyphs.put('p', textureRegions[1][7]);
        Font.glyphs.put('q', textureRegions[2][0]);
        Font.glyphs.put('r', textureRegions[2][1]);
        Font.glyphs.put('s', textureRegions[2][2]);
        Font.glyphs.put('t', textureRegions[2][3]);
        Font.glyphs.put('u', textureRegions[2][4]);
        Font.glyphs.put('v', textureRegions[2][5]);
        Font.glyphs.put('w', textureRegions[2][6]);
        Font.glyphs.put('x', textureRegions[2][7]);
        Font.glyphs.put('y', textureRegions[3][0]);
        Font.glyphs.put('z', textureRegions[3][1]);
        Font.glyphs.put('.', textureRegions[3][2]);
        Font.glyphs.put('>', textureRegions[3][3]);
        Font.glyphs.put('<', textureRegions[6][1]);
        Font.glyphs.put(' ', textureRegions[3][4]);
        Font.glyphs.put('+', textureRegions[3][5]);
        Font.glyphs.put('-', textureRegions[3][6]);
        Font.glyphs.put(':', textureRegions[3][7]);
        Font.glyphs.put('%', textureRegions[6][0]);
        Font.glyphs.put('!', textureRegions[6][2]);

        Font.glyphs.put('1', textureRegions[4][0]);
        Font.glyphs.put('2', textureRegions[4][1]);
        Font.glyphs.put('3', textureRegions[4][2]);
        Font.glyphs.put('4', textureRegions[4][3]);
        Font.glyphs.put('5', textureRegions[4][4]);
        Font.glyphs.put('6', textureRegions[4][5]);
        Font.glyphs.put('7', textureRegions[4][6]);
        Font.glyphs.put('8', textureRegions[4][7]);
        Font.glyphs.put('9', textureRegions[5][0]);
        Font.glyphs.put('0', textureRegions[5][1]);
    }

    public static void draw(SpriteBatch spriteBatch, String message, int xSpace, int ySpace, float x, float y, int scale)
    {
        if (!Font.initialized)
        {
            Font.initialized = true;

            Font.initialize();
        }

        int xs = 0;
        int ys = 0;

        for (char character : message.toLowerCase().toCharArray())
        {
            if (character == '\n')
            {
                ys -= ySpace;
                xs = 0;

                continue;
            }

            spriteBatch.draw(Font.glyphs.get(character), x + scale * xs, y + scale * ys, 8 * scale, 8 * scale);

            xs += xSpace;
        }
    }
}

package io.github.simengangstad.defendthecaves.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

/**
 * @author simengangstad
 * @since 25/02/16
 */
public class LightShader {

    /**
     * Reference to the shader program.
     */
    public ShaderProgram handle;

    /**
     * The amount of lights the shader is set to.
     */
    public int amountOfLights = 1;


    /**
     * The width and size of tiles within the canvas. Light will be clamped to these tiles,
     * so in order to have pixel perfect light this has to be equal to 1.
     */
    private float lightTileSizeInPixels;

    private final String LightTileSizeInPixelsUniformName = "u_lightTileSize";


    private final Vector3 ambientColour = new Vector3();

    private final String AmbientColourUniformName = "u_ambientSceneColour";


    private float time;

    private final String TimeUniformName = "u_time";


    private final String TileSizeUniformName = "u_tileSizeInWorldSpace";

    private final String MapWidthUniformName = "u_widthOfMap";

    private final float[] byteMap;

    private final int width;


    public LightShader(int LightTileSizeInPixels, int widthOfMap, int heightOfMap) {

        this.lightTileSizeInPixels = LightTileSizeInPixels;

        width = widthOfMap;

        byteMap = new float[widthOfMap * heightOfMap];
    }

    public void updateLights(int amountOfLights) {

        if (handle != null) {

            handle.dispose();
        }

        this.amountOfLights = amountOfLights;

        handle = new ShaderProgram(getVertexShader(), getFragmentShader(amountOfLights == 0 ? 1 : amountOfLights));

        if (!handle.isCompiled()) {

            System.err.println("Couldn't compile shader: " + handle.getLog());
        }
    }

    public void updateMap(Map map) {

        for (int y = 0; y < map.getHeight(); y++) {

            for (int x = 0; x < map.getWidth(); x++) {

                byteMap[x + y * map.getWidth()] = (map.isSolid(x, y) ? 1 : 0);
            }
        }

        handle.setUniform1fv("u_map[0]", byteMap, 0, byteMap.length);
    }

    public void setAmbientColour(float r, float g, float b) {

        ambientColour.set(r, g, b);
    }

    public void uploadUniforms(ArrayList<Light> lights) {

        handle.setUniformf(LightTileSizeInPixelsUniformName, lightTileSizeInPixels);

        handle.setUniformf(AmbientColourUniformName, ambientColour);
        handle.setUniformf(TileSizeUniformName, Map.TileSizeInPixelsInWorldSpace);
        handle.setUniformi(MapWidthUniformName, width);

        time += Gdx.graphics.getDeltaTime() * 5;

        handle.setUniformf(TimeUniformName, time);

        for (int i = 0; i < lights.size(); i++) {

            Light light = lights.get(i);

            handle.setUniformf("u_lights[" + i + "].position", light.position);
            handle.setUniformf("u_lights[" + i + "].colour", light.colour);
            handle.setUniformf("u_lights[" + i + "].range", light.range);
            handle.setUniformi("u_lights[" + i + "].enabled", light.enabled ? 1 : 0);
        }
    }

    private String getVertexShader() {

        return  "attribute vec4 a_position;\n" +
                "attribute vec4 a_color;\n" +
                "attribute vec2 a_texCoord0;\n" +

                "uniform mat4 u_projTrans;\n" +

                "varying mat4 v_projTrans;\n" +
                "varying vec4 v_position;\n" +
                "varying vec4 v_colour;\n" +
                "varying vec2 v_texCoords;\n" +

                "void main() {\n" +

                    "v_projTrans = u_projTrans;\n" +
                    "v_position = a_position;\n" +
                    "v_colour = a_color;\n" +
                    "v_colour.a = v_colour.a * (255.0/254.0);\n" +
                    "v_texCoords = vec2(a_texCoord0.x, a_texCoord0.y);\n" +
                    "gl_Position = u_projTrans * a_position;\n" +
                "}";
    }

    private String getFragmentShader(int amountOfLights) {

        return  "#ifdef GL_ES\n" +
                "#define LOWP lowp\n" +

                "precision mediump float;\n" +

                "#else\n" +
                "#define LOWP\n" +
                "#endif\n" +

                "varying mat4 v_projTrans;\n" +
                "varying vec4 v_position;\n" +
                "varying LOWP vec4 v_colour;\n" +
                "varying vec2 v_texCoords;\n" +

                "const int AmountOfLights = " + amountOfLights + ";\n" +

                "struct Light {\n" +

                    "vec2 position;\n" +
                    "vec3 colour;\n" +
                    "float range;\n" +
                    "int enabled;\n" +
                "};\n" +

                "uniform float u_time;\n" +
                "uniform vec3 u_ambientSceneColour;\n" +
                "uniform float u_lightTileSize;\n" +
                "uniform Light u_lights[AmountOfLights];\n" +
                "uniform sampler2D u_texture;\n" +
                "uniform bool u_flash;\n" +

                "uniform float u_tileSizeInWorldSpace;\n" +
                "uniform int u_widthOfMap;\n" +
                "uniform float u_map["+byteMap.length+"];\n" +

                "const float timeScalar = 1.5;\n" +

                "float rand(float a) {\n" +

                    "return fract(sin(dot(vec2(a, a), vec2(12.9898,78.233))) * 43758.5453);\n" +
                "}\n" +

                "void main() {\n" +

                    "vec4 texColor = texture2D(u_texture, v_texCoords);\n" +

                    "vec4 colour;\n" +

                    "if (!u_flash) {\n" +

                        "colour = v_colour * texColor;\n" +
                    "}\n" +
                    "else {\n" +

                        "colour = vec4(1.0, 1.0, 1.0, texColor.a);\n" +
                    "}\n" +

                    "vec2 tilePos = vec2(int(v_position.x / u_tileSizeInWorldSpace), int(v_position.y / u_tileSizeInWorldSpace));\n" +

                    "bool illuminated = false;\n" +
                    "vec3 finalLightColour = vec3(-1.0, -1.0, -1.0);\n" +

                    "float intensity = 0.0;\n" +
                    "int intensity1 = u_widthOfMap;\n" +

                    "if  (u_map[int(tilePos.x) + int(tilePos.y) * u_widthOfMap] == 0.0 || \n" +
                        "(u_map[int(tilePos.x) + int(tilePos.y) * u_widthOfMap] == 1.0 && u_map[int(tilePos.x) + int(tilePos.y - 1.0) * u_widthOfMap] == 0.0)/* ||\n" +
                        "(u_map[int(tilePos.x) + int(tilePos.y) * u_widthOfMap] == 1.0 && u_map[int(tilePos.x + 1.0) + int(tilePos.y) * u_widthOfMap] == 0.0) ||\n" +
                        "(u_map[int(tilePos.x) + int(tilePos.y) * u_widthOfMap] == 1.0 && u_map[int(tilePos.x - 1.0) + int(tilePos.y) * u_widthOfMap] == 0.0)*/) {\n" +

                        "vec3 preColour = vec3(colour);\n" +

                        "for (int i = 0; i < AmountOfLights; i++) {\n" +

                            "if (u_lights[i].enabled == 0) {\n" +

                                "continue;\n" +

                            "}\n" +

                            "float lightRange = u_lights[i].range;\n" +
                            "float range = length(vec2(int(v_position.x / u_lightTileSize), int(v_position.y / u_lightTileSize)) - vec2(int(u_lights[i].position.x / u_lightTileSize), int(u_lights[i].position.y / u_lightTileSize)));\n" +

                            "if (range < lightRange) {\n" +

                                // Temp

                                "if (finalLightColour.x == -1.0) {\n" +

                                    "finalLightColour = u_lights[i].colour;\n" +
                                "}\n" +
                                "else {\n" +

                                    //"finalLightColour = (finalLightColour + u_lights[i].colour);\n" +
                                    "finalLightColour = u_lights[i].colour;\n" +
                                "}\n" +

                                "illuminated = true;\n" +

                                "if ((range / lightRange) < 2.25/3.0) {\n" +

                                    "intensity += 1.0;\n" +
                                "}\n" +

                                "else if ((range / lightRange) < 2.625/3.0) {\n" +

                                    "intensity += 0.5;\n" +
                                "}\n" +

                                "else if ((range / lightRange) <= 3.0/3.0) {\n" +

                                    "intensity += 0.25;\n" +
                                "}\n" +
                            "}\n" +
                        "}\n" +
                    "}\n" +

                    "if (illuminated) {\n" +

                        "colour.xyz *= finalLightColour * (min(intensity, 1.0) * (rand(float(int(u_time / timeScalar))) + 1.0));\n" +
                    "}\n" +
                    "else {\n" +

                        "colour.xyz *= u_ambientSceneColour;\n" +
                    "}\n" +

                "gl_FragColor = colour;\n" +

                "}\n";
    }
}

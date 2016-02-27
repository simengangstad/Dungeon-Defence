package io.github.simengangstad.defendthecaves.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;

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
    private final float[] lightTileSizeInPixels = new float[1];

    private final String LightTileSizeInPixelsUniformName = "u_lightTileSize";


    private final float[] ambientColour = new float[3];

    private final String AmbientColourUniformName = "u_ambientSceneColour";


    private final float[] time = new float[1];

    private final String TimeUniformName = "u_time";


    public LightShader(int LightTileSizeInPixels) {

        this.lightTileSizeInPixels[0] = LightTileSizeInPixels;
    }

    public void create() {

        update(amountOfLights);
    }

    public void update(int amountOfLights) {

        if (handle != null) {

            handle.dispose();
        }

        this.amountOfLights = amountOfLights;

        handle = new ShaderProgram(getVertexShader(), getFragmentShader(amountOfLights));

        if (!handle.isCompiled()) {

            System.err.println("Couldn't compile shader: " + handle.getLog());
        }
    }

    public void setAmbientColour(float r, float g, float b) {

        ambientColour[0] = r;
        ambientColour[1] = g;
        ambientColour[2] = b;
    }

    public void uploadUniforms(ArrayList<Light> lights, Matrix4 projectionMatrix) {

        handle.setUniform1fv(LightTileSizeInPixelsUniformName, lightTileSizeInPixels, 0, 1);

        handle.setUniform3fv(AmbientColourUniformName, ambientColour, 0, 3);

        time[0] += Gdx.graphics.getDeltaTime() * 5;

        handle.setUniform1fv(TimeUniformName, time, 0, 1);

        for (int i = 0; i < lights.size(); i++) {

            Light light = lights.get(i);

            handle.setUniform2fv("u_lights[" + i + "].position", light.getPositionArray(), 0, 2);
            handle.setUniform3fv("u_lights[" + i + "].colour", light.getColourArray(), 0, 3);
            handle.setUniform1fv("u_lights[" + i + "].range", light.getRangeArray(), 0, 1);
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
                "};\n" +

                "uniform float u_time;\n" +
                "uniform vec3 u_ambientSceneColour;\n" +
                "uniform float u_lightTileSize;\n" +
                "uniform Light u_lights[AmountOfLights];\n" +
                "uniform sampler2D u_texture;\n" +
                "uniform bool u_flash;\n" +

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

                    "bool illuminated = false;\n" +

                    "vec3 preColour = vec3(colour);\n" +
                    "vec3 finalLightColour = vec3(-1.0, -1.0, -1.0);\n" +

                    "float intensity = 0.0;\n" +

                    "for (int i = 0; i < AmountOfLights; i++) {\n" +

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


                            "bool fullIntensity = false;\n" +

                            "illuminated = true;\n" +

                            "if ((range / lightRange) < 2.25/3.0) {\n" +

                                "intensity += 1.0;\n" +

                                "fullIntensity = true;\n" +
                            "}\n" +

                            "else if ((range / lightRange) < 2.625/3.0) {\n" +

                                "intensity += 0.5;\n" +
                            "}\n" +

                            "else if ((range / lightRange) <= 3.0/3.0) {\n" +

                                "intensity += 0.25;\n" +
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

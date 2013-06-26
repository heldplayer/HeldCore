
package me.heldplayer.util.HeldCore.client.shader;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

/**
 * Class representing a shader
 * 
 * @author heldplayer
 * 
 */
public class Shader {

    /**
     * The program ID of the shader
     */
    public final int programId;

    public Shader(int programId) {
        this.programId = programId;
    }

    /**
     * Binds the shader
     */
    public void bindShader() {
        prevShader = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
        GL20.glUseProgram(this.programId);
    }

    /**
     * Integer used to store the current active shader in case of other active
     * shaders
     */
    public static int prevShader = 0;

    /**
     * Unbinds the current shader and returns to the previous shader
     */
    public static void unbindShader() {
        GL20.glUseProgram(prevShader);
        prevShader = 0;
    }

}


package me.heldplayer.util.HeldCore.client.shader;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class Shader {

    public int programId;

    public Shader(int programId) {
        this.programId = programId;
    }

    public void bindShader() {
        prevShader = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
        GL20.glUseProgram(this.programId);
    }

    public static int prevShader = 0;

    public static void unbindShader() {
        GL20.glUseProgram(prevShader);
        prevShader = 0;
    }

}

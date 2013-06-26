
package me.heldplayer.util.HeldCore.client.shader;

import java.io.BufferedReader;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class ShaderLoader {

    public static Shader createShader(String name, BufferedReader vertex, BufferedReader fragment) {
        int programId = GL20.glCreateProgram();
        int vertexId = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        int fragmentId = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);

        String vertexData = null;

        try {
            String line = null;

            StringBuilder builder = new StringBuilder();

            while ((line = vertex.readLine()) != null) {
                builder.append(line).append('\n');
            }

            vertex.close();

            vertexData = builder.toString();
        }
        catch (Exception e) {
            System.err.println("Failed finding vertex shader part for " + name);
        }

        String fragmentData = null;

        try {
            String line = null;

            StringBuilder builder = new StringBuilder();

            while ((line = fragment.readLine()) != null) {
                builder.append(line).append('\n');
            }

            fragment.close();

            fragmentData = builder.toString();
        }
        catch (Exception e) {
            System.err.println("Failed finding fragment shader part for " + name);
        }

        if (vertexData != null) {
            GL20.glShaderSource(vertexId, vertexData);
            GL20.glCompileShader(vertexId);

            if (GL20.glGetShader(vertexId, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
                System.err.println("Failed compiling vertex shader for " + name);

                return null;
            }
        }

        if (fragmentData != null) {
            GL20.glShaderSource(fragmentId, fragmentData);
            GL20.glCompileShader(fragmentId);

            if (GL20.glGetShader(fragmentId, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
                System.err.println("Failed compiling fragment shader for " + name);

                return null;
            }
        }

        if (vertexData == null && fragmentData == null) {
            System.err.println("Shader did not load for both vertex and fragment for " + name);

            return null;
        }

        if (vertexData != null) {
            GL20.glAttachShader(programId, vertexId);
        }
        if (fragmentData != null) {
            GL20.glAttachShader(programId, fragmentId);
        }
        GL20.glLinkProgram(programId);

        if (GL20.glGetProgram(programId, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            System.err.println("Failed linking shader for " + name);

            return null;
        }

        if (vertexData != null) {
            GL20.glDeleteShader(vertexId);
        }
        if (fragmentData != null) {
            GL20.glDeleteShader(fragmentId);
        }

        return new Shader(programId);
    }

}

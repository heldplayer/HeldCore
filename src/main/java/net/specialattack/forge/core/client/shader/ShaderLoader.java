package net.specialattack.forge.core.client.shader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.specialattack.forge.core.Objects;
import net.specialattack.forge.core.client.MC;
import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL11;

public class ShaderLoader {

    protected static ShaderProgram createShader(ResourceLocation location) {
        if (!GLUtil.SHADERS_SUPPORTED) {
            return null;
        }
        IReloadableResourceManager resourceManager = MC.getResourceManager();
        BufferedReader vertex;
        try {
            vertex = new BufferedReader(new InputStreamReader(resourceManager.getResource(new ResourceLocation(location.getResourceDomain(), location.getResourcePath() + ".vertex")).getInputStream()));
        } catch (Exception e) {
            return null;
        }
        BufferedReader fragment;
        try {
            fragment = new BufferedReader(new InputStreamReader(resourceManager.getResource(new ResourceLocation(location.getResourceDomain(), location.getResourcePath() + ".fragment")).getInputStream()));
        } catch (Exception e) {
            try {
                vertex.close();
            } catch (IOException e2) {
            }
            return null;
        }
        return createShader(location, vertex, fragment);
    }

    /**
     * Creates a shader from given parameters
     *
     * @param location
     *         Name of the shader, purely used for writing error messages
     * @param vertex
     *         The vertex shader part
     * @param fragment
     *         The fragment shader part
     *
     * @return A shader object representing the newly created shader
     */
    protected static ShaderProgram createShader(ResourceLocation location, BufferedReader vertex, BufferedReader fragment) {
        if (!GLUtil.SHADERS_SUPPORTED) {
            return null;
        }
        int programId = GLUtil.createShaderProgram();
        int vertexId = GLUtil.createVertexShader();
        int fragmentId = GLUtil.createFragmentShader();
        String vertexData = null, fragmentData = null;

        try {
            try {
                StringBuilder builder = new StringBuilder();

                String line;
                while ((line = vertex.readLine()) != null) {
                    builder.append(line).append('\n');
                }

                vertexData = builder.toString();
            } catch (IOException e) {
                throw new ShaderException("Failed loading vertex shader", e);
            }

            try {
                StringBuilder builder = new StringBuilder();

                String line;
                while ((line = fragment.readLine()) != null) {
                    builder.append(line).append('\n');
                }

                fragmentData = builder.toString();
            } catch (IOException e) {
                throw new ShaderException("Failed loading fragment shader", e);
            }

            GLUtil.setShaderSource(vertexId, vertexData);
            GLUtil.compileShader(vertexId);

            if (GLUtil.getShaderParameter(vertexId, GLUtil.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
                throw new ShaderException("Failed compiling vertex shader\n" + GLUtil.getShaderLog(vertexId, Short.MAX_VALUE));
            }

            GLUtil.setShaderSource(fragmentId, fragmentData);
            GLUtil.compileShader(fragmentId);

            if (GLUtil.getShaderParameter(fragmentId, GLUtil.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
                throw new ShaderException("Failed compiling fragment shader\n" + GLUtil.getShaderLog(vertexId, Short.MAX_VALUE));
            }

            GLUtil.attachShader(programId, vertexId);
            GLUtil.attachShader(programId, fragmentId);
            GLUtil.linkProgram(programId);

            if (GLUtil.getProgramParameter(programId, GLUtil.GL_LINK_STATUS) == GL11.GL_FALSE) {
                GLUtil.detachShader(programId, vertexId);
                GLUtil.detachShader(programId, fragmentId);
                throw new ShaderException("Failed linking shader program\n" + GLUtil.getShaderLog(vertexId, Short.MAX_VALUE));
            }
        } catch (ShaderException e) {
            Objects.log.log(Level.ERROR, "Failed loading shader '" + location.toString() + "'", e);
            Objects.log.log(Level.ERROR, "Vertex Shader:\n" + vertexData);
            Objects.log.log(Level.ERROR, "Fragment Shader:\n" + fragmentData);
            GLUtil.deleteProgram(programId);
            GLUtil.deleteShader(vertexId);
            GLUtil.deleteShader(fragmentId);
            return null;
        } finally {
            try {
                vertex.close();
            } catch (IOException e2) {
            }
            try {
                fragment.close();
            } catch (IOException e2) {
            }
        }

        return new ShaderProgram(programId, new Shader(vertexId, vertexData), new Shader(fragmentId, fragmentData));
    }

}

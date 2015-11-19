package net.specialattack.forge.core.client.shader;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class ShaderUniform {

    public final ShaderProgram program;
    public final String name;
    private final int id;

    public ShaderUniform(ShaderProgram program, String name) {
        this.program = program;
        this.name = name;
        this.id = GLUtil.getUniformLocation(program.id, name);
    }

    public void set1(FloatBuffer buffer) {
        GLUtil.Uniform.set1(this.id, buffer);
    }

    public void set1(float a) {
        GLUtil.Uniform.set1(this.id, a);
    }

    public void set1(IntBuffer buffer) {
        GLUtil.Uniform.set1(this.id, buffer);
    }

    public void set1(int a) {
        GLUtil.Uniform.set1(this.id, a);
    }

    public void set2(FloatBuffer buffer) {
        GLUtil.Uniform.set2(this.id, buffer);
    }

    public void set2(float a, float b) {
        GLUtil.Uniform.set2(this.id, a, b);
    }

    public void set2(IntBuffer buffer) {
        GLUtil.Uniform.set2(this.id, buffer);
    }

    public void set2(int a, int b) {
        GLUtil.Uniform.set2(this.id, a, b);
    }

    public void set3(FloatBuffer buffer) {
        GLUtil.Uniform.set3(this.id, buffer);
    }

    public void set3(float a, float b, float c) {
        GLUtil.Uniform.set3(this.id, a, b, c);
    }

    public void set3(IntBuffer buffer) {
        GLUtil.Uniform.set3(this.id, buffer);
    }

    public void set3(int a, int b, int c) {
        GLUtil.Uniform.set3(this.id, a, b, c);
    }

    public void set4(FloatBuffer buffer) {
        GLUtil.Uniform.set4(this.id, buffer);
    }

    public void set4(float a, float b, float c, float d) {
        GLUtil.Uniform.set4(this.id, a, b, c, d);
    }

    public void set4(IntBuffer buffer) {
        GLUtil.Uniform.set4(this.id, buffer);
    }

    public void set4(int a, int b, int c, int d) {
        GLUtil.Uniform.set4(this.id, a, b, c, d);
    }

    public void setMatrix2(boolean transpose, FloatBuffer buffer) {
        GLUtil.Uniform.setMatrix2(this.id, transpose, buffer);
    }

    public void setMatrix3(boolean transpose, FloatBuffer buffer) {
        GLUtil.Uniform.setMatrix3(this.id, transpose, buffer);
    }

    public void setMatrix4(boolean transpose, FloatBuffer buffer) {
        GLUtil.Uniform.setMatrix4(this.id, transpose, buffer);
    }
}

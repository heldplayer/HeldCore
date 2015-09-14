package net.specialattack.forge.core.client.shader;

public class ShaderAttribute {

    public final ShaderProgram program;
    public final String name;
    private final int id;

    public ShaderAttribute(ShaderProgram program, String name) {
        this.program = program;
        this.name = name;
        this.id = GLUtil.getAttributeLocation(program.id, name);
    }

    public void set1(float a) {
        GLUtil.Attribute.set1(this.id, a);
    }

    public void set1(double a) {
        GLUtil.Attribute.set1(this.id, a);
    }

    public void set2(float a, float b) {
        GLUtil.Attribute.set2(this.id, a, b);
    }

    public void set2(double a, double b) {
        GLUtil.Attribute.set2(this.id, a, b);
    }

    public void set3(float a, float b, float c) {
        GLUtil.Attribute.set3(this.id, a, b, c);
    }

    public void set3(double a, double b, double c) {
        GLUtil.Attribute.set3(this.id, a, b, c);
    }

    public void set4(float a, float b, float c, float d) {
        GLUtil.Attribute.set4(this.id, a, b, c, d);
    }

    public void set4(double a, double b, double c, double d) {
        GLUtil.Attribute.set4(this.id, a, b, c, d);
    }
}

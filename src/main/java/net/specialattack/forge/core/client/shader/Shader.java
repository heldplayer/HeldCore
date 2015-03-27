package net.specialattack.forge.core.client.shader;

public class Shader {

    public final int id;
    public final String source;

    public Shader(int id, String source) {
        this.id = id;
        this.source = source;
    }

    public void delete() {
        GLUtil.deleteShader(this.id);
    }

}

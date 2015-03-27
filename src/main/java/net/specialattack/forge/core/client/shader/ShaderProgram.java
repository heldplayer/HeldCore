package net.specialattack.forge.core.client.shader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShaderProgram {

    public final int id;
    public final Shader vertex;
    public final Shader fragment;
    private List<ShaderCallback> callbacks = new ArrayList<ShaderCallback>();
    protected Map<String, ShaderUniform> uniforms = new HashMap<String, ShaderUniform>();

    public ShaderProgram(int id, Shader vertex, Shader fragment) {
        this.vertex = vertex;
        this.fragment = fragment;
        this.id = id;
    }

    public void bind() {
        GLUtil.useProgram(this.id);
        for (ShaderCallback callback : this.callbacks) {
            callback.call(this);
        }
    }

    public void unbind() {
        GLUtil.useProgram(0);
    }

    public void deleteShader() {
        GLUtil.detachShader(this.id, this.vertex.id);
        GLUtil.detachShader(this.id, this.fragment.id);
        GLUtil.deleteProgram(this.id);
        this.vertex.delete();
        this.fragment.delete();
    }

    public List<ShaderCallback> getCallbacks() {
        return this.callbacks;
    }

    public void addCallback(ShaderCallback callback) {
        this.callbacks.add(callback);
    }

    public ShaderUniform getUniform(String name) {
        return this.uniforms.get(name);
    }

}

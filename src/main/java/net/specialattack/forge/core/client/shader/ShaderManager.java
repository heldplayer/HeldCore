package net.specialattack.forge.core.client.shader;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.specialattack.forge.core.client.ClientProxy;
import net.specialattack.forge.core.client.MC;
import net.specialattack.forge.core.client.resources.data.ShaderMetadataSection;
import org.apache.commons.io.IOUtils;

public class ShaderManager implements IResourceManagerReloadListener {

    private static Map<ResourceLocation, ShaderManager.ShaderBinding> data = Maps.newHashMap();

    @Override
    public void onResourceManagerReload(IResourceManager manager) {
        for (ShaderManager.ShaderBinding data : ShaderManager.data.values()) {
            List<ShaderCallback> callbacks = null;
            if (data.shader != null) {
                data.shader.deleteShader();
                callbacks = data.shader.getCallbacks();
                data.shader = null;
            }

            data.shader = ShaderLoader.createShader(data.resource);
            if (data.shader != null && callbacks != null) {
                for (ShaderCallback callback : callbacks) {
                    data.shader.addCallback(callback);
                }
            }
            ShaderManager.loadMetadata(data);
        }
    }

    public static ShaderManager.ShaderBinding getShader(ResourceLocation resourceLocation) {
        ShaderManager.ShaderBinding result = ShaderManager.data.get(resourceLocation);
        if (result == null) {
            result = new ShaderManager.ShaderBinding(resourceLocation);

            result.shader = ShaderLoader.createShader(resourceLocation);
            ShaderManager.loadMetadata(result);

            ShaderManager.data.put(resourceLocation, result);
        }
        return result;
    }

    private static void loadMetadata(ShaderManager.ShaderBinding binding) {
        if (binding.shader != null) {
            IResource resource;
            BufferedReader reader = null;
            try {
                resource = MC.getResourceManager().getResource(binding.metadataResource);

                reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
                JsonObject json = new JsonParser().parse(reader).getAsJsonObject();
                ShaderMetadataSection metadata = ClientProxy.metadataSerializer.parseMetadataSection("spacore:shader", json);
                for (String uniform : metadata.uniforms) {
                    binding.shader.uniforms.put(uniform, new ShaderUniform(binding.shader, uniform));
                }
                for (String attribute : metadata.attributes) {
                    binding.shader.attributes.put(attribute, new ShaderAttribute(binding.shader, attribute));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(reader);
            }
        }
    }

    public static class ShaderBinding {

        public final ResourceLocation resource;
        public final ResourceLocation vertexResource;
        public final ResourceLocation fragmentResource;
        public final ResourceLocation metadataResource;
        private ShaderProgram shader;

        public ShaderBinding(ResourceLocation resource) {
            this.resource = resource;
            this.vertexResource = new ResourceLocation(resource.getResourceDomain(), resource.getResourcePath() + ".vertex");
            this.fragmentResource = new ResourceLocation(resource.getResourceDomain(), resource.getResourcePath() + ".fragment");
            this.metadataResource = new ResourceLocation(resource.getResourceDomain(), resource.getResourcePath() + ".mcmeta");
        }

        public ShaderProgram getShader() {
            return this.shader;
        }
    }
}

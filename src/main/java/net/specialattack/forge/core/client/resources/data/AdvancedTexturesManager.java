package net.specialattack.forge.core.client.resources.data;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.Map;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.specialattack.forge.core.client.MC;

public class AdvancedTexturesManager implements IResourceManagerReloadListener {

    private static Map<ResourceLocation, AdvancedTexture> data = Maps.newHashMap();

    @Override
    public void onResourceManagerReload(IResourceManager manager) {
        for (AdvancedTexture data : AdvancedTexturesManager.data.values()) {
            IResource resource;
            try {
                resource = manager.getResource(data.resource);
            } catch (IOException e) {
                continue;
            }
            data.texture = (TextureMetadataSection) resource.getMetadata("spacore:texture");
        }
    }

    public static AdvancedTexturesManager.AdvancedTexture getTexture(ResourceLocation resourceLocation) {
        AdvancedTexturesManager.AdvancedTexture result = AdvancedTexturesManager.data.get(resourceLocation);
        if (result == null) {
            result = new AdvancedTexture(resourceLocation);
            IResource resource;
            try {
                resource = MC.getResourceManager().getResource(resourceLocation);
                if (resource.hasMetadata()) {
                    result.texture = (TextureMetadataSection) resource.getMetadata("spacore:texture");
                }
            } catch (IOException e) {
            }
            if (result.texture == null) {
                result.texture = AdvancedTexturesManager.createDefault();
            }
            AdvancedTexturesManager.data.put(resourceLocation, result);
        }
        return result;
    }

    private static TextureMetadataSection createDefault() {
        TextureMetadataSection result = new TextureMetadataSection();
        result.textureWidth = 128;
        result.textureHeight = 128;
        result.repeat = new TextureMetadataSection.Repeat(0, 0, 0, 0);
        return result;
    }

    public static class AdvancedTexture {

        public final ResourceLocation resource;
        private TextureMetadataSection texture;

        public AdvancedTexture(ResourceLocation resource) {
            this.resource = resource;
        }

        public TextureMetadataSection getTextureMetadata() {
            return this.texture;
        }

    }

}

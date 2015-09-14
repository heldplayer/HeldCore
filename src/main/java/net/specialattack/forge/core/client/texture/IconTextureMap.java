package net.specialattack.forge.core.client.texture;

import net.minecraft.client.renderer.texture.TextureMap;
import net.specialattack.forge.core.client.ClientProxy;

public class IconTextureMap extends TextureMap {

    public IconTextureMap(int id, String name) {
        super(id, name);
    }

    public void registerIcons() {
        super.registerIcons();

        for (IconHolder holder : ClientProxy.iconHolders) {
            holder.register(this);
        }
    }
}

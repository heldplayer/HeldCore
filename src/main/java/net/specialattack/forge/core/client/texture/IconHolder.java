package net.specialattack.forge.core.client.texture;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.specialattack.forge.core.client.ClientProxy;

public class IconHolder {

    private final String name;
    @SideOnly(Side.CLIENT)
    private TextureAtlasSprite icon;
    @SideOnly(Side.CLIENT)
    private int textureId;

    public IconHolder(String name) {
        this.name = name;
    }

    @SideOnly(Side.CLIENT)
    public void register(TextureMap map) {
        this.icon = map.registerSprite(new ResourceLocation(this.name));
        this.textureId = map.getGlTextureId();
    }

    @SideOnly(Side.CLIENT)
    public void bind() {
        GlStateManager.bindTexture(this.textureId);
    }

    @SideOnly(Side.CLIENT)
    public int getIconWidth() {
        if (this.icon == null) {
            return ClientProxy.missingSprite.getIconWidth();
        }
        return this.icon.getIconWidth();
    }

    @SideOnly(Side.CLIENT)
    public int getIconHeight() {
        if (this.icon == null) {
            return ClientProxy.missingSprite.getIconHeight();
        }
        return this.icon.getIconHeight();
    }

    @SideOnly(Side.CLIENT)
    public float getMinU() {
        if (this.icon == null) {
            return ClientProxy.missingSprite.getMinU();
        }
        return this.icon.getMinU();
    }

    @SideOnly(Side.CLIENT)
    public float getMaxU() {
        if (this.icon == null) {
            return ClientProxy.missingSprite.getMaxU();
        }
        return this.icon.getMaxU();
    }

    @SideOnly(Side.CLIENT)
    public float getInterpolatedU(double offset) {
        if (this.icon == null) {
            return ClientProxy.missingSprite.getInterpolatedU(offset);
        }
        return this.icon.getInterpolatedU(offset);
    }

    @SideOnly(Side.CLIENT)
    public float getMinV() {
        if (this.icon == null) {
            return ClientProxy.missingSprite.getMinV();
        }
        return this.icon.getMinV();
    }

    @SideOnly(Side.CLIENT)
    public float getMaxV() {
        if (this.icon == null) {
            return ClientProxy.missingSprite.getMaxV();
        }
        return this.icon.getMaxV();
    }

    @SideOnly(Side.CLIENT)
    public float getInterpolatedV(double offset) {
        if (this.icon == null) {
            return ClientProxy.missingSprite.getInterpolatedV(offset);
        }
        return this.icon.getInterpolatedV(offset);
    }

    @SideOnly(Side.CLIENT)
    public String getIconName() {
        return this.name;
    }
}

package net.specialattack.forge.core.client.texture;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;

public class IconHolder implements IIcon {

    private final String name;
    private IIcon icon;

    public IconHolder(String name) {
        this.name = name;
    }

    public void register(TextureMap map) {
        this.icon = map.registerIcon(this.name);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getIconWidth() {
        if (this.icon == null) {
            return 0;
        }
        return this.icon.getIconWidth();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getIconHeight() {
        if (this.icon == null) {
            return 0;
        }
        return this.icon.getIconHeight();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getMinU() {
        if (this.icon == null) {
            return 0.0F;
        }
        return this.icon.getMinU();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getMaxU() {
        if (this.icon == null) {
            return 0.0F;
        }
        return this.icon.getMaxU();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getInterpolatedU(double offset) {
        if (this.icon == null) {
            return 0.0F;
        }
        return this.icon.getInterpolatedU(offset);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getMinV() {
        if (this.icon == null) {
            return 0.0F;
        }
        return this.icon.getMinV();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getMaxV() {
        if (this.icon == null) {
            return 0.0F;
        }
        return this.icon.getMaxV();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getInterpolatedV(double offset) {
        if (this.icon == null) {
            return 0.0F;
        }
        return this.icon.getInterpolatedV(offset);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getIconName() {
        return this.name;
    }
}

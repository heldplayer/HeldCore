package net.specialattack.forge.core.client.gui;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class Color {

    public final int colorHex;
    public final float red, green, blue, alpha;

    public Color(int color) {
        this.colorHex = color;
        int alpha = (color >> 24) & 0xFF;
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;
        this.red = (float) red / 255.0F;
        this.green = (float) green / 255.0F;
        this.blue = (float) blue / 255.0F;
        this.alpha = (float) alpha / 255.0F;
    }

    public Color(float red, float green, float blue, float alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
        int iAlpha = (int) (alpha * 255.0F);
        int iRed = (int) (red * 255.0F);
        int iGreen = (int) (green * 255.0F);
        int iBlue = (int) (blue * 255.0F);
        this.colorHex = (iAlpha << 24) | (iRed << 16) | (iGreen << 8) | iBlue;
    }
}

package net.specialattack.forge.core.client.gui.deprecated.style.background;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.specialattack.forge.core.client.MC;
import net.specialattack.forge.core.client.gui.Color;
import net.specialattack.forge.core.client.gui.GuiHelper;
import net.specialattack.forge.core.client.gui.deprecated.SizeContext;
import net.specialattack.forge.core.client.gui.deprecated.element.SGComponent;

public class ColoredTextureBackground implements IBackground {

    private ResourceLocation texture;
    private float textureSize;
    private Color color;

    public ColoredTextureBackground(ResourceLocation texture, float textureSize, Color color) {
        this.texture = texture;
        this.textureSize = textureSize;
        this.color = color;
    }

    @Override
    public void drawBackground(SGComponent component) {
        GlStateManager.enableTexture2D();
        int width = component.getWidth(SizeContext.INNER);
        int height = component.getHeight(SizeContext.INNER);
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        GlStateManager.enableBlend();
        MC.getTextureManager().bindTexture(this.texture);
        GlStateManager.color(this.color.red, this.color.green, this.color.blue);
        GuiHelper.drawTexturedModalRect(component.getLeft(SizeContext.INNER), component.getTop(SizeContext.INNER), width, height, component.getZLevel(), 0, 0, width / this.textureSize, height / this.textureSize);
    }
}

package net.specialattack.forge.core.client.gui.deprecated.style.background;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.specialattack.forge.core.client.MC;
import net.specialattack.forge.core.client.gui.Color;
import net.specialattack.forge.core.client.gui.GuiHelper;
import net.specialattack.forge.core.client.gui.deprecated.SizeContext;
import net.specialattack.forge.core.client.gui.deprecated.element.SGComponent;
import net.specialattack.forge.core.client.resources.data.AdvancedTexturesManager;

public class AdvancedTextureBackground implements IBackground {

    private AdvancedTexturesManager.AdvancedTexture texture;
    private Color color;

    public AdvancedTextureBackground(ResourceLocation texture, Color color) {
        this.texture = AdvancedTexturesManager.getTexture(texture);
        this.color = color;
    }

    public AdvancedTextureBackground(ResourceLocation texture) {
        this.texture = AdvancedTexturesManager.getTexture(texture);
    }

    @Override
    public void drawBackground(SGComponent component) {
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        MC.getTextureManager().bindTexture(this.texture.resource);
        if (this.color != null) {
            GlStateManager.color(this.color.red, this.color.green, this.color.blue);
        } else {
            GlStateManager.color(1.0F, 1.0F, 1.0F);
        }
        GuiHelper.drawRepeatingBackground(component.getLeft(SizeContext.INNER), component.getTop(SizeContext.INNER), component.getWidth(SizeContext.INNER), component.getHeight(SizeContext.INNER), component.getZLevel(), this.texture.getTextureMetadata());
    }
}

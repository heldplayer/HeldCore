package net.specialattack.forge.core.client.gui.style.background;

import net.minecraft.util.ResourceLocation;
import net.specialattack.forge.core.client.GLState;
import net.specialattack.forge.core.client.MC;
import net.specialattack.forge.core.client.gui.Color;
import net.specialattack.forge.core.client.gui.GuiHelper;
import net.specialattack.forge.core.client.gui.SizeContext;
import net.specialattack.forge.core.client.gui.element.SGComponent;
import net.specialattack.forge.core.client.resources.data.AdvancedTexturesManager;
import org.lwjgl.opengl.GL11;

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
        GLState.glEnable(GL11.GL_TEXTURE_2D);
        GLState.glEnable(GL11.GL_BLEND);
        MC.getRenderEngine().bindTexture(this.texture.resource);
        if (this.color != null) {
            GLState.glColor3f(this.color.red, this.color.green, this.color.blue);
        } else {
            GLState.glColor3f(1.0F, 1.0F, 1.0F);
        }
        GuiHelper.drawRepeatingBackground(component.getLeft(SizeContext.INNER), component.getTop(SizeContext.INNER), component.getWidth(SizeContext.INNER), component.getHeight(SizeContext.INNER), component.getZLevel(), this.texture.getTextureMetadata());
    }

}

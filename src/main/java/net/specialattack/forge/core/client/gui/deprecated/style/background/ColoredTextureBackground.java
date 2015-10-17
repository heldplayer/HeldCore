package net.specialattack.forge.core.client.gui.deprecated.style.background;

import net.minecraft.util.ResourceLocation;
import net.specialattack.forge.core.client.GLState;
import net.specialattack.forge.core.client.MC;
import net.specialattack.forge.core.client.gui.Color;
import net.specialattack.forge.core.client.gui.GuiHelper;
import net.specialattack.forge.core.client.gui.deprecated.SizeContext;
import net.specialattack.forge.core.client.gui.deprecated.element.SGComponent;
import org.lwjgl.opengl.GL11;

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
        GLState.glEnable(GL11.GL_TEXTURE_2D);
        int width = component.getWidth(SizeContext.INNER);
        int height = component.getHeight(SizeContext.INNER);
        GLState.glDisable(GL11.GL_LIGHTING);
        GLState.glDisable(GL11.GL_FOG);
        GLState.glEnable(GL11.GL_BLEND);
        MC.getTextureManager().bindTexture(this.texture);
        GLState.glColor3f(this.color.red, this.color.green, this.color.blue);
        GuiHelper.drawTexturedModalRect(component.getLeft(SizeContext.INNER), component.getTop(SizeContext.INNER), width, height, component.getZLevel(), 0, 0, width / this.textureSize, height / this.textureSize);
    }

}

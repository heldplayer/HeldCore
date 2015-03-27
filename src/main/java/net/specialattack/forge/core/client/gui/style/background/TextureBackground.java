package net.specialattack.forge.core.client.gui.style.background;

import net.minecraft.util.ResourceLocation;
import net.specialattack.forge.core.client.MC;
import net.specialattack.forge.core.client.gui.GuiHelper;
import net.specialattack.forge.core.client.gui.GuiStateManager;
import net.specialattack.forge.core.client.gui.SizeContext;
import net.specialattack.forge.core.client.gui.element.SGComponent;
import org.lwjgl.opengl.GL11;

public class TextureBackground implements IBackground {

    private ResourceLocation texture;
    private float textureSize;

    public TextureBackground(ResourceLocation texture, float textureSize) {
        this.texture = texture;
        this.textureSize = textureSize;
    }

    @Override
    public void drawBackground(SGComponent component) {
        GuiStateManager.enableTextures();
        int width = component.getWidth(SizeContext.INNER);
        int height = component.getHeight(SizeContext.INNER);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_FOG);
        GL11.glEnable(GL11.GL_BLEND);
        MC.getRenderEngine().bindTexture(this.texture);
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        GuiHelper.drawTexturedModalRect(component.getLeft(SizeContext.INNER), component.getTop(SizeContext.INNER), width, height, component.getZLevel(), 0, 0, width / this.textureSize, height / this.textureSize);
    }

}

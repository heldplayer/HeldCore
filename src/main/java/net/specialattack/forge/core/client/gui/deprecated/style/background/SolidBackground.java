package net.specialattack.forge.core.client.gui.deprecated.style.background;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.specialattack.forge.core.client.GLState;
import net.specialattack.forge.core.client.gui.Color;
import net.specialattack.forge.core.client.gui.GuiHelper;
import net.specialattack.forge.core.client.gui.deprecated.SizeContext;
import net.specialattack.forge.core.client.gui.deprecated.element.SGComponent;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class SolidBackground implements IBackground {

    private Color color;

    public SolidBackground(Color color) {
        this.color = color;
    }

    @Override
    public void drawBackground(SGComponent component) {
        GLState.glDisable(GL11.GL_TEXTURE_2D);
        int left = component.getLeft(SizeContext.INNER);
        int top = component.getTop(SizeContext.INNER);
        GuiHelper.drawColoredRect(left, top, left + component.getWidth(SizeContext.INNER), top + component.getHeight(SizeContext.INNER), this.color.colorHex, component.getZLevel());
    }

}

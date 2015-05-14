package net.specialattack.forge.core.client.gui.style.background;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.specialattack.forge.core.client.GLState;
import net.specialattack.forge.core.client.gui.Color;
import net.specialattack.forge.core.client.gui.GuiHelper;
import net.specialattack.forge.core.client.gui.SizeContext;
import net.specialattack.forge.core.client.gui.element.SGComponent;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GradientBackground implements IBackground {

    private Color colorTop, colorBottom;

    public GradientBackground(Color colorTop, Color colorBottom) {
        this.colorTop = colorTop;
        this.colorBottom = colorBottom;
    }

    @Override
    public void drawBackground(SGComponent component) {
        GLState.glDisable(GL11.GL_TEXTURE_2D);
        int left = component.getLeft(SizeContext.INNER);
        int top = component.getTop(SizeContext.INNER);
        GuiHelper.drawGradientRect(left, top, left + component.getWidth(SizeContext.INNER), top + component.getHeight(SizeContext.INNER), this.colorTop.colorHex, this.colorBottom.colorHex, component.getZLevel());
    }

}

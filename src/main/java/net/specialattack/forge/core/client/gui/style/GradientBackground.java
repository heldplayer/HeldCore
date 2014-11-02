package net.specialattack.forge.core.client.gui.style;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.specialattack.forge.core.client.gui.Color;
import net.specialattack.forge.core.client.gui.GuiHelper;

@SideOnly(Side.CLIENT)
public class GradientBackground implements IBackground {

    private Color colorTop, colorBottom;

    public GradientBackground(Color colorTop, Color colorBottom) {
        this.colorTop = colorTop;
        this.colorBottom = colorBottom;
    }

    @Override
    public void draw(int startX, int startY, int endX, int endY, float zLevel) {
        GuiHelper.drawGradientRect(startX, startY, endX, endY, this.colorTop.colorHex, this.colorBottom.colorHex, zLevel);
    }

}

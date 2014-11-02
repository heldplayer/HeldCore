package net.specialattack.forge.core.client.gui.style;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.specialattack.forge.core.client.gui.Color;
import net.specialattack.forge.core.client.gui.GuiHelper;

@SideOnly(Side.CLIENT)
public class SolidBackground implements IBackground {

    private Color color;

    public SolidBackground(Color color) {
        this.color = color;
    }

    @Override
    public void draw(int startX, int startY, int endX, int endY, float zLevel) {
        GuiHelper.drawGradientRect(startX, startY, endX, endY, this.color.colorHex, this.color.colorHex, zLevel);
    }

}

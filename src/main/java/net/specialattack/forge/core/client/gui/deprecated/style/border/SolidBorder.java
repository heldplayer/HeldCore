package net.specialattack.forge.core.client.gui.deprecated.style.border;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.specialattack.forge.core.client.gui.Color;
import net.specialattack.forge.core.client.gui.GuiHelper;
import net.specialattack.forge.core.client.gui.deprecated.SizeContext;
import net.specialattack.forge.core.client.gui.deprecated.element.SGComponent;

@SideOnly(Side.CLIENT)
public class SolidBorder implements IBorder {

    private Color color;
    private int width;

    public SolidBorder(Color color, int width) {
        this.color = color;
        this.width = width;
    }

    @Override
    public void drawBorder(SGComponent component) {
        int left = component.getLeft(SizeContext.BORDER);
        int top = component.getTop(SizeContext.BORDER);
        int right = left + component.getWidth(SizeContext.BORDER);
        int bottom = top + component.getHeight(SizeContext.BORDER);
        float zLevel = component.getZLevel();
        GuiHelper.drawColoredRect(left, top, left + this.width, bottom, this.color.colorHex, zLevel); // Left border
        GuiHelper.drawColoredRect(right - this.width, top, right, bottom, this.color.colorHex, zLevel); // Right border

        GuiHelper.drawColoredRect(left + this.width, top, right - this.width, top + this.width, this.color.colorHex, zLevel); // Top border
        GuiHelper.drawColoredRect(left + this.width, bottom - this.width, right - this.width, bottom, this.color.colorHex, zLevel); // Bottom border
    }

    @Override
    public int getSize() {
        return this.width;
    }

}

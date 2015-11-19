package net.specialattack.forge.core.client.gui.deprecated.style.outline;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.specialattack.forge.core.client.gui.Color;
import net.specialattack.forge.core.client.gui.GuiHelper;
import net.specialattack.forge.core.client.gui.deprecated.SizeContext;
import net.specialattack.forge.core.client.gui.deprecated.element.SGComponent;

@SideOnly(Side.CLIENT)
public class SolidOutline implements IOutline {

    private Color color;
    private int width;

    public SolidOutline(Color color, int width) {
        this.color = color;
        this.width = width;
    }

    @Override
    public void drawOutline(SGComponent component) {
        int left = component.getLeft(SizeContext.OUTLINE);
        int top = component.getTop(SizeContext.OUTLINE);
        int right = left + component.getWidth(SizeContext.OUTLINE);
        int bottom = top + component.getHeight(SizeContext.OUTLINE);
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

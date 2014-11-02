package net.specialattack.forge.core.client.gui.style;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IBorder {

    void draw(int startX, int startY, int endX, int endY, float zLevel);

}

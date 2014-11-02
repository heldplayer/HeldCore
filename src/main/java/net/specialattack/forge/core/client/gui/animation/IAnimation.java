package net.specialattack.forge.core.client.gui.animation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.specialattack.forge.core.client.gui.elements.IGuiElement;

@SideOnly(Side.CLIENT)
public interface IAnimation {

    void progressTicks(int ticks);

    void prepareDraw(IGuiElement element, float partialTicks);

}

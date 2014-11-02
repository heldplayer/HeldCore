package net.specialattack.forge.core.client.gui.screens;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.specialattack.forge.core.client.gui.elements.IGuiElement;

@SideOnly(Side.CLIENT)
public interface IGuiScreen extends IGuiElement {

    boolean wantsFramebuffer();

}

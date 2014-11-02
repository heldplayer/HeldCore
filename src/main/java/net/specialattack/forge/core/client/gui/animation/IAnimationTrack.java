package net.specialattack.forge.core.client.gui.animation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.specialattack.forge.core.client.gui.elements.IGuiElement;

@SideOnly(Side.CLIENT)
public interface IAnimationTrack {

    void updateAnimation(IGuiElement element, float ticks);

}

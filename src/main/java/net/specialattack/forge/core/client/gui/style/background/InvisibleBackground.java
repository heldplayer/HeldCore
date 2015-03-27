package net.specialattack.forge.core.client.gui.style.background;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.specialattack.forge.core.client.gui.element.SGComponent;

@SideOnly(Side.CLIENT)
public class InvisibleBackground implements IBackground {

    @Override
    public void drawBackground(SGComponent component) {
    }

}

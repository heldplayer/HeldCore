package net.specialattack.forge.core.client.gui.deprecated.style.background;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.specialattack.forge.core.client.gui.deprecated.element.SGComponent;

@SideOnly(Side.CLIENT)
public class InvisibleBackground implements IBackground {

    @Override
    public void drawBackground(SGComponent component) {
    }
}

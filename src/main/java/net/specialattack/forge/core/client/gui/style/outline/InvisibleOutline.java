package net.specialattack.forge.core.client.gui.style.outline;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.specialattack.forge.core.client.gui.element.SGComponent;

@SideOnly(Side.CLIENT)
public class InvisibleOutline implements IOutline {

    private int width;

    public InvisibleOutline(int width) {
        this.width = width;
    }

    @Override
    public void drawOutline(SGComponent component) {
    }

    @Override
    public int getSize() {
        return this.width;
    }

}

package net.specialattack.forge.core.client.gui.style.border;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.specialattack.forge.core.client.gui.element.SGComponent;

@SideOnly(Side.CLIENT)
public class InvisibleBorder implements IBorder {

    private int width;

    public InvisibleBorder(int width) {
        this.width = width;
    }

    @Override
    public void drawBorder(SGComponent component) {
    }

    @Override
    public int getSize() {
        return this.width;
    }

}

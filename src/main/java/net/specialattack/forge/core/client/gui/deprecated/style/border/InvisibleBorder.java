package net.specialattack.forge.core.client.gui.deprecated.style.border;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.specialattack.forge.core.client.gui.deprecated.element.SGComponent;

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

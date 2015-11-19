package net.specialattack.forge.core.client.gui.deprecated.style.outline;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.specialattack.forge.core.client.gui.deprecated.element.SGComponent;

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

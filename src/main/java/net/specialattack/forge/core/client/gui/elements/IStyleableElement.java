package net.specialattack.forge.core.client.gui.elements;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.specialattack.forge.core.client.gui.Color;
import net.specialattack.forge.core.client.gui.style.IBackground;
import net.specialattack.forge.core.client.gui.style.IBorder;

@SideOnly(Side.CLIENT)
public interface IStyleableElement {

    void setBackground(IBackground background);

    IBackground getBackground();

    boolean hasBackground();

    void setBorder(IBorder border);

    IBorder getBorder();

    boolean hasBorder();

    void setTextColor(Color color);

    Color getTextColor();

    void setDisabledColor(Color color);

    Color getDisabledColor(Color color);

}

package net.specialattack.forge.core.client.gui.deprecated.element;

import net.specialattack.forge.core.client.gui.deprecated.layout.Location;
import net.specialattack.forge.core.client.gui.deprecated.layout.Region;

public interface IComponentHolder {

    void focusChangeUp(SGComponent component);

    void focusChangeDown(SGComponent component);

    void elementClicked(SGComponent component, int button);

    Region getRenderingRegion();

    Location getChildOffset();

    void updateLayout();

    int getMouseState();

    void addPopout(SGComponent component);

    void removePopout(SGComponent component);

    Region findPopoutRegion(boolean horizontal, Region around, Region size);
}

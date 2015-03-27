package net.specialattack.forge.core.client.gui.element;

import net.specialattack.forge.core.client.gui.layout.Region;

public interface IComponentHolder {

    void focusChangeUp(SGComponent component);

    void focusChangeDown(SGComponent component);

    void elementClicked(SGComponent component, int button);

    Region getRenderingRegion();

    void updateLayout();

    int getMouseState();

    void addPopout(SGComponent component);

    void removePopout(SGComponent component);
}

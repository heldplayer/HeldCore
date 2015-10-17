package net.specialattack.forge.core.client.gui.deprecated.element;

import net.specialattack.forge.core.client.gui.deprecated.style.StyleDefs;
import net.specialattack.forge.core.client.gui.deprecated.style.border.InvisibleBorder;

public class SGSeperator extends SGComponent {

    public SGSeperator() {
        this.setPreferredInnerSize(1, 1);
        this.setBackground(StyleDefs.BACKGROUND_SPLITTER);
        this.setBorder(new InvisibleBorder(2));
    }

}

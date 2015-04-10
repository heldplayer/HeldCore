package net.specialattack.forge.core.client.gui.element;

import net.specialattack.forge.core.client.gui.style.StyleDefs;
import net.specialattack.forge.core.client.gui.style.border.InvisibleBorder;

public class SGSeperator extends SGComponent {

    public SGSeperator() {
        this.setPreferredInnerSize(1, 1);
        this.setBackground(StyleDefs.BACKGROUND_SPLITTER);
        this.setBorder(new InvisibleBorder(2));
    }

}

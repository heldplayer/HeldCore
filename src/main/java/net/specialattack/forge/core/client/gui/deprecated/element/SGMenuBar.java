package net.specialattack.forge.core.client.gui.deprecated.element;

import net.specialattack.forge.core.client.gui.deprecated.layout.FlowDirection;
import net.specialattack.forge.core.client.gui.deprecated.layout.FlowLayout;
import net.specialattack.forge.core.client.gui.deprecated.layout.FlowSGLayoutManager;
import net.specialattack.forge.core.client.gui.deprecated.style.StyleDefs;

public class SGMenuBar extends SGComponent {

    public SGMenuBar() {
        this.setLayoutManager(new FlowSGLayoutManager(FlowDirection.HORIZONTAL, FlowLayout.CENTER));
        this.setBackground(StyleDefs.BACKGROUND_MENU_BAR);
    }

    @Override
    public void addChild(SGComponent child) {
        this.addChild(child, true);
    }

    @Override
    public void addChild(SGComponent child, Object param) {
        // We might not allow non-menu items to be in here
        //if (child == null || !(child instanceof SGMenu)) {
        //throw new IllegalArgumentException("Can only add Menus to a menu bar");
        //}
        super.addChild(child, param);
    }

    @Override
    public void elementClicked(SGComponent component, int button) {
        super.elementClicked(component, button);
        boolean componentIn = this.isComponentIn(component);
        for (SGComponent child : this.getChildren()) {
            if (child instanceof SGMenu) {
                ((SGMenu) child).canCount = componentIn;
            }
        }
    }

}

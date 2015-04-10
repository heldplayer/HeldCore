package net.specialattack.forge.core.client.gui.element;

import java.util.Collections;
import java.util.List;
import net.specialattack.forge.core.client.gui.Color;
import net.specialattack.forge.core.client.gui.GuiHelper;
import net.specialattack.forge.core.client.gui.layout.BorderedSGLayoutManager;
import net.specialattack.forge.core.client.gui.layout.FlowLayout;
import net.specialattack.forge.core.client.gui.layout.Region;
import net.specialattack.forge.core.client.gui.layout.SGLayoutManager;
import net.specialattack.forge.core.client.gui.style.StyleDefs;
import net.specialattack.forge.core.client.gui.style.border.InvisibleBorder;

// A simple item in a menu
public class SGMenuItem extends SGInteractable {

    private SGLabel innerLabel;

    public SGMenuItem(String text) {
        super.setLayoutManager(new BorderedSGLayoutManager());
        super.addChild(this.innerLabel = new SGLabel(text), BorderedSGLayoutManager.Border.CENTER);
        this.innerLabel.setLayout(FlowLayout.MIN, FlowLayout.CENTER);
        //this.innerLabel.setBorder(new InvisibleBorder(1));
        //this.setBackgrounds(StyleDefs.BACKGROUND_BUTTON_NORMAL, StyleDefs.BACKGROUND_BUTTON_HOVER, StyleDefs.BACKGROUND_BUTTON_DISABLED);
        //this.setBackgrounds(StyleDefs.BACKGROUND_MENU_ITEM_NORMAL, StyleDefs.BACKGROUND_MENU_ITEM_HOVER, StyleDefs.BACKGROUND_MENU_ITEM_DISABLED);
        this.setColors(StyleDefs.COLOR_MENU_ITEM_NORMAL, StyleDefs.COLOR_MENU_ITEM_HOVER, StyleDefs.COLOR_MENU_ITEM_DISABLED);
        this.setHasShadow(false);
    }

    public SGMenuItem() {
        this(null);
    }

    public void setText(String text) {
        this.innerLabel.setText(text);
    }

    public void setShouldSplit(boolean value) {
        this.innerLabel.setShouldSplit(value);
    }

    public void setHasShadow(boolean hasShadow) {
        this.innerLabel.setHasShadow(hasShadow);
    }

    @Override
    public Region predictSize() {
        //Region inner = super.predictSize();
        //return new Region(0, 0, inner.width + 2, inner.height - 2);
        return super.predictSize(); // FIXME?
    }

    @Override
    public void setSizeRestrictions(int width, int height) {
        //super.setSizeRestrictions(width - 2, height + 2);
        super.setSizeRestrictions(width, height); // FIXME
    }

    @Override
    public void addChild(SGComponent child, Object param) {
        // No children allowed here
    }

    @Override
    public void removeChild(SGComponent child) {
        // Don't even think about it
    }

    @Override
    public List<SGComponent> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public void setLayoutManager(SGLayoutManager layoutManager) {
        // We don't need no layout manager
    }

    @Override
    public void onClick(int mouseX, int mouseY, int button) {
        if (this.isEnabled() && button == 0) {
            GuiHelper.playButtonClick();
        }
        super.onClick(mouseX, mouseY, button);
    }

    @Override
    public void setMouseOver(boolean value) {
        super.setMouseOver(value);
        this.innerLabel.setColor(this.getTextColor());
    }

    @Override
    public void setColor(Color color) {
        super.setColor(color);
        this.innerLabel.setColor(this.getTextColor());
    }

    @Override
    public void setColors(Color normal, Color hover, Color disabled) {
        super.setColors(normal, hover, disabled);
        this.innerLabel.setColor(this.getTextColor());
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.innerLabel.setColor(this.getTextColor());
    }
}

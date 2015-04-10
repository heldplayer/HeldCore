package net.specialattack.forge.core.client.gui.element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.specialattack.forge.core.client.gui.Color;
import net.specialattack.forge.core.client.gui.GuiHelper;
import net.specialattack.forge.core.client.gui.layout.*;
import net.specialattack.forge.core.client.gui.style.StyleDefs;

public class SGComboBox extends SGInteractable {

    private SGComponent popout = new SGPopout(this);
    private SGLabel innerLabel;
    private List<String> values;

    public SGComboBox(List<String> values) {
        if (values == null) {
            this.values = new ArrayList<String>();
        } else {
            this.values = new ArrayList<String>(values);
        }
        this.popout.setLayoutManager(new FlowSGLayoutManager(FlowDirection.VERTICAL, FlowLayout.MIN));
        this.popout.setBackground(StyleDefs.BACKGROUND_MENU_ITEM_NORMAL);
        this.popout.setBorder(StyleDefs.BORDER_MENU);
        //this.popout.setParent(this);
        this.popout.setVisible(false);
        super.setLayoutManager(new BorderedSGLayoutManager());
        super.addChild(this.innerLabel = new SGLabel(), BorderedSGLayoutManager.Border.CENTER);
        this.setBackgrounds(StyleDefs.BACKGROUND_BUTTON_NORMAL, StyleDefs.BACKGROUND_BUTTON_HOVER, StyleDefs.BACKGROUND_BUTTON_DISABLED);
        this.rebuildOptions();
    }

    public SGComboBox(String... values) {
        this(Arrays.asList(values));
    }

    public SGComboBox() {
        this((List<String>) null);
    }

    public void setValue(String text) {
        if (values.contains(text)) {
            this.innerLabel.setText(text);
        } else {
            this.innerLabel.setText("");
        }
    }

    public void addOption(String option) {
        this.values.add(option);
        this.rebuildOptions();
    }

    public void removeOption(String option) {
        this.values.remove(option);
        this.rebuildOptions();
    }

    private void rebuildOptions() {
        for (SGComponent component : this.popout.getChildren()) {
            this.popout.removeChild(component);
        }
        for (String value : this.values) {
            SGLabel label = new SGLabel(value);
            label.setLayout(FlowLayout.MIN, FlowLayout.CENTER);
            this.popout.addChild(label, true);
        }
    }

    @Override
    public Region predictSize() {
        Region inner = super.predictSize();
        return new Region(0, 0, inner.width + 4, inner.height + 4);
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
    public void clickHappened(int mouseX, int mouseY, int button) {
        if (this.isEnabled() && button == 0) {
            GuiHelper.playButtonClick();
        }
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

    @Override
    public void onClick(int mouseX, int mouseY, int button) {
        super.onClick(mouseX, mouseY, button);
        if (button == 0) {
            if (this.popout.isVisible()) {
                this.hidePopout();
            } else {
                this.showPopout();
            }
        }
    }

    @Override
    public void elementClicked(SGComponent component, int button) {
        super.elementClicked(component, button);
        this.popout.elementClicked(component, button);
        if (!this.isComponentIn(component)) {
            this.hidePopout();
        }
    }

    @Override
    public boolean isComponentIn(SGComponent component) {
        if (this == component || this.popout == component) {
            return true;
        }
        List<SGComponent> children = this.popout.getChildren();
        if (children != null) {
            for (SGComponent child : children) {
                if (child == component || child.isComponentIn(component)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void showPopout() {
        if (!this.popout.isVisible()) {
            Region predicted = this.popout.predictSize();
            Region rendering = this.getRenderingRegion();
            this.popout.setDimensions(this.findPopoutRegion(false, rendering, predicted));
            this.popout.updateLayout();
            this.popout.setVisible(true);
            this.addPopout(this.popout);
        }
    }

    private void hidePopout() {
        if (this.popout.isVisible()) {
            this.popout.setVisible(false);
            this.removePopout(this.popout);
        }
    }
}

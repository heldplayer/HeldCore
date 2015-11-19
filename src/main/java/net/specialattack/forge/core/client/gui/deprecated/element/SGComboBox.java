package net.specialattack.forge.core.client.gui.deprecated.element;

import java.util.*;
import net.specialattack.forge.core.client.gui.Color;
import net.specialattack.forge.core.client.gui.GuiHelper;
import net.specialattack.forge.core.client.gui.deprecated.MouseHandler;
import net.specialattack.forge.core.client.gui.deprecated.layout.*;
import net.specialattack.forge.core.client.gui.deprecated.style.StyleDefs;
import net.specialattack.forge.core.client.gui.deprecated.style.background.IBackground;

public class SGComboBox extends SGInteractable {

    private SGComponent popout = new SGPopout(this);
    private SGLabel innerLabel = new SGLabel() {
        @Override
        public Color getColor() {
            return SGComboBox.this.getTextColor();
        }
    };
    private List<Value> values;

    public SGComboBox(Iterable<String> values) {
        this.values = new ArrayList<Value>();
        if (values != null) {
            for (String value : values) {
                this.values.add(new Value(value));
            }
        }
        this.popout.setLayoutManager(new FlowSGLayoutManager(FlowDirection.VERTICAL, FlowLayout.MIN));
        this.popout.setBackground(StyleDefs.BACKGROUND_MENU_ITEM_NORMAL);
        this.popout.setBorder(StyleDefs.BORDER_MENU);
        //this.popout.setParent(this);
        this.popout.setVisible(false);
        super.setLayoutManager(new BorderedSGLayoutManager());
        super.addChild(this.innerLabel, BorderedSGLayoutManager.Border.CENTER);
        this.innerLabel.setText("");
        this.setBackgrounds(StyleDefs.BACKGROUND_BUTTON_NORMAL, StyleDefs.BACKGROUND_BUTTON_HOVER, StyleDefs.BACKGROUND_BUTTON_DISABLED);
        this.rebuildOptions();
    }

    public SGComboBox(String... args) {
        this(Arrays.asList(args));
    }

    public void setValue(String value) {
        Iterator<Value> i = this.values.iterator();
        String selected = "";
        while (i.hasNext()) {
            Value next = i.next();
            if (next.getValue().equals(value)) {
                next.getOption().selected = true;
                selected = value;
            } else {
                next.getOption().selected = false;
            }
        }
        this.innerLabel.setText(selected);
        this.valueChanged(selected);
    }

    public void valueChanged(String value) {
    }

    public void addOption(String value) {
        this.values.add(new Value(value));
        this.rebuildOptions();
    }

    public void removeOption(String value) {
        Iterator<Value> i = this.values.iterator();
        while (i.hasNext()) {
            Value next = i.next();
            if (next.getValue().equals(value)) {
                i.remove();
            }
        }
        this.rebuildOptions();
    }

    private void rebuildOptions() {
        for (SGComponent component : this.popout.getChildren()) {
            this.popout.removeChild(component);
        }
        for (Value value : this.values) {
            this.popout.addChild(value.option, true);
        }
    }

    @Override
    public Region predictSize() {
        Region inner = super.predictSize();
        return new Region(0, 0, inner.getWidth() + 4, inner.getHeight() + 4);
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
        //        List<SGComponent> children = this.popout.getChildren();
        //        if (children != null) {
        //            for (SGComponent child : children) {
        //                if (child == component || child.isComponentIn(component)) {
        //                    return true;
        //                }
        //            }
        //        }
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

    private class Value {

        private String value;
        private Option option;
        private boolean selected;

        public Value(String value) {
            this.value = value;
            this.option = new Option(value);
            this.option.setMouseHandler(new MouseHandler() {
                @Override
                public boolean onClick(int mouseX, int mouseY, int button) {
                    if (Value.this.option.isEnabled()) {
                        SGComboBox.this.setValue(Value.this.value);
                        return true;
                    }
                    return false;
                }
            });
        }

        public String getValue() {
            return this.value;
        }

        public Option getOption() {
            return this.option;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
            this.option.setSelected(selected);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Value value1 = (Value) o;
            return this.value.equals(value1.value);
        }

        @Override
        public int hashCode() {
            return this.value.hashCode();
        }
    }

    public static class Option extends SGInteractable {

        private SGLabel innerLabel = new SGLabel() {
            @Override
            public Color getColor() {
                return Option.this.getTextColor();
            }
        };
        private Color colorSelected = StyleDefs.COLOR_BUTTON_TEXT;
        private IBackground backgroundSelected;
        private boolean selected;

        public Option(String text) {
            super.setLayoutManager(new BorderedSGLayoutManager());
            super.addChild(this.innerLabel, BorderedSGLayoutManager.Border.CENTER);
            this.innerLabel.setText(text);
            this.innerLabel.setLayout(FlowLayout.MIN, FlowLayout.CENTER);
            this.setBackgrounds(StyleDefs.BACKGROUND_COMBO_OPTION_NORMAL, StyleDefs.BACKGROUND_COMBO_OPTION_HOVER, StyleDefs.BACKGROUND_COMBO_OPTION_DISABLED, StyleDefs.BACKGROUND_COMBO_OPTION_SELECTED);
            this.setColors(StyleDefs.COLOR_COMBO_OPTION_NORMAL, StyleDefs.COLOR_COMBO_OPTION_HOVER, StyleDefs.COLOR_COMBO_OPTION_DISABLED, StyleDefs.COLOR_COMBO_OPTION_SELECTED);
            this.setHasShadow(false);
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
        }

        @Override
        public void setColor(Color color) {
            super.setColor(color);
        }

        @Override
        public void setColors(Color normal, Color hover, Color disabled) {
            super.setColors(normal, hover, disabled);
        }

        public void setColors(Color normal, Color hover, Color disabled, Color selected) {
            this.setColors(normal, hover, disabled);
            this.colorSelected = selected;
        }

        @Override
        public void setBackground(IBackground background) {
            super.setBackground(background);
        }

        @Override
        public void setBackgrounds(IBackground normal, IBackground hover, IBackground disabled) {
            super.setBackgrounds(normal, hover, disabled);
        }

        public void setBackgrounds(IBackground normal, IBackground hover, IBackground disabled, IBackground selected) {
            this.setBackgrounds(normal, hover, disabled);
            this.backgroundSelected = selected;
        }

        @Override
        public IBackground getBackground() {
            return this.selected ? this.backgroundSelected : super.getBackground();
        }

        @Override
        public Color getTextColor() {
            return this.selected ? this.colorSelected : super.getTextColor();
        }

        @Override
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }
    }

}

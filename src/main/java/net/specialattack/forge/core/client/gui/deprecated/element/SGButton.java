package net.specialattack.forge.core.client.gui.deprecated.element;

import java.util.Collections;
import java.util.List;
import net.specialattack.forge.core.client.gui.Color;
import net.specialattack.forge.core.client.gui.GuiHelper;
import net.specialattack.forge.core.client.gui.deprecated.layout.BorderedSGLayoutManager;
import net.specialattack.forge.core.client.gui.deprecated.layout.Region;
import net.specialattack.forge.core.client.gui.deprecated.layout.SGLayoutManager;
import net.specialattack.forge.core.client.gui.deprecated.style.StyleDefs;

public class SGButton extends SGInteractable {

    private SGLabel innerLabel = new SGLabel() {
        @Override
        public Color getColor() {
            return SGButton.this.getTextColor();
        }
    };

    public SGButton(String text) {
        super.setLayoutManager(new BorderedSGLayoutManager());
        super.addChild(this.innerLabel, BorderedSGLayoutManager.Border.CENTER);
        this.innerLabel.setText(text);
        this.setBackgrounds(StyleDefs.BACKGROUND_BUTTON_NORMAL, StyleDefs.BACKGROUND_BUTTON_HOVER, StyleDefs.BACKGROUND_BUTTON_DISABLED);
        this.setText(text);
    }

    public SGButton() {
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
}

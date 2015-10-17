package net.specialattack.forge.core.client.gui.deprecated.element;

import net.specialattack.forge.core.client.gui.Color;
import net.specialattack.forge.core.client.gui.deprecated.SizeContext;
import net.specialattack.forge.core.client.gui.deprecated.layout.Location;
import net.specialattack.forge.core.client.gui.deprecated.style.StyleDefs;
import net.specialattack.forge.core.client.gui.deprecated.style.background.IBackground;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public abstract class SGInteractable extends SGComponent {

    private Color color = StyleDefs.COLOR_BUTTON_TEXT, colorHover = StyleDefs.COLOR_BUTTON_TEXT_HOVER, colorDisabled = StyleDefs.COLOR_TEXTBOX_TEXT_DISABLED;
    private IBackground background, backgroundHover, backgroundDisabled;
    private boolean enabled = true;

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public Pair<SGComponent, Location> cascadeMouse(int mouseX, int mouseY) {
        if (this.isVisible() && this.isMouseOver(mouseX, mouseY)) {
            return ImmutablePair.of((SGComponent) this, new Location(mouseX - this.getLeft(SizeContext.INNER), mouseY - this.getTop(SizeContext.INNER)));
        }
        return null;
    }

    @Override
    public void setBackground(IBackground background) {
        this.background = this.backgroundHover = this.backgroundDisabled = background;
    }

    public void setBackgrounds(IBackground normal, IBackground hover, IBackground disabled) {
        this.background = normal;
        this.backgroundHover = hover;
        this.backgroundDisabled = disabled;
    }

    @Override
    public IBackground getBackground() {
        return this.isEnabled() ? this.isMouseOver() ? this.backgroundHover : this.background : this.backgroundDisabled;
    }

    public Color getTextColor() {
        return this.isEnabled() ? this.isMouseOver() ? this.colorHover : this.color : this.colorDisabled;
    }

    public void setColor(Color color) {
        if (color == null) {
            throw new IllegalArgumentException("Color cannot be null");
        }
        this.color = this.colorHover = this.colorDisabled = color;
    }

    public void setColors(Color normal, Color hover, Color disabled) {
        if (normal == null || hover == null || disabled == null) {
            throw new IllegalArgumentException("Color cannot be null");
        }
        this.color = normal;
        this.colorHover = hover;
        this.colorDisabled = disabled;
    }

}

package net.specialattack.forge.core.client.gui.element;

import java.util.List;
import net.specialattack.forge.core.client.gui.SizeContext;
import net.specialattack.forge.core.client.gui.layout.FlowDirection;
import net.specialattack.forge.core.client.gui.layout.FlowLayout;
import net.specialattack.forge.core.client.gui.layout.FlowSGLayoutManager;
import net.specialattack.forge.core.client.gui.layout.Region;
import net.specialattack.forge.core.client.gui.style.StyleDefs;

// A menu item that can have sub-items
public class SGMenu extends SGMenuItem {

    private SGComponent popout = new SGPopout(this) {
        @Override
        public boolean onScroll(int mouseX, int mouseY, int scroll) {
            boolean result = super.onScroll(mouseX, mouseY, scroll);
            this.getRoot().elementClicked(this, 0);
            return result;
        }
    };
    protected boolean inMenu = false;
    protected boolean canCount = false;
    private int counter;

    public SGMenu(String text) {
        super(text);
        this.popout.setLayoutManager(new FlowSGLayoutManager(FlowDirection.VERTICAL, FlowLayout.MIN));
        this.popout.setBackground(StyleDefs.BACKGROUND_MENU_ITEM_NORMAL);
        this.popout.setBorder(StyleDefs.BORDER_MENU);
        this.popout.setVisible(false);
    }

    public SGMenu() {
        this(null);
    }

    @Override
    public void updateTick() {
        super.updateTick();
        this.popout.updateTick();
        if (this.canCount) {
            if (this.isMouseOver()) {
                if (this.counter <= 5) {
                    if (this.counter == 5 && !this.popout.isVisible()) {
                        this.showPopout();
                        this.getRoot().elementClicked(this, 0);
                    }
                    this.counter++;
                }
            } else {
                this.counter = 0;
            }
        } else {
            this.counter = 0;
        }
    }

    @Override
    public Region predictSize() {
        Region inner = super.predictSize();
        if (this.inMenu) {
            return inner.expanded(12, 0);
        } else {
            return inner;
        }
    }

    @Override
    public void setSizeRestrictions(int width, int height) {
        super.setSizeRestrictions(width + 12, height);
    }

    @Override
    public void addChild(SGComponent child) {
        this.addChild(child, true);
    }

    @Override
    public void addChild(SGComponent child, Object param) {
        if (child instanceof SGMenu) {
            SGMenu menu = (SGMenu) child;
            menu.inMenu = true;
            menu.canCount = true;
        }
        this.popout.addChild(child, param);
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Override
    public void removeChild(SGComponent child) {
        this.popout.removeChild(child);
    }

    @Override
    public List<SGComponent> getChildren() {
        return this.popout.getChildren();
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
    public void drawBackground(int mouseX, int mouseY, float partialTicks) {
        super.drawBackground(mouseX, mouseY, partialTicks);
        if (this.inMenu) {
            int left = this.getLeft(SizeContext.INNER) + this.getWidth(SizeContext.INNER) - 8;
            int top = (this.getHeight(SizeContext.INNER) + this.font.FONT_HEIGHT) / 2 + this.getTop(SizeContext.INNER) - 8;
            this.font.drawString("\u25B6", left, top, this.getTextColor().colorHex);
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
            this.popout.setDimensions(this.findPopoutRegion(this.inMenu, this.getRenderingRegion(), this.popout.predictSize()));
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

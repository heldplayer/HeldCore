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

    private FlowDirection flowDirection;
    private SGComponent popout = new SGScrollPane() {
        {
            this.setScrollbarSize(2);
        }

        @Override
        public void setParent(IComponentHolder holder) {
        }

        @Override
        public IComponentHolder getParent() {
            return SGMenu.this;
        }

        @Override // FIXME
        public Region getRenderingRegion() {
            return this.getDimensions();
        }
    };
    protected boolean inMenu = false;
    protected boolean canCount = false;
    private int counter;

    public SGMenu(String text, FlowDirection direction) {
        super(text);
        this.setFlowDirection(direction);
        this.popout.setLayoutManager(new FlowSGLayoutManager(direction, FlowLayout.MIN));
        this.popout.setBackground(StyleDefs.BACKGROUND_MENU_ITEM_NORMAL);
        this.popout.setBorder(StyleDefs.BORDER_MENU);
        //this.popout.setParent(this);
        this.popout.setVisible(false);

    }

    public SGMenu(String text) {
        this(text, FlowDirection.VERTICAL);
    }

    public SGMenu() {
        this(null, FlowDirection.VERTICAL);
    }

    public void setFlowDirection(FlowDirection direction) {
        this.flowDirection = direction == null ? FlowDirection.VERTICAL : direction;
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
            return new Region(0, 0, inner.width + 12, inner.height);
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
        if (child == null || !(child instanceof SGMenuItem || child instanceof SGSeperator)) {
            throw new IllegalArgumentException("Can only add Menu Items or Splitters to a menu");
        }
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
    public void updateLayout() {
        super.updateLayout();

        Region predicted = this.popout.predictSize();
        Region rendering = this.getRenderingRegion();
        if (this.inMenu) {
            this.popout.setDimensions(rendering.left + rendering.width, rendering.top, predicted.width, predicted.height + 2);
        } else {
            this.popout.setDimensions(rendering.left, rendering.top + rendering.height, predicted.width, predicted.height + 2);
        }
        this.popout.updateLayout();

    }
    //
    //    @Override
    //    public Pair<SGComponent, Location> cascadeMouse(int mouseX, int mouseY) {
    //        if (!this.isVisible()) {
    //            return null;
    //        }
    //        if (this.isErrored() && this.isMouseOver(mouseX, mouseY)) {
    //            return ImmutablePair.of((SGComponent) this, new Location(mouseX - this.getLeft(SizeContext.INNER), mouseY - this.getTop(SizeContext.INNER)));
    //        }
    //        try {
    //            if (this.isMouseOver(mouseX, mouseY)) {
    //                return ImmutablePair.of((SGComponent) this, new Location(mouseX - this.getLeft(SizeContext.INNER), mouseY - this.getTop(SizeContext.INNER)));
    //            }
    //            Pair<SGComponent, Location> over = this.popout.cascadeMouse(mouseX, mouseY);
    //            if (over != null) {
    //                return over;
    //            }
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //            this.setErrored();
    //        }
    //        return null;
    //    }

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

    @Override // FIXME
    public Region getRenderingRegion() {
        return super.getRenderingRegion();
    }

    private void showPopout() {
        if (!this.popout.isVisible()) {
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

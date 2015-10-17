package net.specialattack.forge.core.client.gui.deprecated.element;

import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.MathHelper;
import net.specialattack.forge.core.client.GLState;
import net.specialattack.forge.core.client.gui.Color;
import net.specialattack.forge.core.client.gui.GuiHelper;
import net.specialattack.forge.core.client.gui.deprecated.SGUtils;
import net.specialattack.forge.core.client.gui.deprecated.SizeContext;
import net.specialattack.forge.core.client.gui.deprecated.layout.BorderedSGLayoutManager;
import net.specialattack.forge.core.client.gui.deprecated.layout.Location;
import net.specialattack.forge.core.client.gui.deprecated.layout.Region;
import net.specialattack.forge.core.client.gui.deprecated.layout.SGLayoutManager;
import net.specialattack.forge.core.client.gui.deprecated.style.StyleDefs;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

public class SGScrollPane extends SGComponent {

    private float scrollLeft, scrollTop;
    private boolean horizontal, vertical;
    private Inner innerPanel;
    private Color scrollbarBackground = StyleDefs.COLOR_SCROLLBAR_BACKGROUND;
    private Color scrollbarHoverForeground = StyleDefs.COLOR_SCROLLBAR_FOREGROUND_HOVER, scrollbarForeground = StyleDefs.COLOR_SCROLLBAR_FOREGROUND;
    private int scrollbarWidth = 8;
    private byte dragging = -1;

    public SGScrollPane() {
        this.innerPanel = new Inner();
        super.setLayoutManager(new BorderedSGLayoutManager());
        super.addChild(this.innerPanel, BorderedSGLayoutManager.Border.CENTER);
        //this.innerPanel.setParent(null);
    }

    public void setCanScroll(boolean horizontal, boolean vertical) {
        this.horizontal = horizontal;
        this.vertical = vertical;
    }

    public void setScrollbarSize(int size) {
        this.scrollbarWidth = size;
    }

    public Color getScrollbarBackground() {
        return this.scrollbarBackground;
    }

    public Color getScrollbarForeground() {
        return this.scrollbarForeground;
    }

    public Color getScrollbarHoverForeground() {
        return this.scrollbarHoverForeground;
    }

    public void setScrollbarColors(Color background, Color foreground, Color foregroundHover) {
        this.scrollbarBackground = background;
        this.scrollbarForeground = foreground;
        this.scrollbarHoverForeground = foregroundHover;
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks) {
        if (this.isErrored()) {
            SGUtils.drawErrorBox(this);
            return;
        }
        if (!this.isVisible()) {
            return;
        }
        int stack = 0;
        try {
            stack++;
            GLState.glPushMatrix();
            stack++;
            GLState.glPushMatrix();
            this.drawBackground(mouseX, mouseY, partialTicks);
            this.drawForeground(mouseX, mouseY, partialTicks);
            //GL11.glTranslatef(this.getLeft(SizeContext.INNER) - (int) this.scrollLeft, this.getTop(SizeContext.INNER) - (int) this.scrollTop, 0.0F);
            //SGUtils.drawErrorBox(this.innerPanel);
            GLState.glPopMatrix();
            stack--;
            if (DEBUG) {
                if (this.isMouseOver() || this.hasFocus()) {
                    int color = (this.hashCode() & 0xFFFFFF) | 0x88000000;
                    GLState.glDepthMask(false);
                    int left = this.getLeft(SizeContext.OUTLINE);
                    int top = this.getTop(SizeContext.OUTLINE);
                    int width = this.getWidth(SizeContext.OUTLINE);
                    int height = this.getHeight(SizeContext.OUTLINE);
                    if (this.isMouseOver()) {
                        GuiHelper.drawColoredRect(left, top, left + width, top + height, color, this.getZLevel());
                    }
                    if (this.hasFocus()) {
                        SGUtils.drawBox(left, top, width, height, this.getZLevel(), color);
                    }
                    GLState.glDepthMask(true);
                }
            }
            SGUtils.clipComponent(this.innerPanel);
            int left = this.getLeft(SizeContext.INNER) - (int) this.scrollLeft;
            int top = this.getTop(SizeContext.INNER) - (int) this.scrollTop;
            GL11.glTranslatef(left, top, this.getZLevel());
            List<SGComponent> children = this.getChildren();
            int borders = this.getBorderWidth() + this.getOutlineWidth();
            Region region = new Region((int) this.scrollLeft, (int) this.scrollTop, this.getWidth(SizeContext.INNER), this.getHeight(SizeContext.INNER));
            if (children != null) {
                for (SGComponent component : children) {
                    if (component.getDimensions().intersects(region)) {
                        component.draw(mouseX - left, mouseY - top, partialTicks);
                    }
                }
            }
            GLState.glPopMatrix();
            stack--;
        } catch (Exception e) {
            e.printStackTrace();
            this.setErrored();
            while (stack > 0) {
                GLState.glPopMatrix();
                stack--;
            }
        } finally {
            try {
                SGUtils.endClip();
            } catch (Exception e) {
                e.printStackTrace();
                this.setErrored();
            }
        }
    }

    @Override
    public void drawBackground(int mouseX, int mouseY, float partialTicks) {
        super.drawBackground(mouseX, mouseY, partialTicks);
        if (this.horizontal || this.vertical) {
            GLState.glPushMatrix();
            GL11.glTranslatef(this.getLeft(SizeContext.INNER), this.getTop(SizeContext.INNER), this.getZLevel());
            if (this.vertical) {
                int superWidth = super.getWidth(SizeContext.INNER);
                // We use super height in case we can scroll both horizontally and vertically vv
                GuiHelper.drawColoredRect(superWidth - this.scrollbarWidth, 0, superWidth, super.getHeight(SizeContext.INNER), this.getScrollbarBackground().colorHex, 0.0F);
            }
            if (this.horizontal) {
                int superHeight = super.getHeight(SizeContext.INNER);
                // Here we don't do super width to prevent overlap               vv
                GuiHelper.drawColoredRect(0, superHeight - this.scrollbarWidth, this.getWidth(SizeContext.INNER), superHeight, this.getScrollbarBackground().colorHex, 0.0F);
            }
            GLState.glPopMatrix();
        }
    }

    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTicks) {
        super.drawForeground(mouseX, mouseY, partialTicks);
        if (this.horizontal || this.vertical) {
            GLState.glPushMatrix();
            GL11.glTranslatef(this.getLeft(SizeContext.INNER), this.getTop(SizeContext.INNER), this.getZLevel());
            int width = this.getWidth(SizeContext.INNER);
            int height = this.getHeight(SizeContext.INNER);
            int left = this.getLeft(SizeContext.INNER);
            int top = this.getTop(SizeContext.INNER);
            boolean hover = this.isMouseOver();
            if (this.vertical) {
                int superWidth = super.getWidth(SizeContext.INNER);
                int innerHeight = this.getHeight(SizeContext.INNER);
                int innerOuterHeight = this.innerPanel.getHeight(SizeContext.OUTLINE);
                Color color = this.getScrollbarForeground();
                if (this.dragging == 1 || (hover && mouseX - left >= width && mouseX - left < width + this.scrollbarWidth && mouseY - top >= 0 && mouseY - top < height)) {
                    color = this.getScrollbarHoverForeground();
                }
                if (innerOuterHeight > innerHeight) {
                    int thumbHeight = GuiHelper.getScaled(innerHeight, innerHeight, innerOuterHeight);
                    int position = GuiHelper.getScaled(innerHeight - thumbHeight, (int) this.scrollTop, innerOuterHeight - innerHeight);
                    GuiHelper.drawColoredRect(superWidth - this.scrollbarWidth, position, superWidth, thumbHeight + position, color.colorHex, 0.0F);
                } else {
                    GuiHelper.drawColoredRect(superWidth - this.scrollbarWidth, 0, superWidth, innerHeight, color.colorHex, 0.0F);
                }
            }
            if (this.horizontal) {
                int superHeight = super.getHeight(SizeContext.INNER);
                int innerWidth = this.getWidth(SizeContext.INNER);
                int innerInnerWidth = this.innerPanel.getWidth(SizeContext.OUTLINE);
                Color color = this.getScrollbarForeground();
                if (this.dragging == 2 || (hover && mouseY - top >= height && mouseY - top < height + this.scrollbarWidth && mouseX - left >= 0 && mouseX - left < width)) {
                    color = this.getScrollbarHoverForeground();
                }
                if (innerInnerWidth > innerWidth) {
                    int thumbWidth = GuiHelper.getScaled(innerWidth, innerWidth, innerInnerWidth);
                    int position = GuiHelper.getScaled(innerWidth - thumbWidth, (int) this.scrollLeft, innerInnerWidth - innerWidth);
                    GuiHelper.drawColoredRect(position, superHeight - this.scrollbarWidth, thumbWidth + position, superHeight, color.colorHex, 0.0F);
                } else {
                    GuiHelper.drawColoredRect(0, superHeight - this.scrollbarWidth, innerWidth, superHeight, color.colorHex, 0.0F);
                }
            }
            GLState.glPopMatrix();
        }
    }

    @Override
    public Pair<SGComponent, Location> cascadeMouse(int mouseX, int mouseY) {
        if (!this.isVisible()) {
            return null;
        }
        int left = this.getLeft(SizeContext.INNER);
        int top = this.getTop(SizeContext.INNER);
        int borders = this.getBorderWidth() + this.getOutlineWidth();
        if (this.isErrored() && this.isMouseOver(mouseX, mouseY)) {
            return ImmutablePair.of((SGComponent) this, new Location(mouseX - left, mouseY - top));
        }
        try {
            if (!this.isMouseOver(mouseX, mouseY)) {
                return null;
            }
            int width = this.getWidth(SizeContext.INNER);
            int height = this.getHeight(SizeContext.INNER);
            //if (this.vertical) {
            if (mouseX - left >= width) {
                return ImmutablePair.of((SGComponent) this, new Location(mouseX - left, mouseY - top));
            }
            //}
            //if (this.horizontal) {
            if (mouseY - top >= height) {
                return ImmutablePair.of((SGComponent) this, new Location(mouseX - left, mouseY - top));
            }
            //}
            if (mouseX - left < 0 || mouseY - top < 0 || mouseX - left > width || mouseY - top > height) {
                return ImmutablePair.of((SGComponent) this, new Location(mouseX - left, mouseY - top));
            }
            List<SGComponent> children = this.getRawReverseChildren();
            if (children != null) {
                for (SGComponent component : children) {
                    Pair<SGComponent, Location> over = component.cascadeMouse(mouseX - left + (int) this.scrollLeft, mouseY - top + (int) this.scrollTop);
                    if (over != null) {
                        return over;
                    }
                }
            }
            return ImmutablePair.of((SGComponent) this, new Location(mouseX - left, mouseY - top));
        } catch (Exception e) {
            e.printStackTrace();
            this.setErrored();
        }
        return null;
    }

    @Override
    public boolean onScroll(int mouseX, int mouseY, int scroll) {
        if (!this.isVisible() || this.isErrored()) {
            return false;
        }
        try {
            List<SGComponent> children = this.getChildren();
            if (children != null) {
                for (SGComponent component : children) {
                    int left = this.getLeft(SizeContext.INNER);
                    int top = this.getTop(SizeContext.INNER);
                    if (component.isMouseOver(mouseX - left, mouseY - top)) {
                        boolean scrolled = component.onScroll(mouseX - left - (int) this.scrollLeft, mouseY - top - (int) this.scrollTop, scroll);
                        if (scrolled) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.setErrored();
        }
        float amount = scroll;
        if (!GuiScreen.isCtrlKeyDown()) {
            amount /= 10;
        }
        if (this.vertical && this.horizontal) {
            if (GuiScreen.isShiftKeyDown()) {
                this.scrollLeft = MathHelper.clamp_float(this.scrollLeft - amount, 0, Math.max(0, this.innerPanel.getWidth(SizeContext.OUTLINE) - this.getWidth(SizeContext.INNER)));
            } else {
                this.scrollTop = MathHelper.clamp_float(this.scrollTop - amount, 0, Math.max(0, this.innerPanel.getHeight(SizeContext.OUTLINE) - this.getHeight(SizeContext.INNER)));
            }
        } else if (this.vertical) {
            this.scrollTop = MathHelper.clamp_float(this.scrollTop - amount, 0, Math.max(0, this.innerPanel.getHeight(SizeContext.OUTLINE) - this.getHeight(SizeContext.INNER)));
        } else if (this.horizontal) {
            this.scrollLeft = MathHelper.clamp_float(this.scrollLeft - amount, 0, Math.max(0, this.innerPanel.getWidth(SizeContext.OUTLINE) - this.getWidth(SizeContext.INNER)));
        }
        return false;
    }

    @Override
    public void onMouseDown(int mouseX, int mouseY, int button) {
        super.onMouseDown(mouseX, mouseY, button);
        if (button == 0) {
            int width = this.getWidth(SizeContext.INNER);
            int height = this.getHeight(SizeContext.INNER);
            if (this.vertical) {
                if (mouseX >= width && mouseX < width + this.scrollbarWidth && mouseY >= 0 && mouseY < height) {
                    this.dragging = 1;
                }
            }
            if (this.horizontal) {
                if (mouseY >= height && mouseY < height + this.scrollbarWidth && mouseX >= 0 && mouseX < width) {
                    this.dragging = 2;
                }
            }
        }
    }

    @Override
    public void onMouseUp(int mouseX, int mouseY, int button) {
        super.onMouseUp(mouseX, mouseY, button);
        this.dragging = 0;
    }

    @Override
    public void onMouseDrag(int oldX, int oldY, int newX, int newY, int button, long pressTime) {
        super.onMouseDrag(oldX, oldY, newX, newY, button, pressTime);
        if (this.dragging == 1) { // Vertical
            int innerHeight = this.getHeight(SizeContext.INNER);
            int outerHeight = this.innerPanel.getHeight(SizeContext.OUTLINE);
            int thumbHeight = GuiHelper.getScaled(innerHeight, innerHeight, outerHeight);
            float scrollHeight = outerHeight - innerHeight; // Floats for precision
            float scaledHeight = innerHeight - thumbHeight;
            float position = GuiHelper.getScaled(scaledHeight, this.scrollTop, scrollHeight);
            this.scrollTop = GuiHelper.getScaled(scrollHeight, position + newY - oldY, scaledHeight);
        } else if (this.dragging == 2) { // Horizontal
            int innerWidth = this.getWidth(SizeContext.INNER);
            int outerWidth = this.innerPanel.getWidth(SizeContext.OUTLINE);
            int thumbWidth = GuiHelper.getScaled(innerWidth, innerWidth, outerWidth);
            float scrollWidth = outerWidth - innerWidth; // Floats for precision
            float scaledWidth = innerWidth - thumbWidth;
            float position = GuiHelper.getScaled(scaledWidth, this.scrollLeft, scrollWidth);
            this.scrollLeft = GuiHelper.getScaled(scrollWidth, position + newX - oldX, scaledWidth);
        }
    }

    @Override
    public void addChild(SGComponent child, Object param) {
        this.innerPanel.addChild(child, param);
    }

    @Override
    public void removeChild(SGComponent child) {
        this.innerPanel.removeChild(child);
        this.getRoot().updateLayout();
    }

    @Override
    public void setLayoutManager(SGLayoutManager layoutManager) {
        this.innerPanel.setLayoutManager(layoutManager);
    }

    @Override
    public List<SGComponent> getChildren() {
        return this.innerPanel.getChildren();
    }

    @Override
    public int getWidth(SizeContext context) {
        if (this.vertical && context == SizeContext.INNER) {
            return super.getWidth(context) - this.scrollbarWidth;
        }
        return super.getWidth(context);
    }

    @Override
    public int getHeight(SizeContext context) {
        if (this.horizontal && context == SizeContext.INNER) {
            return super.getHeight(context) - this.scrollbarWidth;
        }
        return super.getHeight(context);
    }

    @Override
    public void setDimensions(int left, int top, int width, int height) {
        //int borders = (this.getBorderWidth() + this.getOutlineWidth()) * 2;
        //int innerWidth = width - borders - (this.vertical ? this.scrollbarWidth : 0);
        //int innerHeight = height - borders - (this.horizontal ? this.scrollbarWidth : 0);
        //this.innerPanel.setPreferredInnerSize(innerWidth, innerHeight);
        //this.innerPanel.setPreferredInnerSize(innerWidth - (this.vertical ? this.scrollbarWidth : 0), innerHeight - (this.horizontal ? this.scrollbarWidth : 0));
        super.setDimensions(left, top, width, height);
        //this.innerPanel.updateLayout();
        //this.innerPanel.setPreferredInnerSize(this.getWidth(SizeContext.INNER), this.getHeight(SizeContext.INNER));
        //this.updateLayout();
    }

    @Override
    public void updateLayout() {
        super.updateLayout();
        int borders = (this.getBorderWidth() + this.getOutlineWidth()) * 2;
        int horiz = borders + (this.vertical ? this.scrollbarWidth : 0);
        int vert = borders + (this.horizontal ? this.scrollbarWidth : 0);
        int width, height;
        Region predicted = this.innerPanel.predictSize();
        if (this.vertical) {
            height = Math.max(this.getHeight(SizeContext.INNER), predicted.getHeight());
        } else {
            height = this.getHeight(SizeContext.INNER);
        }
        if (this.horizontal) {
            width = Math.max(this.getWidth(SizeContext.INNER), predicted.getWidth());
        } else {
            width = this.getWidth(SizeContext.INNER);
        }
        this.innerPanel.setDimensions(0, 0, width, height);
        //this.innerPanel.setDimensions(this.innerPanel.predictSize().expanded(-horiz, -vert));
        if (this.vertical) {
            this.scrollTop = MathHelper.clamp_float(this.scrollTop, 0, Math.max(0, this.innerPanel.getHeight(SizeContext.OUTLINE) - this.getHeight(SizeContext.INNER)));
        } else if (this.horizontal) {
            this.scrollLeft = MathHelper.clamp_float(this.scrollLeft, 0, Math.max(0, this.innerPanel.getWidth(SizeContext.OUTLINE) - this.getWidth(SizeContext.INNER)));
        }
    }

    @Override
    public Region predictSize() {
        int borders = (this.getBorderWidth() + this.getOutlineWidth()) * 2;
        Region predicted = super.predictSize().expanded(borders + (this.vertical ? this.scrollbarWidth : 0), borders + (this.horizontal ? this.scrollbarWidth : 0));
        int preferredWidth = this.getPreferredWidth();
        int preferredHeight = this.getPreferredHeight();
        int width = preferredWidth <= 0 ? predicted.getWidth() : Math.min(preferredWidth, predicted.getWidth());
        int height = preferredHeight <= 0 ? predicted.getHeight() : Math.min(preferredHeight, predicted.getHeight());

        return new Region(0, 0, width, height);
        //return super.predictSize();//.expanded(this.vertical ? this.scrollbarWidth : 0, this.horizontal ? this.scrollbarWidth : 0);
    }

    private class Inner extends SGPanel {

        @Override
        public void setSizeRestrictions(int width, int height) {
            // There is no limit to our love
        }

        @Override
        public Region getRenderingRegion() {
            int borders = SGScrollPane.this.getBorderWidth() + this.getOutlineWidth();
            Region parentRegion = SGScrollPane.this.getRenderingRegion();
            //return super.getRenderingRegion().offset(parentRegion.left, parentRegion.top);
            int left = parentRegion.getLeft() + borders;
            int top = parentRegion.getTop() + borders;
            int width = SGScrollPane.this.getWidth(SizeContext.INNER);
            int height = SGScrollPane.this.getHeight(SizeContext.INNER);
            return new Region(left, top, width, height);
        }

        @Override
        public Location getChildOffset() {
            return new Location((int) SGScrollPane.this.scrollLeft, (int) SGScrollPane.this.scrollTop);
        }

        @Override
        public Region predictSize() {
            //int borders = (SGScrollPane.this.getBorderWidth() + SGScrollPane.this.getOutlineWidth());
            return super.predictSize();//.expanded(borders * 2 - 1, borders * 2 - 1);
        }

        @Override
        public void updateLayout() {
            super.updateLayout();
        }

        @Override
        public void draw(int mouseX, int mouseY, float partialTicks) {
        }

        @Override
        public IComponentHolder getRoot() {
            return SGScrollPane.this.getRoot();
        }
    }

}

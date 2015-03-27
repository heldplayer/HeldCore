package net.specialattack.forge.core.client.gui.element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.gui.FontRenderer;
import net.specialattack.forge.core.client.MC;
import net.specialattack.forge.core.client.gui.GuiHelper;
import net.specialattack.forge.core.client.gui.MouseHandler;
import net.specialattack.forge.core.client.gui.SGUtils;
import net.specialattack.forge.core.client.gui.SizeContext;
import net.specialattack.forge.core.client.gui.layout.Location;
import net.specialattack.forge.core.client.gui.layout.Region;
import net.specialattack.forge.core.client.gui.layout.SGLayoutManager;
import net.specialattack.forge.core.client.gui.style.background.IBackground;
import net.specialattack.forge.core.client.gui.style.border.IBorder;
import net.specialattack.forge.core.client.gui.style.outline.IOutline;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

public class SGComponent implements IComponentHolder {

    public static boolean DEBUG = false;

    public FontRenderer font = MC.getFontRenderer();
    private int preferredWidth, preferredHeight;
    private int left, top, width, height;
    private float zLevel;
    private int limitWidth = Integer.MAX_VALUE, limitHeight = Integer.MAX_VALUE;
    private IComponentHolder parent;
    private List<SGComponent> children;
    private List<SGComponent> accessChildren;
    private SGLayoutManager layoutManager;
    private IBackground background;
    private IOutline outline;
    private IBorder border;
    private boolean mouseOver, focus;
    private boolean error;
    private boolean visible = true;
    private boolean forceSize;
    private MouseHandler mouseHandler;

    public void setPreferredSize(int width, int height) {
        this.preferredWidth = width;
        this.preferredHeight = height;
    }

    public int getPreferredWidth() {
        return this.preferredWidth + this.getBorderWidth() * 2 + this.getOutlineWidth() * 2;
    }

    public int getPreferredHeight() {
        return this.preferredHeight + this.getBorderWidth() * 2 + this.getOutlineWidth() * 2;
    }

    public boolean forcePreferredSize() {
        return this.forceSize;
    }

    public void setShouldForceSize(boolean flag) {
        this.forceSize = flag;
    }

    public int getLeft(SizeContext context) {
        int result = this.left;
        switch (context) {
            case INNER:
                result += this.getBorderWidth();
            case BORDER:
                result += this.getOutlineWidth();
        }
        return result;
    }

    public int getTop(SizeContext context) {
        int result = this.top;
        switch (context) {
            case INNER:
                result += this.getBorderWidth();
            case BORDER:
                result += this.getOutlineWidth();
        }
        return result;
    }

    public int getWidth(SizeContext context) {
        int result = this.width;
        switch (context) {
            case INNER:
                result -= this.getBorderWidth() * 2;
            case BORDER:
                result -= this.getOutlineWidth() * 2;
        }
        return result;
    }

    public int getHeight(SizeContext context) {
        int result = this.height;
        switch (context) {
            case INNER:
                result -= this.getBorderWidth() * 2;
            case BORDER:
                result -= this.getOutlineWidth() * 2;
        }
        return result;
    }

    public float getZLevel() {
        return this.zLevel;
    }

    public int getBorderWidth() {
        return this.border != null ? this.border.getSize() : 0;
    }

    public int getOutlineWidth() {
        return this.outline != null ? this.outline.getSize() : 0;
    }

    public void setDimensions(Region region) {
        this.setDimensions(region.left, region.top, region.width, region.height);
    }

    public void setDimensions(int left, int top, int width, int height) {
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
        this.updateLayout();
    }

    public void setSizeRestrictions(int width, int height) {
        this.limitWidth = width;
        this.limitHeight = height;
    }

    public int getWidthLimit() {
        return this.limitWidth;
    }

    public int getHeightLimit() {
        return this.limitHeight;
    }

    public void setBackground(IBackground background) {
        this.background = background;
    }

    public IBackground getBackground() {
        return this.background;
    }

    public void setBorder(IBorder border) {
        this.border = border;
    }

    public void setOutline(IOutline outline) {
        this.outline = outline;
    }

    public void setLayoutManager(SGLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @Override
    public void updateLayout() {
        if (this.isErrored()) {
            return;
        }
        if (this.layoutManager != null) {
            this.layoutManager.performLayout(this);
        } else {
            if (this.children != null) {
                for (SGComponent component : this.children) {
                    component.setDimensions(0, 0, component.getPreferredWidth(), component.getPreferredHeight());
                }
            }
        }
        if (this.children != null) {
            for (SGComponent component : this.children) {
                //component.updateLayout();
            }
        }
    }

    public Region predictSize() {
        if (this.layoutManager != null) {
            return this.layoutManager.predictSize(this);
        } else {
            return new Region(this.left, this.top, this.width, this.height);
        }
    }

    public Region getDimensions() {
        return new Region(this.left, this.top, this.width, this.height);
    }

    public void addChild(SGComponent child) {
        this.addChild(child, null);
    }

    public void addChild(SGComponent child, Object param) {
        if (this.layoutManager != null) {
            this.layoutManager.addComponent(child, param);
        }
        if (child != null) {
            if (child.isComponentIn(this)) {
                throw new RuntimeException(String.format("Cannot make %s a child of %s", child, this));
            }
            if (this.children == null) {
                this.children = new ArrayList<SGComponent>();
                this.accessChildren = Collections.unmodifiableList(this.children);
            }
            child.parent = this;
            if (!this.children.contains(child)) {
                this.children.add(child);
            }
        }
    }

    public void removeChild(SGComponent child) {
        if (child != null) {
            this.children.remove(child);
            if (this.layoutManager != null) {
                this.layoutManager.removeComponent(child);
            }
        }
    }

    public void setParent(IComponentHolder holder) {
        this.parent = holder;
    }

    public IComponentHolder getParent() {
        return parent;
    }

    /**
     * @return A list containing the children of this component, or null if there are none.
     */
    public List<SGComponent> getChildren() {
        return this.accessChildren;
    }

    public Pair<SGComponent, Location> cascadeMouse(int mouseX, int mouseY) {
        if (!this.isVisible()) {
            return null;
        }
        if (this.isErrored() && this.isMouseOver(mouseX, mouseY)) {
            return ImmutablePair.of(this, new Location(mouseX - this.getLeft(SizeContext.INNER), mouseY - this.getTop(SizeContext.INNER)));
        }
        try {
            if (this.children != null) {
                for (SGComponent component : this.children) {
                    Pair<SGComponent, Location> over = component.cascadeMouse(mouseX - this.getLeft(SizeContext.INNER), mouseY - this.getTop(SizeContext.INNER));
                    if (over != null) {
                        return over;
                    }
                }
            }
            if (this.isMouseOver(mouseX, mouseY)) {
                return ImmutablePair.of(this, new Location(mouseX - this.getLeft(SizeContext.INNER), mouseY - this.getTop(SizeContext.INNER)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.setErrored();
        }
        return null;
    }

    public void setMouseOver(boolean value) {
        this.mouseOver = value;
    }

    public boolean isMouseOver() {
        return this.mouseOver;
    }

    public void setHasFocus(boolean focus) {
        this.focus = focus;
    }

    public boolean hasFocus() {
        return this.focus;
    }

    public void setErrored() {
        this.error = true;
    }

    public boolean isErrored() {
        return this.error;
    }

    public void draw(int mouseX, int mouseY, float partialTicks) {
        if (this.isErrored()) {
            SGUtils.drawErrorBox(this);
            return;
        }
        if (!this.isVisible()) {
            return;
        }
        int stack = 0;
        //SGUtils.clipComponent(this);
        try {
            stack++;
            GL11.glPushMatrix();
            stack++;
            GL11.glPushMatrix();
            this.drawBackground(mouseX, mouseY, partialTicks);
            this.drawForeground(mouseX, mouseY, partialTicks);
            GL11.glPopMatrix();
            stack--;
            if (SGComponent.DEBUG) {
                if (this.mouseOver || this.focus) {
                    int color = (this.hashCode() & 0xFFFFFF) | 0x88000000;
                    GL11.glDepthMask(false);
                    if (this.mouseOver) {
                        GuiHelper.drawColoredRect(this.getLeft(SizeContext.OUTLINE), this.getTop(SizeContext.OUTLINE), this.getLeft(SizeContext.OUTLINE) + this.getWidth(SizeContext.OUTLINE), this.getTop(SizeContext.OUTLINE) + this.getHeight(SizeContext.OUTLINE), color, this.getZLevel());
                    }
                    if (this.focus) {
                        SGUtils.drawBox(this.getLeft(SizeContext.OUTLINE), this.getTop(SizeContext.OUTLINE), this.getWidth(SizeContext.OUTLINE), this.getHeight(SizeContext.OUTLINE), this.getZLevel(), color);
                    }
                    GL11.glDepthMask(true);
                }
            }

            int left = this.getLeft(SizeContext.INNER);
            int top = this.getTop(SizeContext.INNER);
            GL11.glTranslatef(left, top, this.getZLevel());
            //this.drawHook(mouseX - left, mouseY - top, partialTicks);
            if (this.children != null) {
                for (SGComponent component : this.children) {
                    component.draw(mouseX - left, mouseY - top, partialTicks);
                }
            }
            //if (this instanceof SGMenu) {
            //Region predicted = this.predictSize();
            //int pos = this.height / 3;
            //GL11.glTranslatef(0, 0, 100);
            //this.font.drawStringWithShadow(String.format("%dx%d", this.getPreferredWidth(), this.getPreferredHeight()), 0, pos, (this.hashCode() & 0xFFFFFF) | 0xFF000000);
            //this.font.drawStringWithShadow(String.format("%dx%d", predicted.width, predicted.height), 0, pos + 10, (this.hashCode() & 0xFFFFFF) | 0xFF000000);
            //this.font.drawStringWithShadow(String.format("%dx%d", this.width, this.height), 0, pos + 20, (this.hashCode() & 0xFFFFFF) | 0xFF000000);
            //this.font.drawStringWithShadow(String.format("%dx%d", this.left, this.top), 0, pos + 30, (this.hashCode() & 0xFFFFFF) | 0xFF000000);
            //}
            GL11.glPopMatrix();
            stack--;
        } catch (Exception e) {
            e.printStackTrace();
            this.setErrored();
            while (stack > 0) {
                GL11.glPopMatrix();
                stack--;
            }
        }
        //SGUtils.endClip();
    }

    public void drawForeground(int mouseX, int mouseY, float partialTicks) {
    }

    public void drawBackground(int mouseX, int mouseY, float partialTicks) {
        IBackground background = this.getBackground();
        if (background != null) {
            background.drawBackground(this);
        }
        if (this.border != null) {
            this.border.drawBorder(this);
        }
        if (this.outline != null) {
            this.outline.drawOutline(this);
        }
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        if (!this.isVisible()) {
            return false;
        }
        int left = this.getLeft(SizeContext.BORDER);
        int top = this.getTop(SizeContext.BORDER);
        return mouseX >= left && mouseX < left + this.getWidth(SizeContext.BORDER) && mouseY >= top && mouseY < top + this.getHeight(SizeContext.BORDER);
    }

    public final void doClick(int mouseX, int mouseY, int button) {
        if (this.isErrored()) {
            return;
        }
        try {
            if (this.mouseHandler == null || !this.mouseHandler.onClick(mouseX, mouseY, button)) {
                this.onClick(mouseX, mouseY, button);
            }
            this.clickHappened(mouseX, mouseY, button);
        } catch (Exception e) {
            e.printStackTrace();
            this.setErrored();
        }
    }

    public void clickHappened(int mouseX, int mouseY, int button) {
    }

    public void onMouseDown(int mouseX, int mouseY, int button) {
        if (this.mouseHandler != null) {
            this.mouseHandler.onMouseDown(mouseX, mouseY, button);
        }
    }

    public void onMouseUp(int mouseX, int mouseY, int button) {
        if (this.mouseHandler != null) {
            this.mouseHandler.onMouseUp(mouseX, mouseY, button);
        }
    }

    public void onMouseDrag(int oldX, int oldY, int newX, int newY, int button, long pressTime) {
        if (this.mouseHandler != null) {
            this.mouseHandler.onMouseDrag(oldX, oldY, newX, newY, button, pressTime);
        }
    }

    public void onClick(int mouseX, int mouseY, int button) {
        this.focusChangeUp(null);
    }

    public boolean onScroll(int mouseX, int mouseY, int scroll) {
        if (!this.isVisible() || this.isErrored()) {
            return false;
        }
        try {
            if (this.children != null) {
                for (SGComponent component : this.children) {
                    if (component.isMouseOver(mouseX - this.getLeft(SizeContext.INNER), mouseY - this.getTop(SizeContext.INNER))) {
                        boolean scrolled = component.onScroll(mouseX - this.getLeft(SizeContext.INNER), mouseY - this.getTop(SizeContext.INNER), scroll);
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
        return false;
    }

    public final boolean doKey(char character, int keycode) {
        if (this.isErrored()) {
            return false;
        }
        try {
            return this.onKey(character, keycode);
        } catch (Exception e) {
            e.printStackTrace();
            this.setErrored();
            return false;
        }
    }

    public boolean onKey(char character, int keycode) {
        return false;
    }

    public void setMouseHandler(MouseHandler handler) {
        this.mouseHandler = handler;
    }

    @Override
    public void focusChangeUp(SGComponent component) {
        IComponentHolder parent = this.getParent();
        if (parent != null) {
            parent.focusChangeUp(component);
        } else {
            this.focusChangeDown(component);
        }
    }

    @Override
    public void focusChangeDown(SGComponent component) {
        if (component == this) {
            this.focus = true;
        }
        if (this.children != null) {
            for (SGComponent child : this.children) {
                child.focusChangeDown(component);
            }
        }
    }

    @Override
    public void elementClicked(SGComponent component, int button) {
        if (this.children != null) {
            for (SGComponent child : this.children) {
                child.elementClicked(component, button);
            }
        }
    }

    public boolean isComponentIn(SGComponent component) {
        if (this == component) {
            return true;
        }
        if (this.children != null) {
            for (SGComponent child : this.children) {
                if (child == component || child.isComponentIn(component)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void updateTick() {
        if (this.children != null) {
            for (SGComponent child : this.children) {
                child.updateTick();
            }
        }
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public IComponentHolder getRoot() {
        IComponentHolder parent = this.getParent();
        if (parent != null) {
            if (parent instanceof SGComponent) {
                return ((SGComponent) parent).getRoot();
            }
            return parent;
        }
        return this;
    }

    @Override
    public Region getRenderingRegion() {
        IComponentHolder parent = this.getParent();
        if (parent != null) {
            Region parentRegion = parent.getRenderingRegion();
            return this.getDimensions().offset(parentRegion.left, parentRegion.top);
        }
        return this.getDimensions();
    }

    @Override
    public int getMouseState() {
        IComponentHolder parent = this.getParent();
        if (parent != null) {
            return parent.getMouseState();
        }
        return -1;
    }

    @Override
    public void addPopout(SGComponent component) {
        IComponentHolder parent = this.getParent();
        if (parent != null) {
            parent.addPopout(component);
        }
    }

    @Override
    public void removePopout(SGComponent component) {
        IComponentHolder parent = this.getParent();
        if (parent != null) {
            parent.removePopout(component);
        }
    }
}
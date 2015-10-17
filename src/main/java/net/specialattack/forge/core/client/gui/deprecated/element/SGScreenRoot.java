package net.specialattack.forge.core.client.gui.deprecated.element;

import com.google.common.collect.Lists;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.specialattack.forge.core.client.GLState;
import net.specialattack.forge.core.client.gui.deprecated.SGUtils;
import net.specialattack.forge.core.client.gui.deprecated.layout.BorderedSGLayoutManager;
import net.specialattack.forge.core.client.gui.deprecated.layout.Location;
import net.specialattack.forge.core.client.gui.deprecated.layout.Region;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class SGScreenRoot extends GuiScreen implements IComponentHolder {

    private final SGComponent outerRoot;
    private SGComponent innerRoot;
    private SGMenuBar menu;
    public SGComponent hover;
    public SGComponent focus;
    public SGComponent clicked;
    private Location outDragStartLoc, inDragStartLoc, inDragPrevLoc;
    private boolean mouseDown;
    private LinkedList<SGComponent> popouts;
    private List<SGComponent> reversedPopouts;

    public SGScreenRoot(SGComponent root) {
        this.outerRoot = new SGPanel();
        this.outerRoot.setParent(this);
        this.outerRoot.setLayoutManager(new BorderedSGLayoutManager());
        this.setRoot(root);
    }

    public SGScreenRoot() {
        this(new SGPanel());
    }

    public SGComponent getInnerRoot() {
        return this.innerRoot;
    }

    public void setRoot(SGComponent root) {
        this.outerRoot.removeChild(this.innerRoot);
        this.outerRoot.addChild(root, BorderedSGLayoutManager.Border.CENTER);
        this.innerRoot = root;
    }

    public void setMenu(SGMenuBar menu) {
        this.outerRoot.removeChild(this.menu);
        this.outerRoot.addChild(menu, BorderedSGLayoutManager.Border.TOP);
        this.menu = menu;
    }

    @Override
    public void initGui() {
        super.initGui();
        Keyboard.enableRepeatEvents(true);
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height) {
        super.setWorldAndResolution(mc, width, height);
        this.outerRoot.setDimensions(0, 0, width, height);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // super.drawScreen(mouseX, mouseY, partialTicks);
        GLState.glEnable(GL11.GL_DEPTH_TEST);
        boolean hoverDone = false;
        if (this.reversedPopouts != null) {
            if (this.eventButton == -1) {
                for (SGComponent popout : this.reversedPopouts) {
                    Pair<SGComponent, Location> hover = popout.cascadeMouse(mouseX, mouseY);

                    SGComponent component = hover == null ? null : hover.getLeft();
                    if (component != null) {
                        if (component != this.hover) {
                            if (this.hover != null) {
                                this.hover.setMouseOver(false);
                            }
                            component.setMouseOver(true);
                            this.hover = component;
                        }
                        hoverDone = true;
                        break;
                    }
                }
            }
        }
        if (this.eventButton == -1 && !hoverDone) {
            Pair<SGComponent, Location> hover = this.outerRoot.cascadeMouse(mouseX, mouseY);

            SGComponent component = hover == null ? null : hover.getLeft();
            if (component != this.hover) {
                if (this.hover != null) {
                    this.hover.setMouseOver(false);
                }
                if (component == this.outerRoot) {
                    component = null;
                }
                if (component != null) {
                    component.setMouseOver(true);
                }
                this.hover = component;
            }
        }
        this.outerRoot.draw(mouseX, mouseY, partialTicks);
        SGUtils.endAllClips();

        if (this.popouts != null) {
            for (SGComponent popout : this.popouts) {
                //SGUtils.drawErrorBox(popout);
                popout.draw(mouseX, mouseY, partialTicks);
                SGUtils.endAllClips();
            }
        }
        //GuiHelper.drawColoredRect(mouseX, mouseY, mouseX + 1, mouseY + 1, 0xFFFFFFFF, 100);
        //if (this.hover != null) {
        //this.fontRendererObj.drawStringWithShadow(this.hover.getClass().toString(), 0, 10, 0xFFFFFFFF);
        //}
    }

    @Override
    public void handleMouseInput() {
        int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        int mouseButton = Mouse.getEventButton();

        if (Mouse.getEventButtonState()) { // Mouse button down
            if (this.mc.gameSettings.touchscreen && this.field_146298_h++ > 0) { // field_146298_h = pressTime
                return;
            }

            this.eventButton = mouseButton;
            this.lastMouseEvent = Minecraft.getSystemTime();
            this.mouseClicked(mouseX, mouseY, this.eventButton);
        } else if (mouseButton != -1) { // Mouse released
            if (this.mc.gameSettings.touchscreen && --this.field_146298_h > 0) {
                return;
            }

            this.eventButton = -1;
            this.mouseMovedOrUp(mouseX, mouseY, mouseButton);
        } else if (this.eventButton != -1 && this.lastMouseEvent > 0L) { // Mouse drag
            long downtime = Minecraft.getSystemTime() - this.lastMouseEvent;
            this.mouseClickMove(mouseX, mouseY, this.eventButton, downtime);
        }

        int scroll = Mouse.getEventDWheel();
        if (scroll != 0 && this.hover != null) {
            this.mouseScroll(mouseX, mouseY, scroll);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) { // Mouse down
        // TODO: Might do something about added buttons
        // super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.reversedPopouts != null) {
            for (SGComponent popout : this.reversedPopouts) {
                Pair<SGComponent, Location> clicked = popout.cascadeMouse(mouseX, mouseY);
                if (clicked != null) {
                    SGComponent component = clicked.getLeft();
                    Location location = clicked.getRight();
                    if (component != null && location != null) {
                        this.inDragPrevLoc = this.inDragStartLoc = location;
                        this.outDragStartLoc = new Location(mouseX, mouseY);
                        (this.clicked = component).onMouseDown(this.inDragStartLoc.left, this.inDragStartLoc.top, mouseButton);
                        return;
                    }
                }
            }
        }

        Pair<SGComponent, Location> clicked = this.outerRoot.cascadeMouse(mouseX, mouseY);

        if (clicked != null) {
            this.clicked = clicked.getLeft();
            this.inDragPrevLoc = this.inDragStartLoc = clicked.getRight();
            if (this.clicked != null && this.inDragStartLoc != null) {
                this.clicked.onMouseDown(this.inDragStartLoc.left, this.inDragStartLoc.top, mouseButton);
                this.outDragStartLoc = new Location(mouseX, mouseY);
            }
        } else {
            this.clicked = null;
        }
    }

    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int mouseButton) { // Mouse up
        // super.mouseMovedOrUp(mouseX, mouseY, mouseButton);
        if (this.clicked != null) {
            int newX = mouseX - this.outDragStartLoc.left + this.inDragStartLoc.left;
            int newY = mouseY - this.outDragStartLoc.top + this.inDragStartLoc.top;
            this.clicked.onMouseUp(newX, newY, mouseButton);

            if (this.reversedPopouts != null) {
                for (SGComponent popout : this.reversedPopouts) {
                    Pair<SGComponent, Location> clicked = popout.cascadeMouse(mouseX, mouseY);
                    if (clicked != null) {
                        SGComponent component = clicked.getLeft();
                        if (component == this.clicked) {
                            Location loc = clicked.getRight();
                            component.onMouseUp(loc.left, loc.top, mouseButton);
                            component.doClick(loc.left, loc.top, mouseButton);
                            this.outerRoot.elementClicked(component, mouseButton);
                            return;
                        }
                    }
                }
            }

            Pair<SGComponent, Location> clicked = this.outerRoot.cascadeMouse(mouseX, mouseY);

            if (clicked != null) {
                SGComponent component = clicked.getLeft();
                if (component == this.clicked) {
                    Location loc = clicked.getRight();
                    component.onMouseUp(loc.left, loc.top, mouseButton);
                    component.doClick(loc.left, loc.top, mouseButton);
                    this.outerRoot.elementClicked(component, mouseButton);
                }
            }
            this.clicked = null;
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int mouseButton, long pressTime) { // Mouse drag
        if (this.clicked != null) {
            int prevX = this.inDragPrevLoc.left;
            int prevY = this.inDragPrevLoc.top;
            int newX = mouseX - this.outDragStartLoc.left + this.inDragStartLoc.left;
            int newY = mouseY - this.outDragStartLoc.top + this.inDragStartLoc.top;
            this.clicked.onMouseDrag(prevX, prevY, newX, newY, mouseButton, pressTime);
            this.inDragPrevLoc = new Location(newX, newY);
        }
    }

    protected void mouseScroll(int mouseX, int mouseY, int amount) { // Mouse scroll
        if (this.reversedPopouts != null) {
            for (SGComponent popout : this.popouts) {
                Pair<SGComponent, Location> hover = popout.cascadeMouse(mouseX, mouseY);
                if (hover != null) {
                    SGComponent component = hover.getLeft();
                    Location location = hover.getRight();
                    if (component != null && location != null) {
                        popout.onScroll(mouseX, mouseY, amount);
                        return;
                    }
                }
            }
        }

        Pair<SGComponent, Location> hover = this.outerRoot.cascadeMouse(mouseX, mouseY);
        if (hover != null) {
            this.outerRoot.onScroll(mouseX, mouseY, amount);
        }
    }

    @Override
    protected void keyTyped(char character, int keycode) {
        if (this.focus == null || !this.focus.doKey(character, keycode)) {
            super.keyTyped(character, keycode);
        }
    }

    @Override
    public void updateScreen() {
        this.outerRoot.updateTick();
    }

    @Override
    public void focusChangeUp(SGComponent component) {
        if (this.focus != null) {
            this.focus.setHasFocus(false);
        }
        this.focus = component;
        if (component != null) {
            component.setHasFocus(true);
        }
        this.focusChangeDown(component);
    }

    @Override
    public void focusChangeDown(SGComponent component) {
        this.outerRoot.focusChangeDown(component);
    }

    @Override
    public void elementClicked(SGComponent component, int button) {
        this.outerRoot.elementClicked(component, button);
    }

    @Override
    public Region getRenderingRegion() {
        return new Region(0, 0, this.width, this.height);
    }

    @Override
    public Location getChildOffset() {
        return Location.ZERO;
    }

    @Override
    public void updateLayout() {
        this.outerRoot.updateLayout();
    }

    @Override
    public int getMouseState() {
        return this.eventButton;
    }

    @Override
    public void addPopout(SGComponent component) {
        if (this.popouts == null || this.reversedPopouts == null) {
            this.popouts = new LinkedList<SGComponent>();
            this.reversedPopouts = Lists.reverse(this.popouts);
        }
        this.popouts.add(component);
    }

    @Override
    public void removePopout(SGComponent component) {
        if (this.popouts != null) {
            this.popouts.remove(component);
        }
    }

    @Override
    public Region findPopoutRegion(boolean horizontal, Region around, Region size) {
        return SGUtils.findPopoutRegion(horizontal, around, size, this.width, this.height); // Delegate for reusability
    }
}

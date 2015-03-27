package net.specialattack.forge.core.client.gui;

/*
@SideOnly(Side.CLIENT)
public abstract class GuiBase extends GuiScreen implements IGuiElement {

    private int width, height;
    private int posX, posY;
    private float zLevel;
    private Positioning positioningX, positioningY;
    private IGuiElement parent;
    private List<IGuiElement> elements;
    private List<ITickListener> tickListeners;
    private IFocusableElement focusedElement;
    private boolean enabled, visible;

    public GuiBase(int posX, int posY, int width, int height, IGuiElement parent, float zLevel, Positioning positioningX, Positioning positioningY) {
        this.width = width;
        this.height = height;
        this.posX = posX;
        this.posY = posY;
        this.zLevel = zLevel;
        this.positioningX = positioningX == null ? Positioning.MIN_OFFSET : positioningX;
        this.positioningY = positioningY == null ? Positioning.MIN_OFFSET : positioningY;
        this.enabled = true;
        this.visible = true;
        this.parent = parent;
        this.tickListeners = new ArrayList<ITickListener>();
        this.elements = new ArrayList<IGuiElement>();
        if (parent != null) {
            parent.addChild(this);
        }
    }

    public GuiBase(int posX, int posY, int width, int height, IGuiElement parent, int posZ) {
        this(posX, posY, width, height, parent, posZ, Positioning.MIN_OFFSET, Positioning.MIN_OFFSET);
    }

    public GuiBase(int posX, int posY, int width, int height, IGuiElement parent) {
        this(posX, posY, width, height, parent, 0, Positioning.MIN_OFFSET, Positioning.MIN_OFFSET);
    }

    public GuiBase(int posX, int posY, int width, int height) {
        this(posX, posY, width, height, null, 0, Positioning.MIN_OFFSET, Positioning.MIN_OFFSET);
    }

    public GuiBase() {
        this(0, 0, 0, 0, null, 0, Positioning.MIN_OFFSET, Positioning.MIN_OFFSET);
    }

    @Override
    public void setWorldAndResolution(Minecraft minecraft, int width, int height) {
        super.setWorldAndResolution(minecraft, width, height);
        if (this instanceof IResizableGuiElement) {
            ((IResizableGuiElement) this).setSize(width, height);
        } else {
            this.width = width;
            this.height = height;
        }
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public int getPosX() {
        if (this.getParent() != null) {
            return this.positioningX.position(this.posX, this.getWidth(), this.getParent().getWidth());
        } else {
            return this.posX;
        }
    }

    @Override
    public int getPosY() {
        if (this.getParent() != null) {
            return this.positioningY.position(this.posY, this.getHeight(), this.getParent().getHeight());
        } else {
            return this.posY;
        }
    }

    @Override
    public float getZLevel() {
        if (this.getParent() != null) {
            return this.zLevel + this.getParent().getZLevel();
        } else {
            return this.zLevel;
        }
    }

    @Override
    public IGuiElement getParent() {
        return this.parent;
    }

    @Override
    public List<IGuiElement> getChildElements() {
        return this.elements;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public boolean isVisible() {
        return this.visible;
    }

    @Override
    public void addChild(IGuiElement element) {
        if (!this.elements.contains(element)) {
            this.elements.add(element);
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public final void draw(float partialTicks) {
        if (!this.visible) {
            return;
        }
        GL11.glTranslatef(this.getPosX(), this.getPosY(), this.getZLevel());
        GL11.glTranslatef((float) this.getWidth() / 2.0F, (float) this.getHeight() / 2.0F, 0.0F);
        if (this instanceof IRotatableGuiElement) {
            Vec3 rotation = ((IRotatableGuiElement) this).getRotation();
            if (rotation != null) {
                GL11.glRotated(rotation.xCoord, 1.0F, 0.F, 0.0F);
                GL11.glRotated(rotation.yCoord, 0.0F, 1.0F, 0.0F);
                GL11.glRotated(rotation.zCoord, 0.0F, 0.0F, 1.0F);
            }
        }
        if (this instanceof IScalableGuiElement) {
            Vec3 scale = ((IScalableGuiElement) this).getScale();
            if (scale != null) {
                GL11.glScaled(scale.xCoord, scale.yCoord, scale.zCoord);
            }
        }
        GL11.glTranslatef((float) -this.getWidth() / 2.0F, (float) -this.getHeight() / 2.0F, 0.0F);

        boolean debug = false;
        this.doDrawBackground(partialTicks);
        this.doDrawForeground(partialTicks);
        if (debug && this.parent == null) {
            int color = (this.hashCode() & 0xFFFFFF) | 0x88000000;
            GL11.glDepthMask(false);
            GL11.glColorMask(true, true, true, false);
            GuiHelper.drawGradientRect(0, 0, this.getWidth(), this.getHeight(), color, color, this.getZLevel() + 1.0F);
            GL11.glColorMask(true, true, true, true);
            GL11.glDepthMask(true);
        }
        for (IGuiElement element : this.elements) {
            GL11.glPushMatrix();
            element.draw(partialTicks);
            if (debug) {
                int color = (element.hashCode() & 0xFFFFFF) | 0x88000000;
                GL11.glDepthMask(false);
                GL11.glColorMask(true, true, true, false);
                GuiHelper.drawGradientRect(0, 0, element.getWidth(), element.getHeight(), color, color, element.getZLevel() + 1.0F);
                GL11.glColorMask(true, true, true, true);
                GL11.glDepthMask(true);
            }
            GL11.glPopMatrix();
        }
    }

    public abstract void doDrawForeground(float partialTicks);

    public abstract void doDrawBackground(float partialTicks);

    @Override
    public boolean onClickMe(int mouseX, int mouseY, int button) {
        return false;
    }

    @Override
    public boolean onClick(int mouseX, int mouseY, int button) {
        if (!this.visible) {
            return false;
        }
        boolean clicked = false;
        for (IGuiElement element : this.elements) {
            if (!element.isVisible()) {
                continue;
            }
            int posX = element.getPosX();
            int posY = element.getPosY();
            if (GuiBase.isInsideBounds(mouseX, mouseY, posX, posY, posX + element.getWidth(), posY + element.getHeight())) {
                element.onClick(mouseX - posX, mouseY - posY, button);
                clicked = true;
            }
        }
        return !clicked && this.onClickMe(mouseX, mouseY, button);
    }

    @Override
    public boolean onKey(char character, int keycode) {
        return this.visible && this.focusedElement != null && this.focusedElement.onKey(character, keycode);
    }

    @Override
    public void propagateFocusChangeDown(IFocusableElement element) {
        this.focusedElement = element;
        for (IGuiElement currentElement : this.elements) {
            element.propagateFocusChangeDown(element);
        }
    }

    @Override
    public void propagateFocusChangeUp(IFocusableElement element) {
        if (this.parent != null) {
            this.parent.propagateFocusChangeUp(element);
        } else {
            this.propagateFocusChangeDown(element);
        }
    }

    @Override
    public void updateTick() {
        for (IGuiElement element : this.elements) {
            element.updateTick();
        }
        for (ITickListener listener : this.tickListeners) {
            listener.onTick();
        }
    }

    @Override
    public void addTickListener(ITickListener listener) {
        this.tickListeners.add(listener);
    }

    public static void playButtonClick() {
        MC.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
    }

    public static boolean isInsideBounds(int posX, int posY, int minX, int minY, int maxX, int maxY) {
        return posX >= minX && posX <= maxX && posY >= minY && posY <= maxY;
    }

    public void drawRoot(int mouseX, int mouseY, float partialTicks) {
        GL11.glPushMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_BLEND);
        this.draw(partialTicks);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    @Override
    public List<String> findTooltip(float mouseX, float mouseY) {
        List<String> result = null;
        for (IGuiElement element : this.getChildElements()) {
            result = element.findTooltip(mouseX, mouseY);
            if (result != null) {
                return result;
            }
        }
        if (this.hasTooltip()) {
            if (mouseX >= this.getPosX() && mouseX < this.getPosX() + this.getWidth() && mouseY >= this.getPosY() && mouseY < this.getPosY() + this.getHeight()) {
                result = this.getTooltip(mouseX - this.getPosX(), mouseY - this.getPosY());
            }
        }
        return result;
    }

    @Override
    public boolean hasTooltip() {
        return false;
    }

    @Override
    public List<String> getTooltip(float mouseX, float mouseY) {
        return null;
    }

    // GuiScreen overrides

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public final void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawRoot(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (!this.onClick(mouseX, mouseY, mouseButton)) {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void keyTyped(char character, int keycode) {
        if (!this.onKey(character, keycode)) {
            super.keyTyped(character, keycode);
        }
    }

    @Override
    protected void drawGradientRect(int startX, int startY, int endX, int endY, int startColor, int endColor) {
        GuiHelper.drawGradientRect(startX, startY, endX, endY, startColor, endColor, this.getZLevel());
    }

}
*/

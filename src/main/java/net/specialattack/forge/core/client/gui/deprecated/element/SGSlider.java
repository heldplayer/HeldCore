package net.specialattack.forge.core.client.gui.deprecated.element;

import net.minecraft.client.renderer.GlStateManager;
import net.specialattack.forge.core.client.gui.GuiHelper;
import net.specialattack.forge.core.client.gui.deprecated.SizeContext;
import net.specialattack.forge.core.client.gui.deprecated.layout.FlowDirection;
import org.lwjgl.opengl.GL11;

public class SGSlider extends SGComponent {

    private FlowDirection direction;
    private double position;
    private double min, max;
    private boolean integer, drawText;

    public SGSlider(FlowDirection direction, double min, double max, boolean integer, boolean drawText) {
        this.direction = direction;
        this.min = min;
        this.max = max;
        this.integer = integer;
        this.drawText = drawText;

        if (direction == FlowDirection.HORIZONTAL) {
            this.setPreferredInnerSize(50, 12);
        } else {
            this.setPreferredInnerSize(12, 50);
        }
    }

    public double getValue() {
        return this.min + (this.max - this.min) * this.position;
    }

    public int getValueInt() {
        return (int) Math.round(this.min + (this.max - this.min) * this.position);
    }

    @Override
    public void drawBackground(int mouseX, int mouseY, float partialTicks) {
        super.drawBackground(mouseX, mouseY, partialTicks);
        GlStateManager.disableTexture2D();
        GlStateManager.translate(this.getLeft(SizeContext.INNER), this.getTop(SizeContext.INNER), this.getZLevel());
        int width = this.getWidth(SizeContext.INNER);
        int height = this.getHeight(SizeContext.INNER);

        if (this.direction == FlowDirection.HORIZONTAL) {
            GuiHelper.drawColoredRect(1, height / 2 - 1, width - 1, height / 2 + 1, 0xFF202020, 0.0F);
        } else {
            GuiHelper.drawColoredRect(width / 2 - 1, 1, width / 2 + 1, height - 1, 0xFF202020, 0.0F);
        }
    }

    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTicks) {
        //GL11.glTranslatef(this.getLeft(SizeContext.INNER), this.getTop(SizeContext.INNER), this.getZLevel());
        GlStateManager.disableTexture2D();
        int width = this.getWidth(SizeContext.INNER);
        int height = this.getHeight(SizeContext.INNER);

        if (this.direction == FlowDirection.HORIZONTAL) {
            int left = (int) (this.position * (width - 2) / (this.max - this.min));
            GuiHelper.drawColoredRect(left, 1, left + 2, height - 1, 0xFF888888, 0.0F);
        } else {
            int top = (int) (this.position * (height - 2) / (this.max - this.min));
            GuiHelper.drawColoredRect(1, top, width - 1, top + 2, 0xFF888888, 0.0F);
        }
    }

    @Override
    public void onMouseDrag(int oldX, int oldY, int newX, int newY, int button, long pressTime) {
        super.onMouseDrag(oldX, oldY, newX, newY, button, pressTime);
        if (this.direction == FlowDirection.HORIZONTAL) {
            double scaledWidth = this.getWidth(SizeContext.INNER) - 2;
            double position = GuiHelper.getScaled(scaledWidth, this.position, 1.0D);
            this.position = GuiHelper.getScaled(1.0D, position + newX - oldX, scaledWidth);
        } else {
            double scaledHeight = this.getHeight(SizeContext.INNER) - 2;
            double position = GuiHelper.getScaled(scaledHeight, this.position, 1.0D);
            this.position = GuiHelper.getScaled(1.0D, position + newY - oldY, scaledHeight);
        }
    }
}

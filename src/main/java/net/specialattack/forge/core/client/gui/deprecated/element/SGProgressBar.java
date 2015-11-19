package net.specialattack.forge.core.client.gui.deprecated.element;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import net.specialattack.forge.core.client.gui.Color;
import net.specialattack.forge.core.client.gui.GuiHelper;
import net.specialattack.forge.core.client.gui.deprecated.SizeContext;
import net.specialattack.forge.core.client.gui.deprecated.layout.FlowDirection;
import net.specialattack.forge.core.client.gui.deprecated.layout.Region;
import net.specialattack.forge.core.client.gui.deprecated.style.StyleDefs;
import net.specialattack.forge.core.client.gui.deprecated.style.border.InvisibleBorder;
import org.lwjgl.opengl.GL11;

public class SGProgressBar extends SGComponent {

    private float progress;
    private State state;
    private FlowDirection direction;
    private int timer;

    public SGProgressBar(State state, FlowDirection direction) {
        this.setState(state);
        this.setBackground(StyleDefs.BACKGROUND_PROGRESS_BAR);
        this.setBorder(new InvisibleBorder(1));
        this.direction = direction;
        if (direction == FlowDirection.HORIZONTAL) {
            this.setPreferredInnerSize(50, 4);
        } else {
            this.setPreferredInnerSize(4, 50);
        }
    }

    public SGProgressBar(State state) {
        this(state, FlowDirection.HORIZONTAL);
    }

    public SGProgressBar(FlowDirection direction) {
        this(State.NORMAL, direction);
    }

    public SGProgressBar() {
        this(State.NORMAL, FlowDirection.HORIZONTAL);
    }

    public void setProgress(float progress) {
        this.progress = MathHelper.clamp_float(progress, 0.0F, 100.0F);
    }

    public void setState(State state) {
        this.state = state == null ? State.NORMAL : state;
    }

    @Override
    public void updateTick() {
        super.updateTick();
        this.timer++;
    }

    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.translate(this.getLeft(SizeContext.INNER), this.getTop(SizeContext.INNER), this.getZLevel());
        int totalWidth = this.getWidth(SizeContext.INNER);
        int totalHeight = this.getHeight(SizeContext.INNER);
        Color color = this.state.getColor();
        Region region = this.state.decideRegion(totalWidth, totalHeight, this.progress, this.direction, this.timer);
        GuiHelper.drawColoredRect(region.getLeft(), region.getTop(), region.getWidth(), region.getHeight(), color.colorHex, 0.0F);
    }

    public enum State {
        NORMAL {
            @Override
            public Color getColor() {
                return StyleDefs.COLOR_PROGRESS_BAR_NORMAL;
            }
        },
        PAUSED {
            @Override
            public Color getColor() {
                return StyleDefs.COLOR_PROGRESS_BAR_PAUSED;
            }
        },
        ERROR {
            @Override
            public Color getColor() {
                return StyleDefs.COLOR_PROGRESS_BAR_ERROR;
            }
        },
        CONTINUOUS {
            @Override
            public Color getColor() {
                return StyleDefs.COLOR_PROGRESS_BAR_CONTINUOUS;
            }

            @Override
            public Region decideRegion(int totalWidth, int totalHeight, float progress, FlowDirection direction, int counter) {
                if (direction == FlowDirection.HORIZONTAL) {
                    int left = (counter * 2) % (totalWidth + 30) - 15;
                    return new Region(MathHelper.clamp_int(left, 0, totalWidth), 0, MathHelper.clamp_int(left + 10, 0, totalWidth), totalHeight);
                } else {
                    int top = (counter * 2) % (totalHeight + 30) - 15;
                    return new Region(0, totalHeight - MathHelper.clamp_int(top + 10, 0, totalHeight), totalWidth, totalHeight - MathHelper.clamp_int(top, 0, totalHeight));
                }
            }
        },
        INDETERMINATE {
            @Override
            public Color getColor() {
                return StyleDefs.COLOR_PROGRESS_BAR_CONTINUOUS;
            }

            @Override
            public Region decideRegion(int totalWidth, int totalHeight, float progress, FlowDirection direction, int counter) {
                if (direction == FlowDirection.HORIZONTAL) {
                    int left = MathHelper.abs_int((counter * 2) % (totalWidth * 2 - 20) - totalWidth + 10);
                    return new Region(MathHelper.clamp_int(left, 0, totalWidth), 0, MathHelper.clamp_int(left + 10, 0, totalWidth), totalHeight);
                } else {
                    int top = MathHelper.abs_int((counter * 2) % (totalHeight * 2 - 20) - totalHeight + 10);
                    return new Region(0, totalHeight - MathHelper.clamp_int(top + 10, 0, totalHeight), totalWidth, totalHeight - MathHelper.clamp_int(top, 0, totalHeight));
                }
            }
        };

        public abstract Color getColor();

        public Region decideRegion(int totalWidth, int totalHeight, float progress, FlowDirection direction, int counter) {
            if (direction == FlowDirection.HORIZONTAL) {
                int width = (int) GuiHelper.getScaled(totalWidth, progress, 100.0F);
                return new Region(0, 0, width, totalHeight);
            } else {
                int height = (int) GuiHelper.getScaled(totalHeight, progress, 100.0F);
                return new Region(0, totalHeight - height, totalWidth, totalHeight);
            }
        }
    }

}

package net.specialattack.forge.core.client.gui.deprecated;

import java.util.LinkedList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.specialattack.forge.core.client.MC;
import net.specialattack.forge.core.client.gui.GuiHelper;
import net.specialattack.forge.core.client.gui.deprecated.element.SGComponent;
import net.specialattack.forge.core.client.gui.deprecated.layout.Region;
import org.lwjgl.opengl.GL11;

public final class SGUtils {

    private SGUtils() {
    }

    public static void drawBox(int left, int top, int width, int height, float zLevel, int color) {
        GuiHelper.drawColoredRect(left, top, left + width, top + 1, color, zLevel); // Top
        GuiHelper.drawColoredRect(left, top + height - 1, left + width, top + height, color, zLevel); // Bottom
        GuiHelper.drawColoredRect(left, top, left + 1, top + height - 1, color, zLevel); // Left
        GuiHelper.drawColoredRect(left + width - 1, top, left + width, top + height - 1, color, zLevel); // Right
    }

    public static void drawErrorBox(SGComponent component) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(component.getLeft(SizeContext.OUTLINE), component.getTop(SizeContext.OUTLINE), component.getZLevel());
        int width = component.getWidth(SizeContext.OUTLINE);
        int height = component.getHeight(SizeContext.OUTLINE);

        GuiHelper.drawColoredRect(0, 0, width, height, 0xFFFFFFFF, 0.0F); // Fill
        SGUtils.drawBox(0, 0, width, height, 0.0F, 0xFFFF0000);

        GL11.glLineWidth(2.0F);
        GlStateManager.disableTexture2D();
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3f(1.0F, 1.0F, 0.0F);
        GL11.glVertex3f(width - 1.0F, height - 1.0F, 0.0F);
        GL11.glVertex3f(width - 1.0F, 1.0F, 0.0F);
        GL11.glVertex3f(1.0F, height - 1.0F, 0.0F);
        GL11.glEnd();

        GlStateManager.popMatrix();
    }

    private static LinkedList<Region> clipRegions = new LinkedList<Region>();

    private static void clipRegion(Region region) {
        Minecraft mc = MC.getMc();
        ScaledResolution resolution = new ScaledResolution(mc);
        float scaleW = (float) mc.displayWidth / (float) resolution.getScaledWidth_double();
        float scaleH = (float) mc.displayHeight / (float) resolution.getScaledHeight_double();

        int x = (int) (region.getLeft() * scaleW);
        int y = (int) (region.getTop() * scaleH);
        int width = (int) (region.getWidth() * scaleW);
        int height = (int) (region.getHeight() * scaleH);

        if (width < 0)
            width = 0;
        if (height < 0)
            height = 0;

        GL11.glScissor(x, mc.displayHeight - y - height, width, height);
    }

    public static void clipComponent(SGComponent component) {
        Region region = component.getRenderingRegion();
        if (SGUtils.clipRegions.size() == 0) {
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
        }
        SGUtils.clipRegions.add(region);
        SGUtils.clipRegion(region);
    }

    public static void endClip() {
        SGUtils.clipRegions.removeLast();
        if (SGUtils.clipRegions.size() > 0) {
            SGUtils.clipRegion(SGUtils.clipRegions.getLast());
        } else {
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }
    }

    public static void endAllClips() {
        if (SGUtils.clipRegions.size() > 0) {
            SGUtils.clipRegions.clear();
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }
    }

    public static Region findPopoutRegion(boolean horizontal, Region around, Region size, int rootWidth, int rootHeight) {
        if (horizontal) {
            int left, width;
            if (around.getLeft() + around.getWidth() + size.getLeft() + size.getWidth() < rootWidth) { // Check if there is enough room to display on the right
                left = around.getLeft() + around.getWidth() + size.getLeft();
                width = size.getWidth();
            } else if (around.getLeft() - size.getWidth() - size.getLeft() >= 0) { // Check if there is enough room to display on the left
                left = around.getLeft() - around.getWidth() - size.getLeft() - size.getWidth();
                width = size.getWidth();
            } else { // Well, let's just put it edged against the right end of the screen
                width = Math.min(rootWidth, size.getWidth());
                left = rootWidth - width;
            }
            int top, height;
            if (around.getTop() + size.getTop() + size.getHeight() < rootHeight) { // Check to see if there is enough room to go down
                top = around.getTop();
                height = size.getHeight();
            } else if (around.getTop() + around.getHeight() - size.getTop() - size.getHeight() >= 0) { // Check to see if there is enough room to go up
                top = around.getTop() + around.getHeight() - size.getTop() - size.getHeight();
                height = size.getHeight();
            } else { // Well, let's just make it use the biggest space
                int down = rootHeight - around.getTop();
                int up = around.getTop() + around.getHeight();
                if (down > up) { // We go down
                    top = around.getTop();
                    height = down;
                } else { // We go up
                    height = up;
                    top = 0;
                }
            }
            return new Region(left, top, width, height);
        } else {
            int top, height;
            if (around.getTop() + around.getHeight() + size.getTop() + size.getHeight() < rootHeight) { // Check if there is enough room to display on the right
                top = around.getTop() + around.getHeight() + size.getTop();
                height = size.getHeight();
            } else if (around.getTop() - size.getHeight() - size.getTop() >= 0) { // Check if there is enough room to display on the top
                top = around.getTop() - around.getHeight() - size.getTop() - size.getHeight();
                height = size.getHeight();
            } else { // Well, let's just put it edged against the right end of the screen
                height = Math.min(rootHeight - around.getHeight(), size.getHeight());
                top = rootHeight - height;
            }
            int left, width;
            if (around.getLeft() + size.getLeft() + size.getWidth() < rootWidth) { // Check to see if there is enough room to go down
                left = around.getLeft();
                width = size.getWidth();
            } else if (around.getLeft() + around.getWidth() - size.getLeft() - size.getWidth() >= 0) { // Check to see if there is enough room to go up
                left = around.getLeft() + around.getWidth() - size.getLeft() - size.getWidth();
                width = size.getWidth();
            } else { // Well, let's just make it use the biggest space
                int down = rootWidth - around.getLeft();
                int up = around.getLeft() + around.getWidth();
                if (down > up) { // We go down
                    left = around.getLeft();
                    width = down;
                } else { // We go up
                    width = up;
                    left = 0;
                }
            }
            return new Region(left, top, width, height);
        }
    }

}

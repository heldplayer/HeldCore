package net.specialattack.forge.core.client.gui;

import java.util.LinkedList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.specialattack.forge.core.client.MC;
import net.specialattack.forge.core.client.gui.element.SGComponent;
import net.specialattack.forge.core.client.gui.layout.Region;
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
        GL11.glPushMatrix();
        GL11.glTranslatef(component.getLeft(SizeContext.OUTLINE), component.getTop(SizeContext.OUTLINE), component.getZLevel());
        int width = component.getWidth(SizeContext.OUTLINE);
        int height = component.getHeight(SizeContext.OUTLINE);

        GuiHelper.drawColoredRect(0, 0, width, height, 0xFFFFFFFF, 0.0F); // Fill
        SGUtils.drawBox(0, 0, width, height, 0.0F, 0xFFFF0000);

        GL11.glLineWidth(2.0F);
        GuiStateManager.disableTextures();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3f(1.0F, 1.0F, 0.0F);
        GL11.glVertex3f(width - 1.0F, height - 1.0F, 0.0F);
        GL11.glVertex3f(width - 1.0F, 1.0F, 0.0F);
        GL11.glVertex3f(1.0F, height - 1.0F, 0.0F);
        GL11.glEnd();

        GL11.glPopMatrix();
    }

    private static LinkedList<Region> clipRegions = new LinkedList<Region>();

    private static void clipRegion(Region region) {
        Minecraft mc = MC.getMinecraft();
        ScaledResolution resolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        float scaleW = (float) mc.displayWidth / (float) resolution.getScaledWidth_double();
        float scaleH = (float) mc.displayHeight / (float) resolution.getScaledHeight_double();

        int x = (int) (region.left * scaleW);
        int y = (int) (region.top * scaleH);
        int width = (int) (region.width * scaleW);
        int height = (int) (region.height * scaleH);

        if (width < 0)
            width = 0;
        if (height < 0)
            height = 0;

        GL11.glScissor(x, mc.displayHeight - y - height, width, height);
    }

    public static void clipComponent(SGComponent component) {
        Region region = component.getRenderingRegion();
        if (clipRegions.size() == 0) {
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
        }
        clipRegions.add(region);
        clipRegion(region);
    }

    public static void endClip() {
        clipRegions.removeLast();
        if (clipRegions.size() > 0) {
            clipRegion(clipRegions.getLast());
        } else {
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }
    }

    public static void endAllClips() {
        if (clipRegions.size() > 0) {
            clipRegions.clear();
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }
    }

    public static Region findPopoutRegion(boolean horizontal, Region around, Region size, int rootWidth, int rootHeight) {
        if (horizontal) {
            int left, width;
            if (around.left + around.width + size.left + size.width < rootWidth) { // Check if there is enough room to display on the right
                left = around.left + around.width + size.left;
                width = size.width;
            } else if (around.left - size.width - size.left >= 0) { // Check if there is enough room to display on the left
                left = around.left - around.width - size.left - size.width;
                width = size.width;
            } else { // Well, let's just put it edged against the right end of the screen
                width = Math.min(rootWidth, size.width);
                left = rootWidth - width;
            }
            int top, height;
            if (around.top + size.top + size.height < rootHeight) { // Check to see if there is enough room to go down
                top = around.top;
                height = size.height;
            } else if (around.top + around.height - size.top - size.height >= 0) { // Check to see if there is enough room to go up
                top = around.top + around.height - size.top - size.height;
                height = size.height;
            } else { // Well, let's just make it use the biggest space
                int down = rootHeight - around.top;
                int up = around.top + around.height;
                if (down > up) { // We go down
                    top = around.top;
                    height = down;
                } else { // We go up
                    height = up;
                    top = 0;
                }
            }
            return new Region(left, top, width, height);
        } else {
            int top, height;
            if (around.top + around.height + size.top + size.height < rootHeight) { // Check if there is enough room to display on the right
                top = around.top + around.height + size.top;
                height = size.height;
            } else if (around.top - size.height - size.top >= 0) { // Check if there is enough room to display on the top
                top = around.top - around.height - size.top - size.height;
                height = size.height;
            } else { // Well, let's just put it edged against the right end of the screen
                height = Math.min(rootHeight - around.height, size.height);
                top = rootHeight - height;
            }
            int left, width;
            if (around.left + size.left + size.width < rootWidth) { // Check to see if there is enough room to go down
                left = around.left;
                width = size.width;
            } else if (around.left + around.width - size.left - size.width >= 0) { // Check to see if there is enough room to go up
                left = around.left + around.width - size.left - size.width;
                width = size.width;
            } else { // Well, let's just make it use the biggest space
                int down = rootWidth - around.left;
                int up = around.left + around.width;
                if (down > up) { // We go down
                    left = around.left;
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

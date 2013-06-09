
package me.heldplayer.util.HeldCore.client;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * A helper class used for rendering on GUIs
 * 
 * @author heldplayer
 * 
 */
@SideOnly(Side.CLIENT)
public abstract class GuiHelper {

    /**
     * Draws a liquid tank
     * 
     * @param itemId
     *        The item ID of the liquid
     * @param itemMeta
     *        The meta for the liquid
     * @param left
     *        The x position to start at
     * @param top
     *        The y position to start at
     * @param width
     *        The width of the tank to render
     * @param height
     *        The height of the tank to render
     */
    public static void drawLiquid(int itemId, int itemMeta, int left, int top, int width, int height) {
        Item item = Item.itemsList[itemId];
        if (item == null) {
            return;
        }
        ItemStack stack = new ItemStack(item, 1, itemMeta);

        if (stack.getItemSpriteNumber() == 0) {
            Minecraft.getMinecraft().renderEngine.bindTexture("/terrain.png");
        }
        else {
            Minecraft.getMinecraft().renderEngine.bindTexture("/gui/items.png");
        }

        for (int i = 0; i <= item.getRenderPasses(itemMeta); i++) {
            Icon icon = item.getIconFromDamageForRenderPass(itemMeta, i);
            int color = item.getColorFromItemStack(stack, i);
            float red = (color >> 16 & 0xFF) / 255.0F;
            float green = (color >> 8 & 0xFF) / 255.0F;
            float blue = (color & 0xFF) / 255.0F;

            GL11.glColor4f(red, green, blue, 1.0F);

            for (int x = 0; x < width; x += 16) {
                for (int y = 0; y < height; y += 16) {
                    int drawWidth = width - x > 16 ? 16 : width - x;
                    int drawHeight = height - y > 16 ? 16 : height - y;

                    float pixelSize = 0.0F;

                    if (stack.getItemSpriteNumber() == 0) {
                        pixelSize = 1.0F / (float) Minecraft.getMinecraft().renderEngine.textureMapBlocks.getTexture().getHeight();
                    }
                    else {
                        pixelSize = 1.0F / (float) Minecraft.getMinecraft().renderEngine.textureMapItems.getTexture().getHeight();
                    }

                    float pixels = pixelSize * 16 - pixelSize * drawHeight;

                    drawTexturedModalRect(left + x, top + height - y - drawHeight, drawWidth, drawHeight, 0.0F, icon.getMinU(), icon.getMinV() + pixels, icon.getMaxU(), icon.getMaxV());
                }
            }
        }
    }

    /**
     * Scales an int for drawing in a GUI
     * 
     * @param scale
     *        The resulting max value of the int
     * @param amount
     *        The amount
     * @param total
     *        The max amount that can be entered
     * @return
     */
    public static int getScaled(int scale, int amount, int total) {
        if (amount > total) {
            amount = total;
        }

        return amount * scale / total;
    }

    /**
     * Draws a tooltip
     * 
     * @param strings
     *        The strings to draw
     * @param fontRenderer
     *        The font renderer instance to use
     * @param mouseX
     *        The x position of the mouse
     * @param mouseY
     *        The y position of the mouse
     * @param guiTop
     *        The top position of the GUI
     * @param height
     *        The height of the GUI
     */
    public static void drawTooltip(List<String> strings, FontRenderer fontRenderer, int mouseX, int mouseY, int guiTop, int height) {
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        if (!strings.isEmpty()) {
            int startY = 0;
            int xPos;
            int yPos;

            for (xPos = 0; xPos < strings.size(); ++xPos) {
                yPos = fontRenderer.getStringWidth((String) strings.get(xPos));

                if (yPos > startY) {
                    startY = yPos;
                }
            }

            xPos = mouseX + 12;
            yPos = mouseY - 12;
            int width = 8;

            if (strings.size() > 1) {
                width += 2 + (strings.size() - 1) * 10;
            }

            if (guiTop + yPos + width + 6 > height) {
                yPos = height - width - guiTop - 6;
            }

            int color1 = 0xF0100010;
            drawGradientRect(xPos - 3, yPos - 4, xPos + startY + 3, yPos - 3, color1, color1, 300.0F);
            drawGradientRect(xPos - 3, yPos + width + 3, xPos + startY + 3, yPos + width + 4, color1, color1, 300.0F);
            drawGradientRect(xPos - 3, yPos - 3, xPos + startY + 3, yPos + width + 3, color1, color1, 300.0F);
            drawGradientRect(xPos - 4, yPos - 3, xPos - 3, yPos + width + 3, color1, color1, 300.0F);
            drawGradientRect(xPos + startY + 3, yPos - 3, xPos + startY + 4, yPos + width + 3, color1, color1, 300.0F);
            int color2 = 0x505000FF;
            int color3 = (color2 & 0xFEFEFE) >> 1 | color2 & 0x1000000;
            drawGradientRect(xPos - 3, yPos - 3 + 1, xPos - 3 + 1, yPos + width + 3 - 1, color2, color3, 300.0F);
            drawGradientRect(xPos + startY + 2, yPos - 3 + 1, xPos + startY + 3, yPos + width + 3 - 1, color2, color3, 300.0F);
            drawGradientRect(xPos - 3, yPos - 3, xPos + startY + 3, yPos - 3 + 1, color2, color2, 300.0F);
            drawGradientRect(xPos - 3, yPos + width + 2, xPos + startY + 3, yPos + width + 3, color3, color3, 300.0F);

            for (int i = 0; i < strings.size(); ++i) {
                String currentLine = (String) strings.get(i);

                if (i > 0) {
                    currentLine = "\u00a77" + currentLine;
                }

                fontRenderer.drawStringWithShadow(currentLine, xPos, yPos, -1);

                if (i == 0) {
                    yPos += 2;
                }

                yPos += 10;
            }
        }
    }

    /**
     * Draws a gradient rectangle
     * 
     * @param startX
     *        The starting x position
     * @param startY
     *        The starting y position
     * @param endX
     *        The ending x position
     * @param endY
     *        The ending y position
     * @param color1
     *        The first colour
     * @param color2
     *        The last colour
     * @param zLevel
     *        The z-level for rendering
     */
    public static void drawGradientRect(int startX, int startY, int endX, int endY, int color1, int color2, float zLevel) {
        float alpha1 = (float) (color1 >> 24 & 255) / 255.0F;
        float red1 = (float) (color1 >> 16 & 255) / 255.0F;
        float green1 = (float) (color1 >> 8 & 255) / 255.0F;
        float blue1 = (float) (color1 & 255) / 255.0F;
        float alpha2 = (float) (color2 >> 24 & 255) / 255.0F;
        float red2 = (float) (color2 >> 16 & 255) / 255.0F;
        float green2 = (float) (color2 >> 8 & 255) / 255.0F;
        float blue2 = (float) (color2 & 255) / 255.0F;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        Tessellator tes = Tessellator.instance;
        tes.startDrawingQuads();
        tes.setColorRGBA_F(red1, green1, blue1, alpha1);
        tes.addVertex((double) endX, (double) startY, (double) zLevel);
        tes.addVertex((double) startX, (double) startY, (double) zLevel);
        tes.setColorRGBA_F(red2, green2, blue2, alpha2);
        tes.addVertex((double) startX, (double) endY, (double) zLevel);
        tes.addVertex((double) endX, (double) endY, (double) zLevel);
        tes.draw();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    /**
     * Draws a textured rectangle
     * 
     * @param startX
     *        The starting x position
     * @param startY
     *        The starting y position
     * @param width
     *        The width of the rectangle
     * @param height
     *        The height of the rectangle
     * @param zLevel
     *        The z-level for rendering
     * @param startU
     *        The starting texture u location
     * @param startV
     *        The starting texture v location
     * @param endU
     *        The ending texture u location
     * @param endV
     *        The ending texture v location
     */
    public static void drawTexturedModalRect(int startX, int startY, int width, int height, float zLevel, float startU, float startV, float endU, float endV) {
        Tessellator tes = Tessellator.instance;
        tes.startDrawingQuads();
        tes.addVertexWithUV((double) startX, (double) (startY + height), (double) zLevel, startU, endV);
        tes.addVertexWithUV((double) (startX + width), (double) (startY + height), (double) zLevel, endU, endV);
        tes.addVertexWithUV((double) (startX + width), (double) startY, (double) zLevel, endU, startV);
        tes.addVertexWithUV((double) startX, (double) startY, (double) zLevel, startU, startV);
        tes.draw();
    }

}

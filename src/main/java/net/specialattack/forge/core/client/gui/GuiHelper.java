package net.specialattack.forge.core.client.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.specialattack.forge.core.client.MC;
import net.specialattack.forge.core.client.resources.data.TextureMetadataSection;
import net.specialattack.forge.core.client.texture.IconHolder;
import org.lwjgl.opengl.GL11;

/**
 * A helper class used for rendering on GUIs
 *
 * @author heldplayer
 */
@SideOnly(Side.CLIENT)
public final class GuiHelper {

    private static final ArrayList<String> reusableArrayList = new ArrayList<String>();

    private GuiHelper() {
    }

    /**
     * Draws a fluid tank
     *
     * @param fluid
     *         The fluid to render
     * @param left
     *         The x position to start at
     * @param top
     *         The y position to start at
     * @param width
     *         The width of the tank to render
     * @param height
     *         The height of the tank to render
     */
    public static void drawFluid(Fluid fluid, int left, int top, int width, int height, float zLevel) {
        /* Disabled but kept if needed, as fluid.getIcon() no longer exists
        MC.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        TextureAtlasSprite icon = RenderHelper.getIconSafe(fluid.getIcon());
        int color = fluid.getColor();
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;

        GlStateManager.color(red, green, blue, 1.0F);

        for (int x = 0; x < width; x += 16) {
            for (int y = 0; y < height; y += 16) {
                int drawWidth = width - x > 16 ? 16 : width - x;
                int drawHeight = height - y > 16 ? 16 : height - y;

                float minU = icon.getMinU();
                float minV = icon.getInterpolatedV(16.0F - drawHeight);
                float maxU = icon.getMaxU();
                float maxV = icon.getMaxV();

                GuiHelper.drawTexturedModalRect(left + x, top + height - y - drawHeight, drawWidth, drawHeight, zLevel, minU, minV, maxU, maxV);
            }
        }
        //*/
    }

    /**
     * Draws a textured rectangle
     *
     * @param startX
     *         The starting x position
     * @param startY
     *         The starting y position
     * @param width
     *         The width of the rectangle
     * @param height
     *         The height of the rectangle
     * @param zLevel
     *         The z-level for rendering
     * @param startU
     *         The starting texture u location
     * @param startV
     *         The starting texture v location
     * @param endU
     *         The ending texture u location
     * @param endV
     *         The ending texture v location
     */
    public static void drawTexturedModalRect(int startX, int startY, int width, int height, float zLevel, float startU, float startV, float endU, float endV) {
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2d(startU, endV);
        GL11.glVertex3d(startX, startY + height, zLevel);
        GL11.glTexCoord2d(endU, endV);
        GL11.glVertex3d(startX + width, startY + height, zLevel);
        GL11.glTexCoord2d(endU, startV);
        GL11.glVertex3d(startX + width, startY, zLevel);
        GL11.glTexCoord2d(startU, startV);
        GL11.glVertex3d(startX, startY, zLevel);
        GL11.glEnd();
    }

    /**
     * Draws a textured rectangle
     *
     * @param startX
     *         The starting x position
     * @param startY
     *         The starting y position
     * @param width
     *         The width of the rectangle
     * @param height
     *         The height of the rectangle
     * @param zLevel
     *         The z-level for rendering
     * @param startU
     *         The starting texture u location
     * @param startV
     *         The starting texture v location
     * @param endU
     *         The ending texture u location
     * @param endV
     *         The ending texture v location
     */
    public static void drawTexturedModalRect(float startX, float startY, float width, float height, float zLevel, float startU, float startV, float endU, float endV) {
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2d(startU, endV);
        GL11.glVertex3d(startX, startY + height, zLevel);
        GL11.glTexCoord2d(endU, endV);
        GL11.glVertex3d(startX + width, startY + height, zLevel);
        GL11.glTexCoord2d(endU, startV);
        GL11.glVertex3d(startX + width, startY, zLevel);
        GL11.glTexCoord2d(startU, startV);
        GL11.glVertex3d(startX, startY, zLevel);
        GL11.glEnd();
    }

    /**
     * Scales an int for drawing in a GUI
     *
     * @param scale
     *         The resulting max value of the int
     * @param amount
     *         The amount
     * @param total
     *         The max amount that can be entered
     *
     * @return Scaled int
     */
    public static int getScaled(int scale, int amount, int total) {
        if (amount > total) {
            amount = total;
        }

        return amount * scale / total;
    }

    /**
     * Scales a float for drawing in a GUI
     *
     * @param scale
     *         The resulting max value
     * @param amount
     *         The amount
     * @param total
     *         The max amount that can be entered
     *
     * @return Scaled float
     */
    public static float getScaled(float scale, float amount, float total) {
        if (amount > total) {
            amount = total;
        }
        if (amount < 0.0F) {
            amount = 0.0F;
        }

        return amount * scale / total;
    }

    /**
     * Scales a double for drawing in a GUI
     *
     * @param scale
     *         The resulting max value
     * @param amount
     *         The amount
     * @param total
     *         The max amount that can be entered
     *
     * @return Scaled double
     */
    public static double getScaled(double scale, double amount, double total) {
        if (amount > total) {
            amount = total;
        }
        if (amount < 0.0F) {
            amount = 0.0F;
        }

        return amount * scale / total;
    }

    /**
     * Draws a tooltip
     *
     * @param strings
     *         The strings to draw
     * @param fontRenderer
     *         The font renderer instance to use
     * @param mouseX
     *         The x position of the mouse
     * @param mouseY
     *         The y position of the mouse
     * @param guiTop
     *         The top position of the GUI
     * @param height
     *         The height of the GUI
     */
    public static void drawTooltip(List<String> strings, FontRenderer fontRenderer, int mouseX, int mouseY, int guiTop, int height) {
        GlStateManager.disableRescaleNormal();
        net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();

        if (!strings.isEmpty()) {
            int startY = 0;
            int xPos;
            int yPos;

            for (xPos = 0; xPos < strings.size(); ++xPos) {
                yPos = fontRenderer.getStringWidth(strings.get(xPos));

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
            GuiHelper.drawColoredRect(xPos - 3, yPos - 4, xPos + startY + 3, yPos - 3, color1, 300.0F);
            GuiHelper.drawColoredRect(xPos - 3, yPos + width + 3, xPos + startY + 3, yPos + width + 4, color1, 300.0F);
            GuiHelper.drawColoredRect(xPos - 3, yPos - 3, xPos + startY + 3, yPos + width + 3, color1, 300.0F);
            GuiHelper.drawColoredRect(xPos - 4, yPos - 3, xPos - 3, yPos + width + 3, color1, 300.0F);
            GuiHelper.drawColoredRect(xPos + startY + 3, yPos - 3, xPos + startY + 4, yPos + width + 3, color1, 300.0F);
            int color2 = 0x505000FF;
            int color3 = (color2 & 0xFEFEFE) >> 1 | color2 & 0x1000000;
            GuiHelper.drawGradientRect(xPos - 3, yPos - 3 + 1, xPos - 3 + 1, yPos + width + 3 - 1, color2, color3, 300.0F);
            GuiHelper.drawGradientRect(xPos + startY + 2, yPos - 3 + 1, xPos + startY + 3, yPos + width + 3 - 1, color2, color3, 300.0F);
            GuiHelper.drawColoredRect(xPos - 3, yPos - 3, xPos + startY + 3, yPos - 3 + 1, color2, 300.0F);
            GuiHelper.drawColoredRect(xPos - 3, yPos + width + 2, xPos + startY + 3, yPos + width + 3, color3, 300.0F);

            for (int i = 0; i < strings.size(); ++i) {
                String currentLine = strings.get(i);

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
     *         The starting x position
     * @param startY
     *         The starting y position
     * @param endX
     *         The ending x position
     * @param endY
     *         The ending y position
     * @param startColor
     *         The top colour
     * @param endColor
     *         The bottom colour
     * @param zLevel
     *         The z-level for rendering
     */
    public static void drawGradientRect(int startX, int startY, int endX, int endY, int startColor, int endColor, float zLevel) {
        GuiHelper.drawGradientRect((float) startX, (float) startY, (float) endX, (float) endY, startColor, endColor, zLevel);
        if (startColor == endColor) {
            GuiHelper.drawColoredRect((float) startX, (float) startY, (float) endX, (float) endY, startColor, zLevel);
        }
    }

    /**
     * Draws a gradient rectangle
     *
     * @param startX
     *         The starting x position
     * @param startY
     *         The starting y position
     * @param endX
     *         The ending x position
     * @param endY
     *         The ending y position
     * @param startColor
     *         The top colour
     * @param endColor
     *         The bottom colour
     * @param zLevel
     *         The z-level for rendering
     */
    public static void drawGradientRect(float startX, float startY, float endX, float endY, int startColor, int endColor, float zLevel) {
        if (startColor == endColor) {
            GuiHelper.drawColoredRect(startX, startY, endX, endY, startColor, zLevel);
        }
        float startAlpha = (float) (startColor >> 24 & 255) / 255.0F;
        float startRed = (float) (startColor >> 16 & 255) / 255.0F;
        float startGreen = (float) (startColor >> 8 & 255) / 255.0F;
        float startBlue = (float) (startColor & 255) / 255.0F;
        float endAlpha = (float) (endColor >> 24 & 255) / 255.0F;
        float endRed = (float) (endColor >> 16 & 255) / 255.0F;
        float endGreen = (float) (endColor >> 8 & 255) / 255.0F;
        float endBlue = (float) (endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.color(startRed, startGreen, startBlue, startAlpha);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex3f(endX, startY, zLevel);
        GL11.glVertex3f(startX, startY, zLevel);
        GlStateManager.color(endRed, endGreen, endBlue, endAlpha);
        GL11.glVertex3f(startX, endY, zLevel);
        GL11.glVertex3f(endX, endY, zLevel);
        GL11.glEnd();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    /**
     * Draws a gradient rectangle
     *
     * @param startX
     *         The starting x position
     * @param startY
     *         The starting y position
     * @param endX
     *         The ending x position
     * @param endY
     *         The ending y position
     * @param color
     *         The top colour
     * @param zLevel
     *         The z-level for rendering
     */
    public static void drawColoredRect(float startX, float startY, float endX, float endY, int color, float zLevel) {
        float startAlpha = (float) (color >> 24 & 255) / 255.0F;
        float startRed = (float) (color >> 16 & 255) / 255.0F;
        float startGreen = (float) (color >> 8 & 255) / 255.0F;
        float startBlue = (float) (color & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.color(startRed, startGreen, startBlue, startAlpha);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex3f(endX, startY, zLevel);
        GL11.glVertex3f(startX, startY, zLevel);
        GL11.glVertex3f(startX, endY, zLevel);
        GL11.glVertex3f(endX, endY, zLevel);
        GL11.glEnd();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static ArrayList<String> getFluidString(IFluidTank tank) {
        GuiHelper.reusableArrayList.clear();

        if (tank == null) {
            GuiHelper.reusableArrayList.add("This tank is broken");
            return GuiHelper.reusableArrayList;
        }

        FluidStack stack = tank.getFluid();

        if (stack != null && stack.amount > 0) {
            GuiHelper.reusableArrayList.add(stack.getFluid().getLocalizedName(stack));
            GuiHelper.reusableArrayList.add(StatCollector.translateToLocalFormatted("spacore.gui.container.fluid.filled", stack.amount, tank.getCapacity()).trim());
        } else {
            GuiHelper.reusableArrayList.add(StatCollector.translateToLocal("spacore.gui.container.fluid.empty"));
            GuiHelper.reusableArrayList.add(StatCollector.translateToLocalFormatted("spacore.gui.container.fluid.filled", 0, tank.getCapacity()).trim());
        }

        return GuiHelper.reusableArrayList;
    }

    public static void drawTexturedModalRect(int startX, int startY, int width, int height, double zLevel, double startU, double startV, double endU, double endV) {
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2d(startU, endV);
        GL11.glVertex3d(startX, startY + height, zLevel);
        GL11.glTexCoord2d(endU, endV);
        GL11.glVertex3d(startX + width, startY + height, zLevel);
        GL11.glTexCoord2d(endU, startV);
        GL11.glVertex3d(startX + width, startY, zLevel);
        GL11.glTexCoord2d(startU, startV);
        GL11.glVertex3d(startX, startY, zLevel);
        GL11.glEnd();
    }

    public static void drawTexturedModalRect(int startX, int startY, int u, int v, int width, int height) {
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2d(u / 256.0F, (v + height) / 256.0F);
        GL11.glVertex3d(startX, startY + height, 0.0D);
        GL11.glTexCoord2d((u + width) / 256.0F, (v + height) / 256.0F);
        GL11.glVertex3d(startX + width, startY + height, 0.0D);
        GL11.glTexCoord2d((u + width) / 256.0F, v / 256.0F);
        GL11.glVertex3d(startX + width, startY, 0.0D);
        GL11.glTexCoord2d(u / 256.0F, v / 256.0F);
        GL11.glVertex3d(startX, startY, 0.0D);
        GL11.glEnd();
    }

    public static void drawTexturedModalRect(int startX, int startY, TextureAtlasSprite icon, int width, int height) {
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2d(icon.getMinU(), icon.getMaxV());
        GL11.glVertex3d(startX, startY + height, 0.0D);
        GL11.glTexCoord2d(icon.getMaxU(), icon.getMaxV());
        GL11.glVertex3d(startX + width, startY + height, 0.0D);
        GL11.glTexCoord2d(icon.getMaxU(), icon.getMinV());
        GL11.glVertex3d(startX + width, startY, 0.0D);
        GL11.glTexCoord2d(icon.getMinU(), icon.getMinV());
        GL11.glVertex3d(startX, startY, 0.0D);
        GL11.glEnd();
    }

    public static void drawTexturedModalRect(int startX, int startY, IconHolder icon, int width, int height) {
        icon.bind();
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2d(icon.getMinU(), icon.getMaxV());
        GL11.glVertex3d(startX, startY + height, 0.0D);
        GL11.glTexCoord2d(icon.getMaxU(), icon.getMaxV());
        GL11.glVertex3d(startX + width, startY + height, 0.0D);
        GL11.glTexCoord2d(icon.getMaxU(), icon.getMinV());
        GL11.glVertex3d(startX + width, startY, 0.0D);
        GL11.glTexCoord2d(icon.getMinU(), icon.getMinV());
        GL11.glVertex3d(startX, startY, 0.0D);
        GL11.glEnd();
    }

    public static void playButtonClick() {
        MC.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
    }

    public static void drawRepeatingBackground(int left, int top, int width, int height, float zLevel, TextureMetadataSection texture) {
        int texWidth = texture.textureWidth;
        int texHeight = texture.textureHeight;
        int bLeft = 0, bTop = 0, bRight = 0, bBottom = 0;
        if (texture.repeat != null) {
            bLeft = texture.repeat.borderLeft;
            bTop = texture.repeat.borderTop;
            bRight = texture.repeat.borderRight;
            bBottom = texture.repeat.borderBottom;
        }
        int fillWidth = texWidth - bLeft - bRight;
        int fillHeight = texHeight - bTop - bBottom;
        int totalBWidth = width - bLeft - bRight;
        int totalBHeight = height - bTop - bBottom;
        int fullHorizParts = (totalBWidth / fillWidth);
        int fullVertParts = (totalBHeight / fillHeight);
        int remainingWidth = totalBWidth % fillWidth;
        int remainingHeight = totalBHeight % fillHeight;
        if (bTop > 0) { // Top
            if (bLeft > 0) { // Top left corner
                GuiHelper.drawPart(left, top, bLeft, bTop, zLevel, 0.0F, 0.0F, bLeft, bTop, texWidth, texHeight);
            }
            if (bRight > 0) { // Top right corner
                GuiHelper.drawPart(left + width - bRight, top, bRight, bTop, zLevel, texWidth - bRight, 0.0F, texWidth, bTop, texWidth, texHeight);
            }
            for (int i = 0; i <= fullHorizParts; i++) {
                if (i < fullHorizParts) {
                    GuiHelper.drawPart(left + bLeft + fillWidth * i, top, fillWidth, bTop, zLevel, bLeft, 0.0F, texWidth - bRight, bTop, texWidth, texHeight);
                } else {
                    GuiHelper.drawPart(left + bLeft + fillWidth * i, top, remainingWidth, bTop, zLevel, bLeft, 0.0F, bLeft + remainingWidth, bTop, texWidth, texHeight);
                }
            }
        }
        if (bBottom > 0) { // Bottom
            if (bLeft > 0) { // Bottom left corner
                GuiHelper.drawPart(left, top + height - bBottom, bLeft, bBottom, zLevel, 0.0F, texHeight - bBottom, bLeft, texHeight, texWidth, texHeight);
            }
            if (bRight > 0) { // Bottom right corner
                GuiHelper.drawPart(left + width - bRight, top + height - bBottom, bRight, bBottom, zLevel, texWidth - bRight, texHeight - bBottom, texWidth, texHeight, texWidth, texHeight);
            }
            for (int i = 0; i <= fullHorizParts; i++) {
                if (i < fullHorizParts) {
                    GuiHelper.drawPart(left + bLeft + fillWidth * i, top + height - bBottom, fillWidth, bBottom, zLevel, bLeft, texHeight - bBottom, texWidth - bRight, texHeight, texWidth, texHeight);
                } else {
                    GuiHelper.drawPart(left + bLeft + fillWidth * i, top + height - bBottom, remainingWidth, bBottom, zLevel, bLeft, texHeight - bBottom, bLeft + remainingWidth, texHeight, texWidth, texHeight);
                }
            }
        }
        if (bLeft > 0) { // Left
            for (int i = 0; i <= fullVertParts; i++) {
                if (i < fullVertParts) {
                    GuiHelper.drawPart(left, top + bTop + fillWidth * i, bLeft, fillHeight, zLevel, 0.0F, bTop, bLeft, texHeight - bBottom, texWidth, texHeight);
                } else {
                    GuiHelper.drawPart(left, top + bTop + fillWidth * i, bLeft, remainingHeight, zLevel, 0.0F, bTop, bLeft, bTop + remainingHeight, texWidth, texHeight);
                }
            }
        }
        if (bRight > 0) { // Right
            for (int i = 0; i <= fullVertParts; i++) {
                if (i < fullVertParts) {
                    GuiHelper.drawPart(left + width - bRight, top + bTop + fillWidth * i, bRight, fillHeight, zLevel, texWidth - bRight, bTop, texWidth, texHeight - bBottom, texWidth, texHeight);
                } else {
                    GuiHelper.drawPart(left + width - bRight, top + bTop + fillWidth * i, bRight, remainingHeight, zLevel, texWidth - bRight, bTop, texWidth, bTop + remainingHeight, texWidth, texHeight);
                }
            }
        }
        for (int i = 0; i <= fullHorizParts; i++) { // Center
            int currentWidth = i < fullHorizParts ? fillWidth : remainingWidth;
            for (int j = 0; j <= fullVertParts; j++) {
                int currentHeight = j < fullVertParts ? fillHeight : remainingHeight;
                GuiHelper.drawPart(left + bLeft + fillWidth * i, top + bTop + fillWidth * j, currentWidth, currentHeight, zLevel, bLeft, bTop, bLeft + currentWidth, bTop + currentHeight, texWidth, texHeight);
            }
        }
    }

    private static void drawPart(int startX, int startY, int width, int height, float zLevel, float startU, float startV, float endU, float endV, float texWidth, float texHeight) {
        GuiHelper.drawTexturedModalRect(startX, startY, width, height, zLevel, startU / texWidth, startV / texHeight, endU / texWidth, endV / texHeight);
    }
}

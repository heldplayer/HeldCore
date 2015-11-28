package net.specialattack.forge.core.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.specialattack.forge.core.client.RenderHelper;
import net.specialattack.forge.core.client.texture.IconHolder;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiButtonIcon extends GuiButton {

    private IconHolder normalIcon;
    private IconHolder hoverIcon;
    private IconHolder disabledIcon;
    private ResourceLocation iconMap;

    public GuiButtonIcon(int id, int posX, int posY, int width, int height, String text, IconHolder normalIcon, ResourceLocation iconMap) {
        super(id, posX, posY, width, height, text);
        this.normalIcon = normalIcon;
        this.hoverIcon = normalIcon;
        this.disabledIcon = normalIcon;
        this.iconMap = iconMap;
    }

    public GuiButtonIcon(int id, int posX, int posY, int width, int height, String text, IconHolder normalIcon, IconHolder hoverIcon, ResourceLocation iconMap) {
        super(id, posX, posY, width, height, text);
        this.normalIcon = normalIcon;
        this.hoverIcon = hoverIcon;
        this.disabledIcon = normalIcon;
        this.iconMap = iconMap;
    }

    public GuiButtonIcon(int id, int posX, int posY, int width, int height, String text, IconHolder normalIcon, IconHolder hoverIcon, IconHolder disabledIcon, ResourceLocation iconMap) {
        super(id, posX, posY, width, height, text);
        this.normalIcon = normalIcon;
        this.hoverIcon = hoverIcon;
        this.disabledIcon = disabledIcon;
        this.iconMap = iconMap;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            FontRenderer font = mc.fontRendererObj;
            mc.getTextureManager().bindTexture(GuiButton.buttonTextures);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int offsetV = this.getHoverState(this.hovered);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46 + offsetV * 20, this.width / 2, this.height);
            this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + offsetV * 20, this.width / 2, this.height);
            this.mouseDragged(mc, mouseX, mouseY);
            int textColor = 0xE0E0E0;

            IconHolder icon = this.normalIcon;

            if (this.packedFGColour != 0) {
                textColor = this.packedFGColour;
            } else if (!this.enabled) {
                textColor = 0xA0A0A0;
                icon = this.disabledIcon;
            } else if (this.hovered) {
                textColor = 0xFFFFA0;
                icon = this.hoverIcon;
            }

            int iconX = this.width / 2 - 8;
            int iconY = this.height / 2 - 8;

            if (this.displayString != null && !this.displayString.isEmpty()) {
                this.drawCenteredString(font, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, textColor);
                iconX -= font.getStringWidth(this.displayString);
            }

            if (icon != null && this.iconMap != null) {
                icon.bind();
                GlStateManager.color(1.0F, 1.0F, 1.0F);
                RenderHelper.bindTexture(this.iconMap);
                GuiHelper.drawTexturedModalRect(iconX + this.xPosition, iconY + this.yPosition, 16, 16, 0.0F, icon.getMinU(), icon.getMinV(), icon.getMaxU(), icon.getMaxV());
            }
        }
    }
}

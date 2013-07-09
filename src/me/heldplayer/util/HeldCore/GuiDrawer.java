
package me.heldplayer.util.HeldCore;

import java.util.EnumSet;

import me.heldplayer.util.HeldCore.client.MineHelp;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Class that draws the notification box for when any mod using HeldCore is
 * updated
 * 
 * @author heldplayer
 * 
 */
@SideOnly(Side.CLIENT)
public class GuiDrawer extends Gui implements ITickHandler {

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData) {}

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData) {
        GuiScreen screen = MineHelp.getMinecraft().currentScreen;

        if (!(screen instanceof GuiMainMenu)) {
            return;
        }

        if (Updater.outOfDateList.isEmpty() || Updater.hide) {
            return;
        }

        FontRenderer font = MineHelp.getFontRenderer();

        String str1 = Updater.notice;
        String str2 = Updater.outOfDateList;

        int width1 = font.getStringWidth(str1);
        int width2 = font.getStringWidth(str2);
        int width = width1 > width2 ? width1 : width2;

        int startX = (screen.width - width) / 2;
        int startY = screen.height / 4 + 24;
        int endX = startX + width;
        int endY = startY + 24;

        drawRect(startX - 2, startY - 2, endX + 2, endY - 1, 0x55200000);
        font.drawStringWithShadow(str1, (screen.width - width1) / 2, startY, 0xFFFFFFFF);
        font.drawStringWithShadow(str2, (screen.width - width2) / 2, startY + 12, 0xFFFFFFFF);
    }

    @Override
    public EnumSet<TickType> ticks() {
        return EnumSet.of(TickType.RENDER);
    }

    @Override
    public String getLabel() {
        return "HeldCore Updates Overlay";
    }

}

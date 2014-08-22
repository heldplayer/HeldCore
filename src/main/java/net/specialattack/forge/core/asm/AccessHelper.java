package net.specialattack.forge.core.asm;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.server.MinecraftServer;

import java.io.File;

public final class AccessHelper {

    public static int getGuiLeft(GuiContainer gui) {
        return gui.guiLeft;
    }

    public static int getGuiTop(GuiContainer gui) {
        return gui.guiTop;
    }

    public static File getAnvilFile(MinecraftServer server) {
        return server.anvilFile;
    }

}

package net.specialattack.forge.core.client.gui;

import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import net.minecraft.client.gui.GuiScreen;
import net.specialattack.forge.core.Objects;
import net.specialattack.forge.core.SpACore;

@SideOnly(Side.CLIENT)
public class GuiConfiguration extends GuiConfig {

    public GuiConfiguration(GuiScreen parent) {
        super(parent, new ArrayList<IConfigElement>(SpACore.configManager.categories.values()), Objects.MOD_ID, false, false, "SpACore Configuration");
    }
}

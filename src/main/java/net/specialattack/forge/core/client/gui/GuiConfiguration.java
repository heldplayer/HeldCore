package net.specialattack.forge.core.client.gui;

import java.util.ArrayList;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.specialattack.forge.core.Objects;
import net.specialattack.forge.core.SpACore;

@SideOnly(Side.CLIENT)
public class GuiConfiguration extends GuiConfig {

    public GuiConfiguration(GuiScreen parent) {
        super(parent, new ArrayList<IConfigElement>(SpACore.configManager.categories.values()), Objects.MOD_ID, false, false, "SpACore Configuration");
    }
}

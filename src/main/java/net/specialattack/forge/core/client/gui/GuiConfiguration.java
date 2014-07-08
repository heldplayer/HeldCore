package net.specialattack.forge.core.client.gui;

import cpw.mods.fml.client.config.GuiConfig;
import net.minecraft.client.gui.GuiScreen;
import net.specialattack.forge.core.Objects;
import net.specialattack.forge.core.SpACore;

public class GuiConfiguration extends GuiConfig {

    public GuiConfiguration(GuiScreen parent) {
        super(parent, SpACore.instance.config.getConfigElements(), Objects.MOD_ID, false, false, "SpACore Configuration");
    }

}

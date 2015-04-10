package net.specialattack.forge.core.client.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.specialattack.forge.core.Objects;
import net.specialattack.forge.core.SpACore;

@SideOnly(Side.CLIENT)
public class GuiConfiguration extends GuiConfig {

    public GuiConfiguration(GuiScreen parent) {
        super(parent, SpACore.instance.config.getConfigElements(), Objects.MOD_ID, false, false, "SpACore Configuration");
    }

}

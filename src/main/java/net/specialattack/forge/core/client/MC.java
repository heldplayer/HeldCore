package net.specialattack.forge.core.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.settings.GameSettings;

@SideOnly(Side.CLIENT)
public final class MC {

    private static Minecraft mc;

    public static EffectRenderer getEffectRenderer() {
        return MC.getMinecraft().effectRenderer;
    }

    public static Minecraft getMinecraft() {
        if (MC.mc == null) {
            MC.mc = Minecraft.getMinecraft();
        }
        return MC.mc;
    }

    public static EntityRenderer getEntityRenderer() {
        return MC.getMinecraft().entityRenderer;
    }

    public static FontRenderer getFontRenderer() {
        return MC.getMinecraft().fontRenderer;
    }

    public static GameSettings getGameSettings() {
        return MC.getMinecraft().gameSettings;
    }

    public static TextureManager getRenderEngine() {
        return MC.getMinecraft().renderEngine;
    }

    public static RenderGlobal getRenderGlobal() {
        return MC.getMinecraft().renderGlobal;
    }

    public static EntityClientPlayerMP getPlayer() {
        return MC.getMinecraft().thePlayer;
    }

    public static WorldClient getWorld() {
        return MC.getMinecraft().theWorld;
    }

    public static IResourceManager getResourceManager() {
        return MC.getMinecraft().getResourceManager();
    }

}

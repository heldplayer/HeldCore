package net.specialattack.forge.core.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.settings.GameSettings;

public final class MC {

    private static Minecraft mc;

    public static EffectRenderer getEffectRenderer() {
        return MC.getMc().effectRenderer;
    }

    public static Minecraft getMc() {
        if (MC.mc == null) {
            MC.mc = Minecraft.getMinecraft();
        }
        return MC.mc;
    }

    public static EntityRenderer getEntityRenderer() {
        return MC.getMc().entityRenderer;
    }

    public static FontRenderer getFontRenderer() {
        return MC.getMc().fontRenderer;
    }

    public static GameSettings getGameSettings() {
        return MC.getMc().gameSettings;
    }

    public static TextureManager getTextureManager() {
        return MC.getMc().renderEngine;
    }

    public static RenderGlobal getRenderGlobal() {
        return MC.getMc().renderGlobal;
    }

    public static EntityClientPlayerMP getPlayer() {
        return MC.getMc().thePlayer;
    }

    public static WorldClient getWorld() {
        return MC.getMc().theWorld;
    }

    public static IReloadableResourceManager getResourceManager() {
        return (IReloadableResourceManager) MC.getMc().getResourceManager();
    }

    public static SoundHandler getSoundHandler() {
        return MC.getMc().getSoundHandler();
    }

}

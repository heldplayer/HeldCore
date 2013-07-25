
package me.heldplayer.util.HeldCore.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.settings.GameSettings;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class MC {

    public static Minecraft getMinecraft() {
        return Minecraft.getMinecraft();
    }

    public static EffectRenderer getEffectRenderer() {
        return Minecraft.getMinecraft().effectRenderer;
    }

    public static EntityRenderer getEntityRenderer() {
        return Minecraft.getMinecraft().entityRenderer;
    }

    public static FontRenderer getFontRenderer() {
        return Minecraft.getMinecraft().fontRenderer;
    }

    public static GameSettings getGameSettings() {
        return Minecraft.getMinecraft().gameSettings;
    }

    public static TextureManager getRenderEngine() {
        return Minecraft.getMinecraft().renderEngine;
    }

    public static RenderGlobal getRenderGlobal() {
        return Minecraft.getMinecraft().renderGlobal;
    }

    public static SoundManager getSoundManager() {
        return Minecraft.getMinecraft().sndManager;
    }

    public static EntityClientPlayerMP getPlayer() {
        return Minecraft.getMinecraft().thePlayer;
    }

    public static WorldClient getWorld() {
        return Minecraft.getMinecraft().theWorld;
    }

}

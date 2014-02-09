
package me.heldplayer.util.HeldCore.client;

import net.minecraft.client.Minecraft;
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

    private static Minecraft mc;

    public static Minecraft getMinecraft() {
        if (mc == null) {
            mc = Minecraft.getMinecraft();
        }
        return mc;
    }

    public static EffectRenderer getEffectRenderer() {
        return getMinecraft().effectRenderer;
    }

    public static EntityRenderer getEntityRenderer() {
        return getMinecraft().entityRenderer;
    }

    public static FontRenderer getFontRenderer() {
        return getMinecraft().fontRenderer;
    }

    public static GameSettings getGameSettings() {
        return getMinecraft().gameSettings;
    }

    public static TextureManager getRenderEngine() {
        return getMinecraft().renderEngine;
    }

    public static RenderGlobal getRenderGlobal() {
        return getMinecraft().renderGlobal;
    }

    public static EntityClientPlayerMP getPlayer() {
        return getMinecraft().thePlayer;
    }

    public static WorldClient getWorld() {
        return getMinecraft().theWorld;
    }

}

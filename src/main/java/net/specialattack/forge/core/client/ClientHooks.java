package net.specialattack.forge.core.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.OpenGlHelper;

@SideOnly(Side.CLIENT)
public final class ClientHooks {

    private ClientHooks() {
    }

    public static void clientLoadWorld(WorldClient world) {
        ClientProxy.syncClientInstance.worldChanged(world);
    }

    public static void framebufferRenderPre() {
        if (ClientDebug.colorBlindEnabled && OpenGlHelper.shadersSupported && OpenGlHelper.framebufferSupported) {
            ClientProxy.colorBlindShader.getShader().bind();
        }
    }

    public static void framebufferRenderPost() {
        if (ClientDebug.colorBlindEnabled && OpenGlHelper.shadersSupported && OpenGlHelper.framebufferSupported) {
            ClientProxy.colorBlindShader.getShader().unbind();
        }
    }
}


package me.heldplayer.util.HeldCore.client;

import me.heldplayer.util.HeldCore.Assets;
import me.heldplayer.util.HeldCore.CommonProxy;
import me.heldplayer.util.HeldCore.HeldCore;
import me.heldplayer.util.HeldCore.sync.SyncHandler;
import me.heldplayer.util.HeldCore.sync.packet.Packet6SetInterval;
import net.minecraft.client.renderer.texture.TextureMap;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

        MC.getRenderEngine().loadTextureMap(Assets.TEXTURE_MAP, new TextureMap(HeldCore.textureMapId.getValue(), "textures/heldcore/"));
    }

    @SubscribeEvent
    public void onClientConnectedToServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        HeldCore.packetHandler.sendPacketToServer(new Packet6SetInterval(Integer.valueOf(HeldCore.refreshRate.getValue())));
    }

    @SubscribeEvent
    public void onClientDisconnectionFromServer(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        SyncHandler.clientSyncables.clear();
    }

}

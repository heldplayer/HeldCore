
package net.specialattack.forge.core.client;

import java.util.Iterator;
import java.util.Map;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkPosition;
import net.minecraftforge.event.world.ChunkEvent;
import net.specialattack.forge.core.Assets;
import net.specialattack.forge.core.CommonProxy;
import net.specialattack.forge.core.SpACore;
import net.specialattack.forge.core.sync.ISyncableObjectOwner;
import net.specialattack.forge.core.sync.SyncHandler;
import net.specialattack.forge.core.sync.packet.Packet1TrackingStatus;
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

        MC.getRenderEngine().loadTextureMap(Assets.TEXTURE_MAP, new TextureMap(SpACore.textureMapId.getValue(), "textures/spacore/"));
    }

    @SubscribeEvent
    public void onClientConnectedToServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        SyncHandler.initializationCounter = 20;
    }

    @SubscribeEvent
    public void onClientDisconnectionFromServer(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        SyncHandler.clientSyncables.clear();
    }

    @SubscribeEvent
    public void onChunkUnload(ChunkEvent.Unload event) {
        if (event.world.isRemote) {
            @SuppressWarnings("unchecked")
            Map<ChunkPosition, TileEntity> tiles = event.getChunk().chunkTileEntityMap;
            Iterator<TileEntity> iterator = tiles.values().iterator();

            while (iterator.hasNext()) {
                TileEntity tile = iterator.next();

                if (tile instanceof ISyncableObjectOwner) {
                    SpACore.packetHandler.sendPacketToServer(new Packet1TrackingStatus((ISyncableObjectOwner) tile, false));
                }
            }
        }
    }

}

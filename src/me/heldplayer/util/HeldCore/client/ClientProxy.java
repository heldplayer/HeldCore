
package me.heldplayer.util.HeldCore.client;

import me.heldplayer.util.HeldCore.Assets;
import me.heldplayer.util.HeldCore.CommonProxy;
import me.heldplayer.util.HeldCore.HeldCore;
import me.heldplayer.util.HeldCore.sync.SyncHandler;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.network.INetworkManager;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

        MC.getRenderEngine().loadTextureMap(Assets.TEXTURE_MAP, new TextureMap(HeldCore.textureMapId.getValue(), "textures/heldcore/"));
    }

    @Override
    public void connectionClosed(INetworkManager manager) {
        super.connectionClosed(manager);

        SyncHandler.clientSyncables.clear();
    }

}

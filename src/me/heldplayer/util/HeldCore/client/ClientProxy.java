
package me.heldplayer.util.HeldCore.client;

import me.heldplayer.util.HeldCore.CommonProxy;
import me.heldplayer.util.HeldCore.sync.SyncHandler;
import net.minecraft.network.INetworkManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void connectionClosed(INetworkManager manager) {
        super.connectionClosed(manager);

        SyncHandler.clientSyncables.clear();
    }

}

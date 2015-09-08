package net.specialattack.forge.core.sync;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.tileentity.TileEntity;
import net.specialattack.forge.core.CommonProxy;
import net.specialattack.forge.core.client.MC;

public abstract class SyncTileEntity extends TileEntity implements ISyncableOwner {

    public SyncTrackingStorage tracker;
    protected Map<String, ISyncable> syncables = new HashMap<String, ISyncable>();
    private boolean initialized;
    private UUID uuid;

    @Override
    public Map<String, ISyncable> getSyncables() {
        return this.syncables;
    }

    @Override
    public final void register(SyncTrackingStorage tracker) {
        this.tracker = tracker;
    }

    @Override
    public final TileEntitySyncObjectProvider getProvider() {
        return CommonProxy.tileEntityProvider;
    }

    @Override
    public final void updateEntity() {
        super.updateEntity();

        if (this.worldObj != null) { // We only work if the world is set
            if (!this.initialized) { // If we're not initialized, initialize
                if (this.worldObj.isRemote) { // Only on the client will we send a track request packet
                    SyncHandlerClient.requestStartTracking(this);
                } else { // Only on the server will we set the UUID
                    this.uuid = UUID.randomUUID();
                }
                this.initialized = true; // Disable the check either way
            }

            this.onTick();
        }
    }

    @Override
    public final UUID getSyncUUID() {
        return this.uuid;
    }

    @Override
    public final void setSyncUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public void onTick() {
    }

    @Override
    public final boolean canUpdate() {
        return true;
    }

    @Override
    public boolean canStartTracking() {
        return SyncHandlerClient.worldStorage.dimId == MC.getWorld().provider.dimensionId;
    }
}

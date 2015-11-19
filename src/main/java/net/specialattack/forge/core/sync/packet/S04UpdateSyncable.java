package net.specialattack.forge.core.sync.packet;

import io.netty.buffer.ByteBuf;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.specialattack.forge.core.sync.*;
import net.specialattack.util.NetworkUtils;
import org.apache.logging.log4j.Level;

public class S04UpdateSyncable extends SyncPacket {

    /*
     * Format of the tag:
     * "data" : [
     *   {
     *     "storage" : "00000000-0000-0000-0000-000000000000",
     *     "uuid" : "00000000-0000-0000-0000-000000000000",
     *     "data" : { // Contains all the updates
     *       "key" : "entry",
     *       ...
     *     }
     *   },
     *   ...
     * ]
     */
    private NBTTagCompound tag;

    public S04UpdateSyncable() {
    }

    public S04UpdateSyncable(NBTTagCompound tag) {
        this.tag = tag;
    }

    @Override
    public Side getSendingSide() {
        return Side.SERVER;
    }

    @Override
    public Side getReceivingSide() {
        return Side.CLIENT;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.tag = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, this.tag);
    }

    @Override
    public void handle(MessageContext ctx, EntityPlayer player) {
        NBTTagList list = this.tag.getTagList("data", NetworkUtils.TAG_COMPOUND);
        SyncHandlerClient.debug("Received syncing updates for %d owners", list.tagCount());
        for (int i = 0; i < list.tagCount(); i++) {
            S04UpdateSyncable.readCompound(list.getCompoundTagAt(i));
        }
    }

    @SuppressWarnings("unchecked")
    public static void readCompound(NBTTagCompound tag) {
        UUID storageId = UUID.fromString(tag.getString("storage"));
        UUID uuid = UUID.fromString(tag.getString("uuid"));
        SyncTrackingStorage storage = SyncHandlerClient.getStorage(storageId);

        if (storage == null) {
            SyncHandlerClient.log.log(Level.WARN, String.format("Problematic Sync update, unknown tracking storage %s for %s", storageId, uuid));
            return;
        }

        ISyncableOwner owner = storage.getTrackedOwner(uuid);
        if (owner == null) {
            SyncHandlerClient.log.log(Level.WARN, String.format("Problematic Sync update, could not find owner %s", uuid));
            return;
        }
        Map<String, ISyncable> syncables = owner.getSyncables();

        SyncHandlerClient.debug("Updating data for %s (%s)", owner.getDebugDisplay(), owner.getSyncUUID());

        NBTTagCompound data = tag.getCompoundTag("data");
        for (String key : (Set<String>) data.getKeySet()) {
            if (syncables.containsKey(key)) {
                ISyncable syncable = syncables.get(key);
                syncable.changed();
                syncable.read(data.getTag(key));
                owner.syncableChanged(syncable);
                SyncHandlerClient.debug("Got data for tag %s", key);
            } else {
                SyncHandlerClient.debug("Got data for tag %s but it was not present", key);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static NBTTagCompound writeCompound(SyncTrackingStorage storage, ISyncableOwner owner, Map<String, ISyncable> syncables) {
        NBTTagCompound tag = new NBTTagCompound();
        SyncObjectProvider provider = owner.getProvider();

        tag.setString("storage", storage.uuid.toString());
        tag.setString("provider", provider.id);
        tag.setString("uuid", owner.getSyncUUID().toString());

        NBTTagCompound data = new NBTTagCompound();
        for (Map.Entry<String, ISyncable> entry : syncables.entrySet()) {
            data.setTag(entry.getKey(), entry.getValue().write());
        }
        tag.setTag("data", data);

        return tag;
    }
}

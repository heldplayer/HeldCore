package net.specialattack.forge.core.sync.packet;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.specialattack.forge.core.sync.*;
import net.specialattack.util.NetworkUtils;
import org.apache.logging.log4j.Level;

public class S03StartSyncing extends SyncPacket {

    /*
     * Format of the tag:
     * "data" : [
     *   {
     *     "storage" : "00000000-0000-0000-0000-000000000000",
     *     "provider" : "provider",
     *     "uuid" : "00000000-0000-0000-0000-000000000000",
     *     "track" : true/false,
     *     "descriptor" : null, // Present if track is true, used by the provider to identify the syncable
     *     "data" : { // Present if track is true, contains data to start tracking
     *       "key" : "entry",
     *       ...
     *     }
     *   },
     *   ...
     * ]
     */
    private NBTTagCompound tag;

    public S03StartSyncing() {
    }

    public S03StartSyncing(NBTTagCompound tag) {
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
        SyncHandlerClient.debug("Received tracking updates for %d syncables", list.tagCount());
        for (int i = 0; i < list.tagCount(); i++) {
            S03StartSyncing.readCompound(list.getCompoundTagAt(i));
        }
    }

    @SuppressWarnings("unchecked")
    public static void readCompound(NBTTagCompound tag) {
        UUID storageId = UUID.fromString(tag.getString("storage"));
        String providerName = tag.getString("provider");
        UUID uuid = UUID.fromString(tag.getString("uuid"));
        SyncObjectProvider<ISyncableOwner> provider = SyncServerAPI.getProvider(providerName);
        SyncTrackingStorage storage = SyncHandlerClient.getStorage(storageId);

        if (storage == null) {
            SyncHandlerClient.log.log(Level.WARN, String.format("Problematic Sync request, unknown tracking storage %s for %s", storageId, uuid));
            return;
        }

        boolean track = tag.getBoolean("track");
        if (track) {
            ISyncableOwner owner = provider.readDescriptorClient(tag.getCompoundTag("descriptor"));
            if (owner == null) {
                SyncHandlerClient.log.log(Level.WARN, String.format("Problematic Sync request, could not find owner %s", uuid));
                return;
            }
            owner.setSyncUUID(uuid);
            Map<String, ISyncable> syncables = owner.getSyncables();

            SyncHandlerClient.debug("Starting to track owner %s (%s)", owner.getDebugDisplay(), owner.getSyncUUID());
            SyncHandlerClient.debug("Owner syncables: %s", syncables.toString());

            NBTTagCompound data = tag.getCompoundTag("data");
            for (String key : (Set<String>) data.func_150296_c()) {
                if (syncables.containsKey(key)) {
                    syncables.get(key).read(data.getTag(key));
                    SyncHandlerClient.debug("Got data for tag %s", key);
                } else {
                    SyncHandlerClient.debug("Got data for tag %s but it was not present", key);
                }
            }
            owner.register(storage);
            storage.globalSyncableOwners.add(owner);
            storage.globalSyncables.addAll(syncables.values());
        } else {
            Iterator<ISyncableOwner> it = storage.globalSyncableOwners.iterator();
            while (it.hasNext()) {
                ISyncableOwner owner = it.next();
                if (owner.getSyncUUID().equals(uuid)) {
                    it.remove();
                    owner.register(null);
                    SyncHandlerClient.debug("Untracked owner %s (%s)", owner.getDebugDisplay(), uuid);
                }
            }

            Iterator<ISyncable> it2 = storage.globalSyncables.iterator();
            while (it2.hasNext()) {
                ISyncable syncable = it2.next();
                if (syncable.getOwner().getSyncUUID().equals(uuid)) {
                    it2.remove();
                    SyncHandlerClient.debug("Untracked syncable %s (owned by %s)", syncable.getDebugDisplay(), uuid);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static NBTTagCompound writeCompound(SyncTrackingStorage storage, ISyncableOwner owner, boolean track) {
        NBTTagCompound tag = new NBTTagCompound();
        SyncObjectProvider provider = owner.getProvider();

        tag.setString("storage", storage.uuid.toString());
        tag.setString("provider", provider.id);
        tag.setString("uuid", owner.getSyncUUID().toString());
        tag.setBoolean("track", track);
        if (track) {
            tag.setTag("descriptor", provider.writeDescriptorClient(owner));

            NBTTagCompound data = new NBTTagCompound();
            for (Map.Entry<String, ISyncable> entry : owner.getSyncables().entrySet()) {
                data.setTag(entry.getKey(), entry.getValue().write());
            }
            tag.setTag("data", data);
        }
        return tag;
    }
}

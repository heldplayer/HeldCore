package net.specialattack.forge.core.sync.packet;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.specialattack.forge.core.sync.*;
import net.specialattack.util.NetworkUtils;
import org.apache.logging.log4j.Level;

public class C02RequestSync extends SyncPacket {

    /*
     * Format of the tag:
     * "data" : [
     *   {
     *     "storage" : "00000000-0000-0000-0000-000000000000",
     *     "provider" : "provider",
     *     "track" : true/false,
     *     "uuid" : "00000000-0000-0000-0000-000000000000", // Present if track is false
     *     "descriptor" : null // Present if track is true, used by the provider to identify the syncable
     *   },
     *   ...
     * ]
     */
    public NBTTagCompound tag;

    public C02RequestSync() {
    }

    public C02RequestSync(NBTTagCompound tag) {
        this.tag = tag;
    }

    @Override
    public Side getSendingSide() {
        return Side.CLIENT;
    }

    @Override
    public Side getReceivingSide() {
        return Side.SERVER;
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
        SyncHandler.debug("Received tracking request for %d syncables from %s", list.tagCount(), player.getUniqueID());
        for (int i = 0; i < list.tagCount(); i++) {
            C02RequestSync.readCompound(list.getCompoundTagAt(i), player.getUniqueID());
        }
    }

    @SuppressWarnings("unchecked")
    public static void readCompound(NBTTagCompound tag, UUID playerUUID) {
        UUID storageId = UUID.fromString(tag.getString("storage"));
        String providerName = tag.getString("provider");
        SyncObjectProvider<ISyncableOwner> provider = SyncServerAPI.getProvider(providerName);
        SyncTrackingStorage storage = SyncHandler.syncStorages.get(storageId);

        if (storage == null) {
            SyncHandler.log.log(Level.WARN, String.format("Problematic tracking request, unknown tracking storage %s", storageId));
            return;
        }

        boolean track = tag.getBoolean("track");
        if (track) {
            ISyncableOwner owner = provider.readDescriptorServer(tag.getCompoundTag("descriptor"), storage);
            if (owner == null) {
                return;
            }
            PlayerTracker playerTracker = storage.getPlayerTracker(playerUUID);
            if (playerTracker == null) {
                SyncHandler.log.log(Level.WARN, String.format("Problematic tracking request, %s does not track storage %s", playerUUID, storageId));
                return;
            }
            playerTracker.attemptTrack(owner);
        } else {
            UUID uuid = UUID.fromString(tag.getString("uuid"));
            PlayerTracker playerTracker = storage.getPlayerTracker(playerUUID);
            if (playerTracker == null) {
                SyncHandler.log.log(Level.WARN, String.format("Problematic tracking request, %s does not track storage %s", playerUUID, storageId));
                return;
            }
            playerTracker.untrack(uuid);
        }
    }

    @SuppressWarnings("unchecked")
    public static NBTTagCompound writeCompound(UUID storageId, ISyncableOwner owner, boolean track) {
        NBTTagCompound tag = new NBTTagCompound();
        SyncObjectProvider provider = owner.getProvider();

        tag.setString("storage", storageId.toString());
        tag.setString("provider", provider.id);
        tag.setBoolean("track", track);
        if (track) {
            tag.setTag("descriptor", provider.writeDescriptorServer(owner));
        } else {
            tag.setString("uuid", owner.getSyncUUID().toString());
        }
        return tag;
    }
}

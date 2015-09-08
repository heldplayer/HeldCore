package net.specialattack.forge.core.sync;

import java.util.Map;
import java.util.UUID;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.specialattack.forge.core.client.MC;

public class TileEntitySyncObjectProvider extends SyncObjectProvider<SyncTileEntity> {

    public TileEntitySyncObjectProvider() {
        super("spacore:tile_entity");
    }

    @Override
    public NBTTagCompound writeDescriptor(SyncTileEntity owner) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("posX", owner.xCoord);
        tag.setInteger("posY", owner.yCoord);
        tag.setInteger("posZ", owner.zCoord);
        tag.setString("uuid", owner.getSyncUUID().toString());
        NBTTagCompound data = new NBTTagCompound();
        for (Map.Entry<String, ISyncable> entry : owner.getSyncables().entrySet()) {
            data.setTag(entry.getKey(), entry.getValue().write());
        }
        tag.setTag("data", data);
        return tag;
    }

    @Override
    public SyncTileEntity readDescriptor(NBTTagCompound tag) {
        int posX = tag.getInteger("posX");
        int posY = tag.getInteger("posY");
        int posZ = tag.getInteger("posZ");
        UUID uuid = UUID.fromString(tag.getString("uuid"));
        TileEntity tileEntity = MC.getWorld().getTileEntity(posX, posY, posZ);
        if (tileEntity != null && tileEntity instanceof SyncTileEntity) {
            return (SyncTileEntity) tileEntity;
        }
        return null;
    }
}

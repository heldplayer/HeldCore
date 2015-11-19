package net.specialattack.forge.core.sync;

import java.util.UUID;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.specialattack.forge.core.client.MC;

public class TileEntitySyncObjectProvider extends SyncObjectProvider<SyncTileEntity> {

    public TileEntitySyncObjectProvider() {
        super("spacore:tile_entity");
    }

    @Override
    public NBTTagCompound writeDescriptorClient(SyncTileEntity owner) {
        NBTTagCompound tag = new NBTTagCompound();
        BlockPos pos = owner.getPos();
        tag.setInteger("posX", pos.getX());
        tag.setInteger("posY", pos.getY());
        tag.setInteger("posZ", pos.getZ());
        tag.setString("uuid", owner.getSyncUUID().toString());
        return tag;
    }

    @Override
    public SyncTileEntity readDescriptorClient(NBTTagCompound tag) {
        int posX = tag.getInteger("posX");
        int posY = tag.getInteger("posY");
        int posZ = tag.getInteger("posZ");
        UUID uuid = UUID.fromString(tag.getString("uuid"));
        TileEntity tileEntity = MC.getWorld().getTileEntity(new BlockPos(posX, posY, posZ));
        if (tileEntity != null && tileEntity instanceof SyncTileEntity) {
            return (SyncTileEntity) tileEntity;
        }
        return null;
    }

    @Override
    public NBTTagCompound writeDescriptorServer(SyncTileEntity owner) {
        NBTTagCompound tag = new NBTTagCompound();
        BlockPos pos = owner.getPos();
        tag.setInteger("posX", pos.getX());
        tag.setInteger("posY", pos.getY());
        tag.setInteger("posZ", pos.getZ());
        return tag;
    }

    @Override
    public SyncTileEntity readDescriptorServer(NBTTagCompound tag, SyncTrackingStorage storage) {
        if (storage instanceof SyncWorldTrackingStorage) {
            World world = ((SyncWorldTrackingStorage) storage).world;
            if (world == null) {
                return null;
            }
            int posX = tag.getInteger("posX");
            int posY = tag.getInteger("posY");
            int posZ = tag.getInteger("posZ");
            TileEntity tile = world.getTileEntity(new BlockPos(posX, posY, posZ));
            if (tile instanceof SyncTileEntity) {
                return (SyncTileEntity) tile;
            } else if (tile != null) {
                throw new IllegalStateException(String.format("Unexpected lack of SyncTileEntity at (%d, %d, %d), found %s", posX, posY, posZ, tile.toString()));
            }
        }
        return null;
    }
}

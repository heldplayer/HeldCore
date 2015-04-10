package net.specialattack.forge.core.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.DimensionManager;

public final class BlockPosition {

    public final World world;
    public final int x, y, z;

    public BlockPosition(World world, int x, int y, int z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static void writeBlockPosition(ByteBuf out, BlockPosition position) {
        out.writeInt(position.world.provider.getDimensionId());
        out.writeInt(position.x);
        out.writeInt(position.y);
        out.writeInt(position.z);
    }

    public static BlockPosition readBlockPosition(ByteBuf in) {
        int dimId = in.readInt();
        int x = in.readInt();
        int y = in.readInt();
        int z = in.readInt();
        WorldProvider provider = DimensionManager.getProvider(dimId);
        return new BlockPosition(provider == null ? null : provider.worldObj, x, y, z);
    }

}

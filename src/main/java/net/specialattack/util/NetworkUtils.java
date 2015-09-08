package net.specialattack.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagIntArray;

public class NetworkUtils {

    public static final byte TAG_END = 0;
    public static final byte TAG_BYTE = 1;
    public static final byte TAG_SHORT = 2;
    public static final byte TAG_INT = 3;
    public static final byte TAG_LONG = 4;
    public static final byte TAG_FLOAT = 5;
    public static final byte TAG_DOUBLE = 6;
    public static final byte TAG_BYTE_ARRAY = 7;
    public static final byte TAG_STRING = 8;
    public static final byte TAG_LIST = 9;
    public static final byte TAG_COMPOUND = 10;
    public static final byte TAG_INT_ARRAY = 11;

    public static NBTTagIntArray writeBlockPos(BlockPos pos) {
        return new NBTTagIntArray(new int[] { pos.x, pos.y, pos.z });
    }

    public static BlockPos readBlockPos(NBTTagIntArray tag) {
        int[] pos = tag.func_150302_c();
        return new BlockPos(pos[0], pos[1], pos[2]);
    }

    public static void writeBlockPos(ByteBuf out, BlockPos pos) {
        out.writeInt(pos.x);
        out.writeInt(pos.y);
        out.writeInt(pos.z);
    }

    public static BlockPos readBlockPos(ByteBuf in) {
        return new BlockPos(in.readInt(), in.readInt(), in.readInt());
    }

}

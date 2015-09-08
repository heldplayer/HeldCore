package net.specialattack.forge.core.sync.packet;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class C02RequestSync extends SyncPacket {

    public NBTTagCompound data;

    public C02RequestSync() {
    }

    public C02RequestSync(NBTTagCompound data) {
        this.data = data;
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
        this.data = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, this.data);
    }

    @Override
    public void handle(MessageContext ctx, EntityPlayer player) {
        // TODO
    }
}

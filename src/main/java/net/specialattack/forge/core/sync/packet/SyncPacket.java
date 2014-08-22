package net.specialattack.forge.core.sync.packet;

import net.minecraft.world.World;
import net.specialattack.forge.core.packet.SpACorePacket;

public abstract class SyncPacket extends SpACorePacket {

    public SyncPacket(World world) {
        super(world);
    }

}

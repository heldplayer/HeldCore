package net.specialattack.forge.core.event;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class WorldChangedEvent extends Event {

    public final WorldClient world;

    public WorldChangedEvent(WorldClient world) {
        this.world = world;
    }
}

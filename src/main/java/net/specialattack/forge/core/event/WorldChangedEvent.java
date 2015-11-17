package net.specialattack.forge.core.event;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.multiplayer.WorldClient;

@SideOnly(Side.CLIENT)
public class WorldChangedEvent extends Event {

    public final WorldClient world;

    public WorldChangedEvent(WorldClient world) {
        this.world = world;
    }
}

package net.specialattack.forge.core.sync;

import cpw.mods.fml.relauncher.Side;
import java.util.Iterator;
import java.util.UUID;
import net.minecraft.world.World;

public class SyncWorldTrackingStorage extends SyncTrackingStorage {

    /**
     * The dimension ID of this storage
     */
    public int dimId;
    /**
     * A reference to to world object this storage belongs to, null on the server
     */
    public World world;

    public SyncWorldTrackingStorage(int dimId, World world, Side side) {
        this(dimId, world, side, UUID.randomUUID());
    }

    public SyncWorldTrackingStorage(int dimId, World world, Side side, UUID uuid) {
        super("World Tracker " + dimId, side, uuid);
        this.setRole(SyncRole.ROLE_WORLD);
        this.dimId = dimId;
        this.world = world;
    }

    @Override
    public void releaseData() {
        super.releaseData();
        this.world = null;
    }

}

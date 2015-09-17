package net.specialattack.forge.core.sync;

import net.minecraft.nbt.NBTTagCompound;

public abstract class SyncObjectProvider<T extends ISyncableOwner> {

    public final String id;

    public SyncObjectProvider(String id) {
        this.id = id;
    }

    public abstract NBTTagCompound writeDescriptorClient(T owner);

    public abstract T readDescriptorClient(NBTTagCompound tag);

    public abstract NBTTagCompound writeDescriptorServer(T owner);

    public abstract T readDescriptorServer(NBTTagCompound tag, SyncTrackingStorage storage);
}

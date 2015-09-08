package net.specialattack.forge.core.sync;

import net.minecraft.nbt.NBTTagCompound;

public abstract class SyncObjectProvider<T extends ISyncableOwner> {

    public final String id;

    public SyncObjectProvider(String id) {
        this.id = id;
    }

    public abstract NBTTagCompound writeDescriptor(T owner);

    public abstract T readDescriptor(NBTTagCompound tag);

}

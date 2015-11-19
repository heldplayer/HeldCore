package net.specialattack.forge.core.sync.object;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.specialattack.forge.core.sync.ISyncable;
import net.specialattack.forge.core.sync.ISyncableOwner;

public class SyncBool implements ISyncable {

    public boolean value, prevValue;
    public String name;

    private final ISyncableOwner owner;

    public SyncBool(boolean value, ISyncableOwner owner, String name) {
        this.value = this.prevValue = value;
        this.owner = owner;
        this.name = name;
    }

    @Override
    public NBTBase write() {
        return new NBTTagByte((byte) (this.value ? 1 : 0));
    }

    @Override
    public void read(NBTBase tag) {
        this.value = ((NBTTagByte) tag).getByte() != 0;
    }

    @Override
    public boolean changed() {
        boolean changed = this.prevValue != this.value;
        this.prevValue = this.value;
        return changed;
    }

    @Override
    public ISyncableOwner getOwner() {
        return this.owner;
    }

    @Override
    public String getDebugDisplay() {
        return String.format("%s: %s", this.name, this.value);
    }
}

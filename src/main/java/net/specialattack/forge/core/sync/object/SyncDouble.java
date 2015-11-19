package net.specialattack.forge.core.sync.object;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagDouble;
import net.specialattack.forge.core.sync.ISyncable;
import net.specialattack.forge.core.sync.ISyncableOwner;

public class SyncDouble implements ISyncable {

    public double value, prevValue;
    public String name;

    private final ISyncableOwner owner;

    public SyncDouble(double value, ISyncableOwner owner, String name) {
        this.value = this.prevValue = value;
        this.owner = owner;
        this.name = name;
    }

    @Override
    public NBTBase write() {
        return new NBTTagDouble(this.value);
    }

    @Override
    public void read(NBTBase tag) {
        this.value = ((NBTTagDouble) tag).getDouble();
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
        return String.format("%s: %fD", this.name, this.value);
    }
}

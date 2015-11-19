package net.specialattack.forge.core.sync.object;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagFloat;
import net.specialattack.forge.core.sync.ISyncable;
import net.specialattack.forge.core.sync.ISyncableOwner;

public class SyncFloat implements ISyncable {

    public float value, prevValue;
    public String name;

    private final ISyncableOwner owner;

    public SyncFloat(float value, ISyncableOwner owner, String name) {
        this.value = this.prevValue = value;
        this.owner = owner;
        this.name = name;
    }

    @Override
    public NBTBase write() {
        return new NBTTagFloat(this.value);
    }

    @Override
    public void read(NBTBase tag) {
        this.value = ((NBTTagFloat) tag).getFloat();
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
        return String.format("%s: %fF", this.name, this.value);
    }
}

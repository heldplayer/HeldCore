package net.specialattack.forge.core.sync.object;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagString;
import net.specialattack.forge.core.sync.ISyncable;
import net.specialattack.forge.core.sync.ISyncableOwner;

public class SyncString implements ISyncable {

    public String value, prevValue;
    public String name;

    private final ISyncableOwner owner;

    public SyncString(String value, ISyncableOwner owner, String name) {
        this.value = this.prevValue = value;
        this.owner = owner;
        this.name = name;
    }

    @Override
    public NBTBase write() {
        return new NBTTagString(this.value);
    }

    @Override
    public void read(NBTBase tag) {
        this.value = ((NBTTagString) tag).func_150285_a_();
    }

    @Override
    public boolean changed() {
        boolean changed = !this.prevValue.equals(this.value);
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

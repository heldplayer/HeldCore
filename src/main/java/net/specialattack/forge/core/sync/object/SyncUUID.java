package net.specialattack.forge.core.sync.object;

import java.util.UUID;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagString;
import net.specialattack.forge.core.sync.ISyncable;
import net.specialattack.forge.core.sync.ISyncableOwner;

public class SyncUUID implements ISyncable {

    public UUID value, prevValue;
    public String name;

    private final ISyncableOwner owner;

    public SyncUUID(UUID value, ISyncableOwner owner, String name) {
        this.value = this.prevValue = value;
        this.owner = owner;
        this.name = name;
    }

    @Override
    public NBTBase write() {
        return new NBTTagString(this.value.toString());
    }

    @Override
    public void read(NBTBase tag) {
        this.value = UUID.fromString(((NBTTagString) tag).func_150285_a_());
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

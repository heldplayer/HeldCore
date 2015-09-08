package net.specialattack.forge.core.sync;

import net.minecraft.nbt.NBTBase;

public interface ISyncable {

    NBTBase write();

    void read(NBTBase tag);

    /**
     * Checks to see if the syncable has changed value since the last check.
     * If it has, returns true and resets the checker.
     *
     * @return True if the syncable has a new value since the last check
     */
    boolean changed();

    /**
     * Used to get the owner of this syncable.
     * A syncable must have exactly 1 owner.
     *
     * @return The owner of this syncable, must not be null.
     */
    ISyncableOwner getOwner();

    /**
     * Used for debugging purposes
     *
     * @return A string representing this Syncable Owner
     */
    String getDebugDisplay();
}

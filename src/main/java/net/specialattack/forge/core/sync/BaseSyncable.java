
package net.specialattack.forge.core.sync;

import net.specialattack.forge.core.Objects;

public abstract class BaseSyncable implements ISyncable {

    private ISyncableObjectOwner owner;
    protected boolean hasChanged;
    protected int id;

    public BaseSyncable(ISyncableObjectOwner owner) {
        this.owner = owner;
        this.id = -1;
    }

    @Override
    public ISyncableObjectOwner getOwner() {
        return this.owner;
    }

    @Override
    public boolean hasChanged() {
        return this.hasChanged;
    }

    @Override
    public void setChanged(boolean value) {
        this.hasChanged = value;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        if (this.id == -1) {
            this.id = SyncHandler.lastSyncId++;
            Objects.log.info("Getting next ID (" + this.id + ") for " + this.toString()); // FIXME: debug code
        }
        return this.id;
    }

}

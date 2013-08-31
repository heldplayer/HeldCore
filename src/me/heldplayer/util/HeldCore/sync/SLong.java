
package me.heldplayer.util.HeldCore.sync;

import java.io.DataOutputStream;
import java.io.IOException;

import com.google.common.io.ByteArrayDataInput;

public class SLong implements ISyncable {

    private ISyncableObjectOwner owner;
    private long value;
    private boolean hasChanged;

    public SLong(ISyncableObjectOwner owner, long value) {
        this.owner = owner;
    }

    public void setValue(long value) {
        this.value = value;
        this.hasChanged = true;
    }

    public long getValue() {
        return this.value;
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
    public void setChanged(boolean changed) {
        this.hasChanged = changed;
    }

    @Override
    public void read(ByteArrayDataInput in) throws IOException {
        this.value = in.readLong();
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeLong(this.value);
    }

}

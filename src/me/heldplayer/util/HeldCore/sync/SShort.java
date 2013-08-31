
package me.heldplayer.util.HeldCore.sync;

import java.io.DataOutputStream;
import java.io.IOException;

import com.google.common.io.ByteArrayDataInput;

public class SShort implements ISyncable {

    private ISyncableObjectOwner owner;
    private short value;
    private boolean hasChanged;

    public SShort(ISyncableObjectOwner owner, short value) {
        this.owner = owner;
    }

    public void setValue(short value) {
        this.value = value;
        this.hasChanged = true;
    }

    public short getValue() {
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
        this.value = in.readShort();
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeShort(this.value);
    }

}

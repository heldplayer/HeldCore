
package me.heldplayer.util.HeldCore.sync;

import java.io.DataOutputStream;
import java.io.IOException;

import com.google.common.io.ByteArrayDataInput;

public class SInteger implements ISyncable {

    private ISyncableObjectOwner owner;
    private int value;
    private boolean hasChanged;

    public SInteger(ISyncableObjectOwner owner, int value) {
        this.owner = owner;
    }

    public void setValue(int value) {
        this.value = value;
        this.hasChanged = true;
    }

    public int getValue() {
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
        this.value = in.readInt();
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeInt(this.value);
    }

}

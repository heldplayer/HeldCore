
package me.heldplayer.util.HeldCore.sync;

import java.io.DataOutputStream;
import java.io.IOException;

import com.google.common.io.ByteArrayDataInput;

public class SDouble implements ISyncable {

    private ISyncableObjectOwner owner;
    private double value;
    private boolean hasChanged;

    public SDouble(ISyncableObjectOwner owner, double value) {
        this.owner = owner;
    }

    public void setValue(double value) {
        this.value = value;
        this.hasChanged = true;
    }

    public double getValue() {
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
        this.value = in.readDouble();
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeDouble(this.value);
    }

}

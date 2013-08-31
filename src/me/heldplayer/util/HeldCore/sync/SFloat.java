
package me.heldplayer.util.HeldCore.sync;

import java.io.DataOutputStream;
import java.io.IOException;

import com.google.common.io.ByteArrayDataInput;

public class SFloat implements ISyncable {

    private ISyncableObjectOwner owner;
    private float value;
    private boolean hasChanged;

    public SFloat(ISyncableObjectOwner owner, float value) {
        this.owner = owner;
    }

    public void setValue(float value) {
        this.value = value;
        this.hasChanged = true;
    }

    public float getValue() {
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
        this.value = in.readFloat();
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeFloat(this.value);
    }

}

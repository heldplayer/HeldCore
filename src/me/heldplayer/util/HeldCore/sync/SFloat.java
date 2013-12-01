
package me.heldplayer.util.HeldCore.sync;

import java.io.DataOutputStream;
import java.io.IOException;

import com.google.common.io.ByteArrayDataInput;

public class SFloat extends BaseSyncable {

    private float value;

    public SFloat(ISyncableObjectOwner owner, float value) {
        super(owner);
        this.value = value;
    }

    public SFloat(ISyncableObjectOwner owner) {
        super(owner);
    }

    public void setValue(float value) {
        this.value = value;
        this.hasChanged = true;
    }

    public float getValue() {
        return this.value;
    }

    @Override
    public void read(ByteArrayDataInput in) throws IOException {
        this.value = in.readFloat();
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeFloat(this.value);
    }

    @Override
    public String toString() {
        return "Float: " + this.value;
    }

}

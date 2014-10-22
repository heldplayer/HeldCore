package net.specialattack.forge.core.sync;

import com.google.common.io.ByteArrayDataInput;
import java.io.DataOutputStream;
import java.io.IOException;

public class SFloat extends BaseSyncable {

    private float value;

    public SFloat(ISyncableObjectOwner owner, float value) {
        super(owner);
        this.value = value;
    }

    public SFloat(ISyncableObjectOwner owner) {
        super(owner);
    }

    public float getValue() {
        return this.value;
    }

    public void setValue(float value) {
        if (this.value != value) {
            this.value = value;
            this.hasChanged = true;
        }
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
    public void setValue(Object obj) {
        if (obj instanceof Number) {
            this.setValue(((Number) obj).floatValue());
        }
    }

    @Override
    public String toString() {
        return "Float: " + this.value;
    }

}

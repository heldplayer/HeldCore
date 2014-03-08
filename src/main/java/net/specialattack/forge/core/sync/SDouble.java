
package net.specialattack.forge.core.sync;

import java.io.DataOutputStream;
import java.io.IOException;

import com.google.common.io.ByteArrayDataInput;

public class SDouble extends BaseSyncable {

    private double value;

    public SDouble(ISyncableObjectOwner owner, double value) {
        super(owner);
        this.value = value;
    }

    public SDouble(ISyncableObjectOwner owner) {
        super(owner);
    }

    public void setValue(double value) {
        if (this.value != value) {
            this.value = value;
            this.hasChanged = true;
        }
    }

    public double getValue() {
        return this.value;
    }

    @Override
    public void read(ByteArrayDataInput in) throws IOException {
        this.value = in.readDouble();
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeDouble(this.value);
    }

    @Override
    public void setValue(Object obj) {
        if (obj instanceof Number) {
            this.setValue(((Number) obj).doubleValue());
        }
    }

    @Override
    public String toString() {
        return "Double: " + this.value;
    }

}

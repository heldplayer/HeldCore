
package net.specialattack.forge.core.sync;

import java.io.DataOutputStream;
import java.io.IOException;

import com.google.common.io.ByteArrayDataInput;

public class SInteger extends BaseSyncable {

    private int value;

    public SInteger(ISyncableObjectOwner owner, int value) {
        super(owner);
        this.value = value;
    }

    public SInteger(ISyncableObjectOwner owner) {
        super(owner);
    }

    public void setValue(int value) {
        this.value = value;
        this.hasChanged = true;
    }

    public int getValue() {
        return this.value;
    }

    @Override
    public void read(ByteArrayDataInput in) throws IOException {
        this.value = in.readInt();
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeInt(this.value);
    }

    @Override
    public void setValue(Object obj) {
        if (obj instanceof Number) {
            this.setValue(((Number) obj).intValue());
        }
    }

    @Override
    public String toString() {
        return "Integer: " + this.value;
    }

}

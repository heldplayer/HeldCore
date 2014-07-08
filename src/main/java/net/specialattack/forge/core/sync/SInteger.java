package net.specialattack.forge.core.sync;

import com.google.common.io.ByteArrayDataInput;

import java.io.DataOutputStream;
import java.io.IOException;

public class SInteger extends BaseSyncable {

    private int value;

    public SInteger(ISyncableObjectOwner owner, int value) {
        super(owner);
        this.value = value;
    }

    public SInteger(ISyncableObjectOwner owner) {
        super(owner);
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        if (this.value != value) {
            this.value = value;
            this.hasChanged = true;
        }
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

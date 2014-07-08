package net.specialattack.forge.core.sync;

import com.google.common.io.ByteArrayDataInput;

import java.io.DataOutputStream;
import java.io.IOException;

public class SLong extends BaseSyncable {

    private long value;

    public SLong(ISyncableObjectOwner owner, long value) {
        super(owner);
        this.value = value;
    }

    public SLong(ISyncableObjectOwner owner) {
        super(owner);
    }

    public long getValue() {
        return this.value;
    }

    public void setValue(long value) {
        if (this.value != value) {
            this.value = value;
            this.hasChanged = true;
        }
    }

    @Override
    public void read(ByteArrayDataInput in) throws IOException {
        this.value = in.readLong();
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeLong(this.value);
    }

    @Override
    public void setValue(Object obj) {
        if (obj instanceof Number) {
            this.setValue(((Number) obj).longValue());
        }
    }

    @Override
    public String toString() {
        return "Long: " + this.value;
    }

}

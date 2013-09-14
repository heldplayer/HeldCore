
package me.heldplayer.util.HeldCore.sync;

import java.io.DataOutputStream;
import java.io.IOException;

import com.google.common.io.ByteArrayDataInput;

public class SLong extends BaseSyncable {

    private long value;

    public SLong(ISyncableObjectOwner owner, long value) {
        super(owner);
        this.value = value;
    }

    public void setValue(long value) {
        this.value = value;
        this.hasChanged = true;
    }

    public long getValue() {
        return this.value;
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
    public String toString() {
        return "Long:" + this.value;
    }

}

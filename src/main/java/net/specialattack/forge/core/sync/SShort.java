
package net.specialattack.forge.core.sync;

import java.io.DataOutputStream;
import java.io.IOException;

import com.google.common.io.ByteArrayDataInput;

public class SShort extends BaseSyncable {

    private short value;

    public SShort(ISyncableObjectOwner owner, short value) {
        super(owner);
        this.value = value;
    }

    public SShort(ISyncableObjectOwner owner) {
        super(owner);
    }

    public void setValue(short value) {
        if (this.value != value) {
            this.value = value;
            this.hasChanged = true;
        }
    }

    public short getValue() {
        return this.value;
    }

    @Override
    public void read(ByteArrayDataInput in) throws IOException {
        this.value = in.readShort();
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeShort(this.value);
    }

    @Override
    public void setValue(Object obj) {
        if (obj instanceof Number) {
            this.setValue(((Number) obj).shortValue());
        }
    }

    @Override
    public String toString() {
        return "Short: " + this.value;
    }

}

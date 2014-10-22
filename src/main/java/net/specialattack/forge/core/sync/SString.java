package net.specialattack.forge.core.sync;

import com.google.common.io.ByteArrayDataInput;
import java.io.DataOutputStream;
import java.io.IOException;

public class SString extends BaseSyncable {

    private String value;

    public SString(ISyncableObjectOwner owner, String value) {
        super(owner);
        this.value = value;
    }

    public SString(ISyncableObjectOwner owner) {
        super(owner);
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        if (!this.value.equals(value)) {
            this.value = value;
            this.hasChanged = true;
        }
    }

    @Override
    public void read(ByteArrayDataInput in) throws IOException {
        byte[] data = new byte[in.readInt()];
        in.readFully(data);
        this.value = new String(data);
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        byte[] data = this.value.getBytes();
        out.writeInt(data.length);
        out.write(this.value.getBytes());
    }

    @Override
    public void setValue(Object obj) {
        if (obj instanceof String) {
            this.setValue((String) obj);
        }
    }

    @Override
    public String toString() {
        return "String: " + this.value;
    }

}


package me.heldplayer.util.HeldCore.sync;

import java.io.DataOutputStream;
import java.io.IOException;

import com.google.common.io.ByteArrayDataInput;

public class SString extends BaseSyncable {

    private String value;

    public SString(ISyncableObjectOwner owner, String value) {
        super(owner);
        this.value = value;
    }

    public SString(ISyncableObjectOwner owner) {
        super(owner);
    }

    public void setValue(String value) {
        this.value = value;
        this.hasChanged = true;
    }

    public String getValue() {
        return this.value;
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

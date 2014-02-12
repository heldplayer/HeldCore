
package net.specialattack.forge.core.sync;

import java.io.DataOutputStream;
import java.io.IOException;

import com.google.common.io.ByteArrayDataInput;

public class SBoolean extends BaseSyncable {

    private boolean value;

    public SBoolean(ISyncableObjectOwner owner, boolean value) {
        super(owner);
        this.value = value;
    }

    public SBoolean(ISyncableObjectOwner owner) {
        super(owner);
    }

    public void setValue(boolean value) {
        this.value = value;
        this.hasChanged = true;
    }

    public boolean getValue() {
        return this.value;
    }

    @Override
    public void read(ByteArrayDataInput in) throws IOException {
        this.value = in.readBoolean();
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeBoolean(this.value);
    }

    @Override
    public void setValue(Object obj) {
        if (obj instanceof Boolean) {
            this.setValue(((Boolean) obj).booleanValue());
        }
    }

    @Override
    public String toString() {
        return "Boolean: " + this.value;
    }

}

package net.specialattack.forge.core.sync;

import com.google.common.io.ByteArrayDataInput;
import java.io.DataOutputStream;
import java.io.IOException;

public interface ISyncable {

    ISyncableObjectOwner getOwner();

    boolean hasChanged();

    void setChanged(boolean value);

    void read(ByteArrayDataInput in) throws IOException;

    void write(DataOutputStream out) throws IOException;

    int getId();

    void setId(int id);

    void setValue(Object obj);

}

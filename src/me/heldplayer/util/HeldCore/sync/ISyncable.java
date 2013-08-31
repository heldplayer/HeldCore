
package me.heldplayer.util.HeldCore.sync;

import java.io.DataOutputStream;
import java.io.IOException;

import com.google.common.io.ByteArrayDataInput;

public interface ISyncable {

    ISyncableObjectOwner getOwner();

    boolean hasChanged();

    void setChanged(boolean value);

    void read(ByteArrayDataInput in) throws IOException;

    void write(DataOutputStream out) throws IOException;

}

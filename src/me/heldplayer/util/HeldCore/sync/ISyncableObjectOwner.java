
package me.heldplayer.util.HeldCore.sync;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;

import com.google.common.io.ByteArrayDataInput;

public interface ISyncableObjectOwner {

    boolean isInvalid();

    List<ISyncable> getSyncables();

    boolean canReceive(EntityPlayerMP player);

    void readSetup(ByteArrayDataInput in) throws IOException;

    void writeSetup(DataOutputStream out) throws IOException;

    int getPosX();

    int getPosY();

    int getPosZ();

}

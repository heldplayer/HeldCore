package net.specialattack.forge.core.sync;

import com.google.common.io.ByteArrayDataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import net.minecraft.world.World;

public interface ISyncableObjectOwner {

    boolean isNotValid();

    void setNotValid();

    List<ISyncable> getSyncables();

    void readSetup(ByteArrayDataInput in) throws IOException;

    void writeSetup(DataOutputStream out) throws IOException;

    String getIdentifier();

    boolean isWorldBound();

    World getWorld();

    int getPosX();

    int getPosY();

    int getPosZ();

    void onDataChanged(ISyncable syncable);

}

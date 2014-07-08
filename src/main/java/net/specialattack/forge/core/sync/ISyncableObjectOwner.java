package net.specialattack.forge.core.sync;

import com.google.common.io.ByteArrayDataInput;
import net.minecraft.world.World;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public interface ISyncableObjectOwner {

    boolean isNotValid();

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

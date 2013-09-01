
package me.heldplayer.util.HeldCore.sync;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;

public interface ISyncableObjectOwner {

    boolean isInvalid();

    List<ISyncable> getSyncables();

    boolean canReceive(EntityPlayerMP player);

}

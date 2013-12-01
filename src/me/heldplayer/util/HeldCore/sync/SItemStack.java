
package me.heldplayer.util.HeldCore.sync;

import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import com.google.common.io.ByteArrayDataInput;

public class SItemStack extends BaseSyncable {

    private ItemStack value;

    public SItemStack(ISyncableObjectOwner owner, ItemStack value) {
        super(owner);
        this.value = value;
    }

    public SItemStack(ISyncableObjectOwner owner) {
        super(owner);
    }

    public void setValue(ItemStack value) {
        this.value = value;
        this.hasChanged = true;
    }

    public ItemStack getValue() {
        return this.value;
    }

    @Override
    public void read(ByteArrayDataInput in) throws IOException {
        NBTTagCompound tag = CompressedStreamTools.read(in);
        this.value = ItemStack.loadItemStackFromNBT(tag);
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        NBTTagCompound tag = this.value.writeToNBT(new NBTTagCompound());
        CompressedStreamTools.write(tag, out);
    }

    @Override
    public String toString() {
        return "ItemStack: " + (this.value == null ? "null" : this.value.toString());
    }

}

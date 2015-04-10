package net.specialattack.forge.core.sync;

import com.google.common.io.ByteArrayDataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;

public class SItemStack extends BaseSyncable {

    private ItemStack value;

    public SItemStack(ISyncableObjectOwner owner, ItemStack value) {
        super(owner);
        this.value = value;
    }

    public SItemStack(ISyncableObjectOwner owner) {
        super(owner);
    }

    public ItemStack getValue() {
        return this.value;
    }

    public void setValue(ItemStack value) {
        if (this.value != value && !ItemStack.areItemStacksEqual(this.value, value)) {
            this.value = value;
            this.hasChanged = true;
        }
    }

    @Override
    public void read(ByteArrayDataInput in) throws IOException {
        NBTTagCompound tag = CompressedStreamTools.func_152456_a(in, NBTSizeTracker.INFINITE);
        this.value = ItemStack.loadItemStackFromNBT(tag);
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        NBTTagCompound tag = this.value.writeToNBT(new NBTTagCompound());
        CompressedStreamTools.write(tag, out);
    }

    @Override
    public void setValue(Object obj) {
        if (obj instanceof ItemStack) {
            this.setValue((ItemStack) obj);
        }
    }

    @Override
    public String toString() {
        return "ItemStack: " + (this.value == null ? "null" : this.value.toString());
    }

}

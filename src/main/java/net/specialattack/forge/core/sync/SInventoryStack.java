package net.specialattack.forge.core.sync;

import com.google.common.io.ByteArrayDataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;

public class SInventoryStack extends BaseSyncable {

    private IInventory inventory;
    private int slot;
    private ItemStack prevValue;

    public SInventoryStack(ISyncableObjectOwner owner, IInventory inventory, int slot) {
        super(owner);
        this.inventory = inventory;
        this.slot = slot;
        this.prevValue = inventory.getStackInSlot(slot);
    }

    public SInventoryStack(ISyncableObjectOwner owner) {
        super(owner);
    }

    @Override
    public boolean hasChanged() {
        if (super.hasChanged()) {
            return true;
        }
        ItemStack stack = this.inventory.getStackInSlot(this.slot);
        return this.prevValue != stack && !ItemStack.areItemStacksEqual(this.prevValue, stack);
    }

    public IInventory getInventory() {
        return this.inventory;
    }

    public void setInventory(IInventory inventory) {
        this.inventory = inventory;
        super.hasChanged = true;
    }

    public ItemStack getValue() {
        return this.inventory.getStackInSlot(this.slot);
    }

    @Override
    public void setValue(Object obj) {
        if (obj instanceof ItemStack) {
            this.setValue((ItemStack) obj);
        }
    }

    public void setValue(ItemStack value) {
        this.inventory.setInventorySlotContents(this.slot, value);
        super.hasChanged = true;
    }

    @Override
    public void read(ByteArrayDataInput in) throws IOException {
        boolean isNull = in.readBoolean();
        if (isNull) {
            this.inventory.setInventorySlotContents(this.slot, null);
        } else {
            NBTTagCompound tag = CompressedStreamTools.func_152456_a(in, NBTSizeTracker.field_152451_a);
            this.inventory.setInventorySlotContents(this.slot, ItemStack.loadItemStackFromNBT(tag));
        }
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        ItemStack stack = this.inventory.getStackInSlot(this.slot);
        if (stack == null) {
            out.writeBoolean(true);
        } else {
            out.writeBoolean(false);
            NBTTagCompound tag = stack.writeToNBT(new NBTTagCompound());
            CompressedStreamTools.write(tag, out);
        }
    }

    @Override
    public String toString() {
        ItemStack stack = this.inventory.getStackInSlot(this.slot);
        return "InventoryStack: " + (stack == null ? "null" : stack.toString());
    }

}


package codechicken.nei;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Dummy class
 */
@SideOnly(Side.CLIENT)
public class InventoryCraftingDummy extends InventoryCrafting {

    public InventoryCraftingDummy() {
        super(null, 3, 3);
    }

    @Override
    public ItemStack decrStackSize(int par1, int par2) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int par1, ItemStack par2ItemStack) {}

}

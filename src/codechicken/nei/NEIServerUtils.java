
package codechicken.nei;

import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class NEIServerUtils {
    public static boolean areStacksSameTypeCrafting(ItemStack stack1, ItemStack stack2) {
        if (stack1 == null || stack2 == null) {
            return false;
        }

        return stack1.itemID == stack2.itemID && (stack1.getItemDamage() == stack2.getItemDamage() || stack1.getItemDamage() == -1 || stack2.getItemDamage() == -1 || stack1.getItem().isDamageable());
    }
}

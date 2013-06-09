
package codechicken.nei.api;

import java.util.Collection;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import codechicken.nei.MultiItemRange;
import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.IUsageHandler;

public class API {

    public static void registerRecipeHandler(ICraftingHandler neiSolderingStationRecipeManager) {}

    public static void registerUsageHandler(IUsageHandler neiSolderingStationRecipeManager) {}

    public static void setItemDamageVariants(int id, Collection<Integer> variants) {}

    public static void addSetRange(String setname, MultiItemRange range) {}

    public static void addToRange(String setname, MultiItemRange range) {}

    public static void hideItem(int itemID) {}

    public static void hideItems(Collection<Integer> items) {}

    public static void addNBTItem(ItemStack item) {}

    public static void registerGuiOverlay(Class<? extends GuiContainer> class1, String string) {}

    public static void registerGuiOverlay(Class<? extends GuiContainer> class1, String string, int x, int y) {}

    public static void registerGuiOverlay(Class<? extends GuiContainer> classz, String ident, IStackPositioner positioner) {}

    public static void registerGuiOverlayHandler(Class<? extends GuiContainer> classz, IOverlayHandler handler, String ident) {}

    public static void setGuiOffset(Class<? extends GuiContainer> classz, int x, int y) {}

}

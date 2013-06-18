
package me.heldplayer.util.HeldCore;

import java.net.URL;

import net.minecraft.util.HttpUtil;

/**
 * Class that checks if minecraft has been updated, disables the GuiDrawer if it
 * is
 * 
 * @author heldplayer
 * 
 */
public class RunnableMinecraftChecker implements Runnable {

    @Override
    public void run() {
        try {
            String s = HttpUtil.func_104145_a(new URL("http://assets.minecraft.net/1_6_has_been_released.flag"));

            if (s != null && s.length() > 0) {
                Updater.hide = true;
            }
        }
        catch (java.io.FileNotFoundException e) {}
        catch (Throwable throwable) {}
    }

}

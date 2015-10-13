package net.specialattack.forge.core.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

@SideOnly(Side.CLIENT)
public class KeyHandler {

    private static List<KeyData> keybinds = new ArrayList<KeyData>();

    public static void registerKeyBind(KeyBinding keyBinding, boolean repeats) {
        KeyHandler.keybinds.add(new KeyData(keyBinding, repeats));
        ClientRegistry.registerKeyBinding(keyBinding);
    }

    public static void registerKeyBind(KeyData key) {
        KeyHandler.keybinds.add(key);
        ClientRegistry.registerKeyBinding(key.keyBinding);
    }

    static void tickKeys() {
        for (KeyData key : KeyHandler.keybinds) {
            int keyCode = key.keyBinding.getKeyCode();
            boolean state = (keyCode < 0 ? Mouse.isButtonDown(keyCode + 100) : Keyboard.isKeyDown(keyCode));
            if (state != key.keyDown || (state && key.repeating)) {
                if (state) {
                    key.keyDown(key.keyDown);
                } else {
                    key.keyUp();
                }
                key.keyDown = state;
            }
        }
    }

    public static class KeyData {

        private KeyBinding keyBinding;
        private boolean keyDown;
        private boolean repeating;
        private int ticktime;

        public KeyData(KeyBinding keyBinding, boolean repeating) {
            this.keyBinding = keyBinding;
            this.repeating = repeating;
        }

        protected void keyDown(boolean isRepeat) {
            this.ticktime++;
        }

        protected void keyUp() {
            this.ticktime = 0;
        }
    }
}

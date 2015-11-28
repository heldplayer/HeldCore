package net.specialattack.forge.core.client;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

@SideOnly(Side.CLIENT)
public class KeyHandler {

    private static List<KeyHandler.KeyData> keybinds = new ArrayList<KeyHandler.KeyData>();

    public static void registerKeyBind(KeyBinding keyBinding, boolean repeats) {
        KeyHandler.keybinds.add(new KeyHandler.KeyData(keyBinding, repeats));
        ClientRegistry.registerKeyBinding(keyBinding);
    }

    public static void registerKeyBind(KeyHandler.KeyData key) {
        KeyHandler.keybinds.add(key);
        ClientRegistry.registerKeyBinding(key.keyBinding);
    }

    static void tickKeys() {
        for (KeyHandler.KeyData key : KeyHandler.keybinds) {
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

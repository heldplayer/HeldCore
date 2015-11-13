package net.specialattack.forge.core.client.gui.config;

import cpw.mods.fml.client.config.GuiEditArray;
import cpw.mods.fml.client.config.GuiEditArrayEntries;
import cpw.mods.fml.client.config.IConfigElement;
import java.lang.reflect.Field;
import org.lwjgl.input.Keyboard;

public class StringArrayEntry extends GuiEditArrayEntries.StringEntry {

    private static Field enabled;
    private String[] possibleValues;

    static {
        Class<GuiEditArray> clazz = GuiEditArray.class;
        try {
            StringArrayEntry.enabled = clazz.getDeclaredField("enabled");
            StringArrayEntry.enabled.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public StringArrayEntry(GuiEditArray owningScreen, GuiEditArrayEntries owningEntryList, IConfigElement configElement, Object value) {
        super(owningScreen, owningEntryList, configElement, value);
        this.possibleValues = configElement.getValidValues();
        if (this.possibleValues != null) {
            if (this.possibleValues.length == 0) {
                this.possibleValues = null;
            } else {
                this.isValidated = true;
            }
        }
    }

    @Override
    public void keyTyped(char eventChar, int eventKey) {
        try {
            if (StringArrayEntry.enabled.getBoolean(this.owningScreen) || eventKey == Keyboard.KEY_LEFT || eventKey == Keyboard.KEY_RIGHT || eventKey == Keyboard.KEY_HOME || eventKey == Keyboard.KEY_END) {
                this.textFieldValue.textboxKeyTyped((StringArrayEntry.enabled.getBoolean(this.owningScreen) ? eventChar : Keyboard.CHAR_NONE), eventKey);

                String value = this.textFieldValue.getText();
                if (this.possibleValues != null) {
                    boolean valid = false;
                    for (String str : this.possibleValues) {
                        if (str.equals(value)) {
                            valid = true;
                            break;
                        }
                    }
                    this.isValidValue = valid;
                } else if (this.configElement.getValidationPattern() != null) {
                    this.isValidValue = this.configElement.getValidationPattern().matcher(value.trim()).matches();
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}

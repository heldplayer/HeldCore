package net.specialattack.forge.core.client.gui.deprecated.style;

import net.minecraft.util.ResourceLocation;
import net.specialattack.forge.core.client.gui.Color;
import net.specialattack.forge.core.client.gui.deprecated.style.background.AdvancedTextureBackground;
import net.specialattack.forge.core.client.gui.deprecated.style.background.IBackground;
import net.specialattack.forge.core.client.gui.deprecated.style.background.SolidBackground;
import net.specialattack.forge.core.client.gui.deprecated.style.border.IBorder;
import net.specialattack.forge.core.client.gui.deprecated.style.border.SolidBorder;

public class StyleDefs {

    // Textbox styles
    public static final Color COLOR_TEXTBOX_BACKGROUND = new Color(0xFF000000);
    public static final Color COLOR_TEXTBOX_BORDER = new Color(0xFFA0A0A0);
    public static final Color COLOR_TEXTBOX_TEXT = new Color(0xFFE0E0E0);
    public static final Color COLOR_TEXTBOX_TEXT_DISABLED = new Color(0xFF707070);
    public static final IBackground BACKGROUND_TEXTBOX = new SolidBackground(StyleDefs.COLOR_TEXTBOX_BACKGROUND);
    public static final IBorder BORDER_TEXTBOX = new SolidBorder(StyleDefs.COLOR_TEXTBOX_BORDER, 1);
    // Button styles
    public static final Color COLOR_BUTTON_TEXT = new Color(0xFFE0E0E0);
    public static final Color COLOR_BUTTON_TEXT_HOVER = new Color(0xFFFFFFA0);
    public static final IBackground BACKGROUND_BUTTON_NORMAL = new AdvancedTextureBackground(new ResourceLocation("spacore:textures/gui/button_normal.png"));
    public static final IBackground BACKGROUND_BUTTON_HOVER = new AdvancedTextureBackground(new ResourceLocation("spacore:textures/gui/button_hover.png"));
    public static final IBackground BACKGROUND_BUTTON_DISABLED = new AdvancedTextureBackground(new ResourceLocation("spacore:textures/gui/button_disabled.png"));
    // Splitter styles
    public static final Color COLOR_SPLITTER_BACKGROUND = new Color(0xFFA0A0A0);
    public static final IBackground BACKGROUND_SPLITTER = new SolidBackground(StyleDefs.COLOR_SPLITTER_BACKGROUND);
    // Menu styles
    public static final Color COLOR_MENU_ITEM_NORMAL = new Color(0xFF000000);
    public static final Color COLOR_MENU_ITEM_HOVER = new Color(0xFF333333);
    public static final Color COLOR_MENU_ITEM_DISABLED = new Color(0xFF888888);
    public static final Color COLOR_MENU_ITEM_BACKGROUND_NORMAL = new Color(0xFFCCCCCC);
    public static final Color COLOR_MENU_ITEM_BACKGROUND_HOVER = new Color(0xFFEEEEEE);
    public static final Color COLOR_MENU_ITEM_BACKGROUND_DISABLED = new Color(0xFFCCCCCC);
    public static final Color COLOR_MENU_BORDER = new Color(0xFF888888);
    public static final Color COLOR_MENU_BAR_BACKGROUND = new Color(0xFF222222);
    public static final IBackground BACKGROUND_MENU_ITEM_NORMAL = new SolidBackground(StyleDefs.COLOR_MENU_ITEM_BACKGROUND_NORMAL);
    public static final IBackground BACKGROUND_MENU_ITEM_HOVER = new SolidBackground(StyleDefs.COLOR_MENU_ITEM_BACKGROUND_HOVER);
    public static final IBackground BACKGROUND_MENU_ITEM_DISABLED = new SolidBackground(StyleDefs.COLOR_MENU_ITEM_BACKGROUND_DISABLED);
    public static final IBackground BACKGROUND_MENU_BAR = new SolidBackground(StyleDefs.COLOR_MENU_BAR_BACKGROUND);
    public static final IBorder BORDER_MENU = new SolidBorder(StyleDefs.COLOR_MENU_BORDER, 1);
    // Progress bar styles
    public static final Color COLOR_PROGRESS_BAR_BACKGROUND = new Color(0xFF404040);
    public static final Color COLOR_PROGRESS_BAR_NORMAL = new Color(0xFF80FF80);
    public static final Color COLOR_PROGRESS_BAR_PAUSED = new Color(0xFFFFFF80);
    public static final Color COLOR_PROGRESS_BAR_ERROR = new Color(0xFFFF8080);
    public static final Color COLOR_PROGRESS_BAR_CONTINUOUS = new Color(0xFF80FF80);
    public static final IBackground BACKGROUND_PROGRESS_BAR = new SolidBackground(StyleDefs.COLOR_PROGRESS_BAR_BACKGROUND);
    // Scrollbar styles
    public static final Color COLOR_SCROLLBAR_BACKGROUND = new Color(0xFF404040);
    public static final Color COLOR_SCROLLBAR_FOREGROUND = new Color(0xFFBBBBBB);
    public static final Color COLOR_SCROLLBAR_FOREGROUND_HOVER = new Color(0xFFEEEEEE);
    // Combo box styles
    public static final Color COLOR_COMBO_OPTION_NORMAL = new Color(0xFF000000);
    public static final Color COLOR_COMBO_OPTION_HOVER = new Color(0xFF333333);
    public static final Color COLOR_COMBO_OPTION_DISABLED = new Color(0xFF888888);
    public static final Color COLOR_COMBO_OPTION_SELECTED = new Color(0xFF333333);
    public static final Color COLOR_COMBO_OPTION_BACKGROUND_NORMAL = new Color(0xFFCCCCCC);
    public static final Color COLOR_COMBO_OPTION_BACKGROUND_HOVER = new Color(0xFFEEEEEE);
    public static final Color COLOR_COMBO_OPTION_BACKGROUND_DISABLED = new Color(0xFFCCCCCC);
    public static final Color COLOR_COMBO_OPTION_BACKGROUND_SELECTED = new Color(0xFFFFFFC1);
    public static final IBackground BACKGROUND_COMBO_OPTION_NORMAL = new SolidBackground(StyleDefs.COLOR_COMBO_OPTION_BACKGROUND_NORMAL);
    public static final IBackground BACKGROUND_COMBO_OPTION_HOVER = new SolidBackground(StyleDefs.COLOR_COMBO_OPTION_BACKGROUND_HOVER);
    public static final IBackground BACKGROUND_COMBO_OPTION_DISABLED = new SolidBackground(StyleDefs.COLOR_COMBO_OPTION_BACKGROUND_DISABLED);
    public static final IBackground BACKGROUND_COMBO_OPTION_SELECTED = new SolidBackground(StyleDefs.COLOR_COMBO_OPTION_BACKGROUND_SELECTED);
}

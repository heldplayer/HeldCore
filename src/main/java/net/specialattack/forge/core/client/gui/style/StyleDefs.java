package net.specialattack.forge.core.client.gui.style;

import net.minecraft.util.ResourceLocation;
import net.specialattack.forge.core.client.gui.Color;
import net.specialattack.forge.core.client.gui.style.background.AdvancedTextureBackground;
import net.specialattack.forge.core.client.gui.style.background.IBackground;
import net.specialattack.forge.core.client.gui.style.background.SolidBackground;
import net.specialattack.forge.core.client.gui.style.border.IBorder;
import net.specialattack.forge.core.client.gui.style.border.SolidBorder;

public final class StyleDefs {

    // Textbox styles
    public static final Color COLOR_TEXTBOX_BACKGROUND = new Color(0xFF000000);
    public static final Color COLOR_TEXTBOX_BORDER = new Color(0xFFA0A0A0);
    public static final Color COLOR_TEXTBOX_TEXT = new Color(0xFFE0E0E0);
    public static final Color COLOR_TEXTBOX_TEXT_DISABLED = new Color(0xFF707070);
    public static final IBackground BACKGROUND_TEXTBOX = new SolidBackground(COLOR_TEXTBOX_BACKGROUND);
    public static final IBorder BORDER_TEXTBOX = new SolidBorder(COLOR_TEXTBOX_BORDER, 1);
    // Button styles
    public static final Color COLOR_BUTTON_TEXT = new Color(0xFFE0E0E0);
    public static final Color COLOR_BUTTON_TEXT_HOVER = new Color(0xFFFFFFA0);
    public static final IBackground BACKGROUND_BUTTON_NORMAL = new AdvancedTextureBackground(new ResourceLocation("spacore:textures/gui/button_normal.png"));
    public static final IBackground BACKGROUND_BUTTON_HOVER = new AdvancedTextureBackground(new ResourceLocation("spacore:textures/gui/button_hover.png"));
    public static final IBackground BACKGROUND_BUTTON_DISABLED = new AdvancedTextureBackground(new ResourceLocation("spacore:textures/gui/button_disabled.png"));
    // Splitter styles
    public static final Color COLOR_SPLITTER_BACKGROUND = new Color(0xFFA0A0A0);
    public static final IBackground BACKGROUND_SPLITTER = new SolidBackground(COLOR_SPLITTER_BACKGROUND);
    // Menu styles
    public static final Color COLOR_MENU_ITEM_NORMAL = new Color(0xFF000000);
    public static final Color COLOR_MENU_ITEM_HOVER = new Color(0xFF333333);
    public static final Color COLOR_MENU_ITEM_DISABLED = new Color(0xFF888888);
    public static final Color COLOR_MENU_ITEM_BACKGROUND_NORMAL = new Color(0xFFCCCCCC);
    public static final Color COLOR_MENU_ITEM_BACKGROUND_HOVER = new Color(0xFFEEEEEE);
    public static final Color COLOR_MENU_ITEM_BACKGROUND_DISABLED = new Color(0xFFCCCCCC);
    public static final Color COLOR_MENU_BORDER = new Color(0xFF888888);
    public static final IBackground BACKGROUND_MENU_ITEM_NORMAL = new SolidBackground(COLOR_MENU_ITEM_BACKGROUND_NORMAL);
    public static final IBackground BACKGROUND_MENU_ITEM_HOVER = new SolidBackground(COLOR_MENU_ITEM_BACKGROUND_HOVER);
    public static final IBackground BACKGROUND_MENU_ITEM_DISABLED = new SolidBackground(COLOR_MENU_ITEM_BACKGROUND_DISABLED);
    public static final IBorder BORDER_MENU = new SolidBorder(COLOR_MENU_BORDER, 1);
    // Progress bar styles
    public static final Color COLOR_PROGRESS_BAR_BACKGROUND = new Color(0xFF404040);
    public static final Color COLOR_PROGRESS_BAR_NORMAL = new Color(0xFF80FF80);
    public static final Color COLOR_PROGRESS_BAR_PAUSED = new Color(0xFFFFFF80);
    public static final Color COLOR_PROGRESS_BAR_ERROR = new Color(0xFFFF8080);
    public static final Color COLOR_PROGRESS_BAR_CONTINUOUS = new Color(0xFF80FF80);
    public static final IBackground BACKGROUND_PROGRESS_BAR = new SolidBackground(COLOR_PROGRESS_BAR_BACKGROUND);
    // Scrollbar stylesstyles
    public static final Color COLOR_SCROLLBAR_BACKGROUND = new Color(0xFF404040);
    public static final Color COLOR_SCROLLBAR_FOREGROUND = new Color(0xFFCCCCCC);

    private StyleDefs() {
    }

}

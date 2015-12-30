package net.specialattack.forge.core.client.gui.deprecated.element;

import java.util.Collections;
import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import net.specialattack.forge.core.client.gui.GuiHelper;
import net.specialattack.forge.core.client.gui.deprecated.SizeContext;
import net.specialattack.forge.core.client.gui.deprecated.layout.SGLayoutManager;
import net.specialattack.forge.core.client.gui.deprecated.style.StyleDefs;
import net.specialattack.forge.core.client.gui.deprecated.style.outline.InvisibleOutline;

public class SGCheckbox extends SGInteractable {

    private boolean checked;

    public SGCheckbox(boolean checked) {
        this.checked = checked;
        this.setPreferredInnerSize(11, 11);
        this.setBackgrounds(StyleDefs.BACKGROUND_BUTTON_NORMAL, StyleDefs.BACKGROUND_BUTTON_HOVER, StyleDefs.BACKGROUND_BUTTON_DISABLED);
        this.setOutline(new InvisibleOutline(2));
    }

    public SGCheckbox() {
        this(false);
    }

    @Override
    public void addChild(SGComponent child, Object param) {
        // No children allowed here
    }

    @Override
    public void removeChild(SGComponent child) {
        // Don't even think about it
    }

    @Override
    public List<SGComponent> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public void setLayoutManager(SGLayoutManager layoutManager) {
        // We don't need no layout manager
    }

    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTicks) {
        if (this.checked) {
            GlStateManager.translate(this.getLeft(SizeContext.INNER), this.getTop(SizeContext.INNER), this.getZLevel());
            this.font.drawString("x", 3, 1, this.getTextColor().colorHex);
        }
    }

    @Override
    public void onClick(int mouseX, int mouseY, int button) {
        this.checked = !this.checked;
    }

    @Override
    public void clickHappened(int mouseX, int mouseY, int button) {
        GuiHelper.playButtonClick();
    }
}

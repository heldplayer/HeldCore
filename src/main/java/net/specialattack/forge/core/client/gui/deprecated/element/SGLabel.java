package net.specialattack.forge.core.client.gui.deprecated.element;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.specialattack.forge.core.client.GLState;
import net.specialattack.forge.core.client.gui.Color;
import net.specialattack.forge.core.client.gui.deprecated.SizeContext;
import net.specialattack.forge.core.client.gui.deprecated.layout.FlowLayout;
import net.specialattack.forge.core.client.gui.deprecated.layout.Region;
import net.specialattack.forge.core.client.gui.deprecated.layout.SGLayoutManager;
import net.specialattack.forge.core.client.gui.deprecated.style.StyleDefs;
import net.specialattack.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class SGLabel extends SGComponent {

    private String text;
    private boolean shouldSplit;
    private boolean hasShadow = true;
    private boolean defaultSize = true;
    private Color color = StyleDefs.COLOR_TEXTBOX_TEXT;
    private FlowLayout verticalLayout = FlowLayout.CENTER;
    private FlowLayout horizontalLayout = FlowLayout.CENTER;

    public SGLabel(String text) {
        this.setText(text);
    }

    public SGLabel() {
        this(null);
    }

    public void setText(String text) {
        this.text = text == null ? "null" : text;
        this.updatePreferredSize();
    }

    public String getText() {
        return this.text;
    }

    public void setShouldSplit(boolean value) {
        this.shouldSplit = value;
        this.updatePreferredSize();
    }

    public void setHasShadow(boolean hasShadow) {
        this.hasShadow = hasShadow;
    }

    public void setLayout(FlowLayout horizontal, FlowLayout vertical) {
        this.horizontalLayout = horizontal;
        this.verticalLayout = vertical;
    }

    private void updatePreferredSize() {
        if (this.defaultSize) {
            if (this.shouldSplit) {
                int preferredWidth = this.getPreferredWidth();
                int height = this.font.listFormattedStringToWidth(this.getText(), preferredWidth).size() * (this.font.FONT_HEIGHT + 1);
                super.setPreferredInnerSize(preferredWidth + 2, height);
            } else {
                int width = 0;
                int height = 0;
                for (String str : this.getText().split("\n")) {
                    width = MathHelper.max(width, this.font.getStringWidth(str));
                    height += this.font.FONT_HEIGHT + 1;
                }
                super.setPreferredInnerSize(width + 2, height);
            }
        }
    }

    public void setColor(Color color) {
        if (color == null) {
            throw new IllegalArgumentException("Color cannot be null");
        }
        this.color = color;
    }

    public Color getColor() {
        return this.color;
    }

    @Override
    public void setPreferredInnerSize(int width, int height) {
        this.defaultSize = false;
        super.setPreferredInnerSize(width, height);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTicks) {
        GL11.glTranslatef(this.getLeft(SizeContext.INNER) + 1, this.getTop(SizeContext.INNER), this.getZLevel());
        if (this.getText() != null) {
            GLState.glEnable(GL11.GL_TEXTURE_2D);
            List<String> strings;
            int width = this.getWidth(SizeContext.INNER);
            if (this.shouldSplit) {
                strings = (List<String>) this.font.listFormattedStringToWidth(this.getText(), width);
            } else {
                strings = Arrays.asList(this.getText().split("\n"));
            }
            int height = this.getHeight(SizeContext.INNER);
            int i = 0;
            int textHeight = strings.size() * (this.font.FONT_HEIGHT + 1);
            int offset = this.verticalLayout.decide(height, textHeight);
            for (String str : strings) {
                if (i * (this.font.FONT_HEIGHT + 1) >= height) {
                    break;
                }
                String trimmed = str;//this.font.trimStringToWidth(str, width, this.hasShadow);
                if (trimmed.length() < str.length()) {
                    int length = width - 10;
                    if (length > 0) {
                        trimmed = this.font.trimStringToWidth(str, length, this.hasShadow) + "...";
                    }
                }
                int left = this.horizontalLayout.decide(width, this.font.getStringWidth(trimmed));
                this.font.drawString(trimmed, left, offset + i * (this.font.FONT_HEIGHT + 1) + 1, this.getColor().colorHex, this.hasShadow);
                i++;
            }
        }
    }

    @Override
    public Region predictSize() {
        return new Region(0, 0, this.getPreferredWidth(), this.getPreferredHeight());
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
}

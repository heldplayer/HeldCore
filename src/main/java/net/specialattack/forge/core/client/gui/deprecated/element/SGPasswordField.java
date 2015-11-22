package net.specialattack.forge.core.client.gui.deprecated.element;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.specialattack.forge.core.client.gui.deprecated.SGUtils;
import net.specialattack.forge.core.client.gui.deprecated.SizeContext;
import net.specialattack.forge.core.client.gui.deprecated.style.StyleDefs;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class SGPasswordField extends SGInteractable {

    private String value;
    private String showingValue;
    private int maxLength = 32;
    private int cursorCounter;
    private int cursorPosition, selectionPosition, scrollOffset;

    public SGPasswordField(String text) {
        this.setValue(text);
        this.setBackground(StyleDefs.BACKGROUND_TEXTBOX);
        this.setColors(StyleDefs.COLOR_TEXTBOX_TEXT, StyleDefs.COLOR_TEXTBOX_TEXT, StyleDefs.COLOR_TEXTBOX_TEXT_DISABLED);
        this.setBorder(StyleDefs.BORDER_TEXTBOX);
    }

    public SGPasswordField() {
        this("");
    }

    @Override
    public void updateTick() {
        this.cursorCounter++;
    }

    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTicks) {
        SGUtils.clipComponent(this);
        GlStateManager.translate(this.getLeft(SizeContext.INNER) + 2, this.getTop(SizeContext.INNER), this.getZLevel());
        int drawColor = this.getTextColor().colorHex;
        int cursorOffset = this.cursorPosition - this.scrollOffset;
        int selectionOffset = this.selectionPosition - this.scrollOffset;
        String visibleText = this.font.trimStringToWidth(this.showingValue.substring(this.scrollOffset), this.getWidth(SizeContext.INNER) - 4);
        //String visibleText = this.font.trimStringToWidth(this.value.substring(this.scrollOffset), this.getWidth(SizeContext.INNER) - (this.hasBorder() ? 4 : 0));
        boolean cursorVisible = cursorOffset >= 0 && cursorOffset <= visibleText.length();
        boolean drawCursor = this.hasFocus() && this.cursorCounter / 6 % 2 == 0 && cursorVisible;
        //int posX = this.hasBorder() ? 4 : 0;
        int posY = (this.getHeight(SizeContext.INNER) - this.font.FONT_HEIGHT) / 2 + 1;
        int cursorPosition = 0;

        if (selectionOffset > visibleText.length()) {
            selectionOffset = visibleText.length();
        }

        if (visibleText.length() > 0) {
            String text = cursorVisible ? visibleText.substring(0, cursorOffset) : visibleText;
            cursorPosition = this.font.drawStringWithShadow(text, 0, posY, drawColor);
        }

        boolean cursorInbetween = this.cursorPosition < this.value.length() || this.value.length() >= this.maxLength;
        int cursorDrawPosition = cursorPosition;

        if (!cursorVisible) {
            cursorDrawPosition = cursorOffset > 0 ? this.getWidth(SizeContext.INNER) : 0;
        } else if (cursorInbetween) {
            cursorDrawPosition = cursorPosition - 1;
            cursorPosition--;
        }

        if (visibleText.length() > 0 && cursorVisible && cursorOffset < visibleText.length()) {
            this.font.drawStringWithShadow(visibleText.substring(cursorOffset), cursorPosition, posY, drawColor);
        }

        if (drawCursor) {
            if (cursorInbetween) {
                Gui.drawRect(cursorDrawPosition - 1, posY - 1, cursorDrawPosition, posY + 1 + this.font.FONT_HEIGHT, 0xFFD0D0D0);
            } else {
                this.font.drawStringWithShadow("_", cursorDrawPosition, posY, drawColor);
            }
        }

        if (selectionOffset != cursorOffset) {
            int selectionLength = this.font.getStringWidth(visibleText.substring(0, selectionOffset));
            this.drawSelection(cursorDrawPosition, posY - 1, selectionLength - 1, posY + 1 + this.font.FONT_HEIGHT);
        }
        SGUtils.endClip();
    }

    private void drawSelection(int startX, int startY, int endX, int endY) {
        if (startX < endX) {
            int temp = startX;
            startX = endX;
            endX = temp;
        }

        if (startY < endY) {
            int temp = startY;
            startY = endY;
            endY = temp;
        }

        int posX = this.getLeft(SizeContext.INNER);
        int width = this.getWidth(SizeContext.INNER);

        if (endX > posX + width) {
            endX = posX + width;
        }

        if (startX > posX + width) {
            startX = posX + width;
        }

        if (this.hasFocus()) {
            GlStateManager.color(0.0F, 0.0F, 255.0F, 255.0F);
        } else {
            GlStateManager.color(255.0F, 0.0F, 255.0F, 255.0F);
        }
        GlStateManager.disableTexture2D();
        GlStateManager.enableColorLogic();
        GlStateManager.colorLogicOp(GL11.GL_OR_REVERSE);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex3d(startX, endY, 0.0D);
        GL11.glVertex3d(endX, endY, 0.0D);
        GL11.glVertex3d(endX, startY, 0.0D);
        GL11.glVertex3d(startX, startY, 0.0D);
        GL11.glEnd();
        GlStateManager.disableColorLogic();
        GlStateManager.enableTexture2D();
    }

    public void setValue(String value) {
        if (value == null) {
            this.value = "";
            this.showingValue = "";
        } else {
            if (value.length() > this.maxLength) {
                value = value.substring(0, this.maxLength);
            }
            this.value = value;
            this.showingValue = "";
            for (int i = 0; i < value.length(); i++) {
                this.showingValue += "*";
            }
        }
    }

    public String getValue() {
        return this.value;
    }

    public String getSelectedText() {
        int start = this.selectionPosition < this.cursorPosition ? this.selectionPosition : this.cursorPosition;
        int end = this.selectionPosition < this.cursorPosition ? this.cursorPosition : this.selectionPosition;
        return this.value.substring(start, end);
    }

    public void writeText(String text) {
        text = ChatAllowedCharacters.filterAllowedCharacters(text);
        int start = this.selectionPosition < this.cursorPosition ? this.selectionPosition : this.cursorPosition;
        int end = this.selectionPosition < this.cursorPosition ? this.cursorPosition : this.selectionPosition;
        int maxLength = this.maxLength - this.value.length() - (start - this.selectionPosition);

        StringBuilder result = new StringBuilder();
        if (this.value.length() > 0) {
            result.append(this.value.substring(0, start));
        }

        int offset;

        if (maxLength < text.length()) {
            result.append(text.substring(0, maxLength));
            offset = maxLength;
        } else {
            result.append(text);
            offset = text.length();
        }

        if (this.value.length() > 0 && end < this.value.length()) {
            result.append(this.value.substring(end));
        }

        this.setValue(result.toString());
        this.setPosition(start + offset);
    }

    public void deleteWords(int amount) {
        if (this.value.length() != 0) {
            if (this.selectionPosition != this.cursorPosition) {
                this.writeText("");
            } else {
                this.deleteFromCursor(this.getNthWordFromCursor(amount) - this.cursorPosition);
            }
        }
    }

    public void deleteFromCursor(int amount) {
        if (this.value.length() != 0) {
            if (this.selectionPosition != this.cursorPosition) {
                this.writeText("");
            } else {
                boolean deleteLeft = amount < 0;
                int start = deleteLeft ? this.cursorPosition + amount : this.cursorPosition;
                int end = deleteLeft ? this.cursorPosition : this.cursorPosition + amount;

                StringBuilder result = new StringBuilder();
                if (start >= 0) {
                    result.append(this.value.substring(0, start));
                }

                if (end < this.value.length()) {
                    result.append(this.value.substring(end));
                }

                this.setValue(result.toString());

                if (deleteLeft) {
                    this.setCursorPosition(this.cursorPosition + amount);
                }
            }
        }
    }

    public int getNthWordFromCursor(int amount) {
        return this.getNthWordFromPosition(amount, this.cursorPosition);
    }

    public int getNthWordFromPosition(int amount, int position) {
        return this.getNthWordFromPosition(amount, position, true);
    }

    public int getNthWordFromPosition(int amount, int position, boolean skipSpaces) {
        int currentPos = position;
        boolean searchLeft = amount < 0;
        amount = Math.abs(amount);

        for (int i = 0; i < amount; ++i) {
            if (searchLeft) {
                while (skipSpaces && currentPos > 0 && this.value.charAt(currentPos - 1) == 32) {
                    currentPos--;
                }

                while (currentPos > 0 && this.value.charAt(currentPos - 1) != 32) {
                    currentPos--;
                }
            } else {
                int length = this.value.length();
                currentPos = this.value.indexOf(32, currentPos);

                if (currentPos == -1) {
                    currentPos = length;
                } else {
                    while (skipSpaces && currentPos < length && this.value.charAt(currentPos) == 32) {
                        currentPos++;
                    }
                }
            }
        }

        return currentPos;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public void setCursorPositionStart() {
        this.setCursorPosition(0);
    }

    public void setCursorPositionEnd() {
        this.setCursorPosition(this.value.length());
    }

    public void setCursorPosition(int position) {
        this.cursorPosition = MathHelper.clamp_int(position, 0, this.value.length());
        this.setSelectionPosition(position);
    }

    public int getCursorPosition() {
        return this.cursorPosition;
    }

    public void setSelectionPositionStart() {
        this.setSelectionPosition(0);
    }

    public void setSelectionPositionEnd() {
        this.setSelectionPosition(this.value.length());
    }

    public void setSelectionPosition(int position) {
        int length = this.value.length();

        position = MathHelper.clamp_int(position, 0, length);
        this.selectionPosition = position;

        if (this.scrollOffset > length) {
            this.scrollOffset = length;
        }

        int width = this.getWidth(SizeContext.INNER) - 4;
        String visible = this.font.trimStringToWidth(this.showingValue.substring(this.scrollOffset), width);
        int offset = visible.length() + this.scrollOffset;
        if (position == this.scrollOffset) {
            this.scrollOffset = this.font.trimStringToWidth(this.showingValue, width, true).length();
        }

        if (position > offset) {
            this.scrollOffset += position - offset;
        } else if (position <= this.scrollOffset) {
            this.scrollOffset -= this.scrollOffset - position;
        }

        if (this.scrollOffset < 0) {
            this.scrollOffset = 0;
        }

        if (this.scrollOffset > offset) {
            this.scrollOffset = offset;
        }
    }

    public int getSelectionPosition() {
        return this.selectionPosition;
    }

    public void setPositionStart() {
        this.setCursorPosition(0);
        this.setSelectionPosition(0);
    }

    public void setPositionEnd() {
        this.setCursorPosition(this.value.length());
        this.setSelectionPosition(this.value.length());
    }

    public void setPosition(int position) {
        this.setCursorPosition(position);
        this.setSelectionPosition(position);
    }

    @Override
    public boolean onKey(char character, int keycode) {
        if (this.hasFocus()) {
            switch (character) {
                case 1:
                    this.setCursorPositionEnd();
                    this.setSelectionPosition(0);
                    return true;
                case 3:
                    GuiScreen.setClipboardString(this.getSelectedText());
                    return true;
                case 22:
                    if (this.isEnabled()) {
                        this.writeText(GuiScreen.getClipboardString());
                        return true;
                    }

                    return false;
                case 24:
                    GuiScreen.setClipboardString(this.getSelectedText());

                    if (this.isEnabled()) {
                        this.writeText("");
                    }

                    return true;
                default:
                    switch (keycode) {
                        case 1:
                            if (this.selectionPosition != this.cursorPosition) {
                                this.setCursorPosition(this.cursorPosition);
                            } else {
                                this.focusChangeUp(null);
                            }
                            return true;
                        case 14:
                            if (this.isEnabled()) {
                                if (GuiScreen.isCtrlKeyDown()) {
                                    this.deleteWords(-1);
                                    return true;
                                } else {
                                    this.deleteFromCursor(-1);
                                    return true;
                                }
                            }

                            return false;
                        case 199:
                            if (GuiScreen.isShiftKeyDown()) {
                                this.setSelectionPositionStart();
                            } else {
                                this.setPositionStart();
                            }

                            return true;
                        case 203:
                            if (GuiScreen.isShiftKeyDown()) {
                                if (GuiScreen.isCtrlKeyDown()) {
                                    this.setSelectionPosition(this.getNthWordFromPosition(-1, this.selectionPosition));
                                } else {
                                    this.setSelectionPosition(this.selectionPosition - 1);
                                }
                            } else if (GuiScreen.isCtrlKeyDown()) {
                                this.setCursorPosition(this.getNthWordFromCursor(-1));
                            } else {
                                this.setPosition(this.cursorPosition - 1);
                            }

                            return true;
                        case 205:
                            if (GuiScreen.isShiftKeyDown()) {
                                if (GuiScreen.isCtrlKeyDown()) {
                                    this.setSelectionPosition(this.getNthWordFromPosition(1, this.selectionPosition));
                                } else {
                                    this.setSelectionPosition(this.selectionPosition + 1);
                                }
                            } else if (GuiScreen.isCtrlKeyDown()) {
                                this.setCursorPosition(this.getNthWordFromCursor(1));
                            } else {
                                this.setPosition(this.cursorPosition + 1);
                            }

                            return true;
                        case 207:
                            if (GuiScreen.isShiftKeyDown()) {
                                this.setSelectionPositionEnd();
                            } else {
                                this.setPositionEnd();
                            }

                            return true;
                        case 211:
                            if (GuiScreen.isCtrlKeyDown()) {
                                if (this.isEnabled()) {
                                    this.deleteWords(1);
                                }
                            } else if (this.isEnabled()) {
                                this.deleteFromCursor(1);
                            }

                            return true;
                        default:
                            if (ChatAllowedCharacters.isAllowedCharacter(character)) {
                                if (this.isEnabled()) {
                                    this.writeText(Character.toString(character));
                                    return true;
                                }
                            }
                            return false;
                    }
            }
        }
        return false;
    }

    @Override
    public void onClick(int mouseX, int mouseY, int button) {
        if (!this.isEnabled()) {
            return;
        }

        this.focusChangeUp(this);

        if (this.hasFocus() && button == 0) {
            String trimmed = this.font.trimStringToWidth(this.showingValue.substring(this.scrollOffset), this.getWidth(SizeContext.INNER));
            if (GuiScreen.isShiftKeyDown()) {
                this.setSelectionPosition(this.font.trimStringToWidth(trimmed, mouseX).length() + this.scrollOffset);
            } else {
                this.setCursorPosition(this.font.trimStringToWidth(trimmed, mouseX).length() + this.scrollOffset);
            }
        }
    }
}

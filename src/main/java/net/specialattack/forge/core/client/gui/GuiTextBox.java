package net.specialattack.forge.core.client.gui;

import java.util.List;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiTextBox extends Gui {

    private final FontRenderer font;
    private final int posX;
    private final int posY;
    private final int width;
    private final int height;

    private ChatComponentText lines = new ChatComponentText("");
    @SuppressWarnings("unchecked")
    private List<IChatComponent> chatLines = this.lines.getSiblings();
    private int cursorPosition;
    private int cursorPositionComponent;

    private int selectionEnd;
    private int selectionEndComponent;

    private int cursorCounter;

    private boolean enableBackgroundDrawing = true;
    private boolean canLoseFocus = true;
    private boolean isFocused;
    private boolean isEnabled = true;
    private boolean visible = true;

    private int lineScrollOffset;
    private int enabledColor = 0xE0E0E0;
    private int disabledColor = 0x707070;

    public GuiTextBox(FontRenderer font, int posX, int posY, int width, int height) {
        this.font = font;
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.lines.appendSibling(new ChatComponentText(""));
    }

    public void updateCursorCounter() {
        this.cursorCounter++;
    }

    public String getText() {
        StringBuilder result = new StringBuilder();

        boolean first = true;
        for (IChatComponent line : this.chatLines) {
            if (first) {
                first = false;
            } else {
                result.append("\n");
            }
            result.append(line.getUnformattedText());
        }
        return result.toString();
    }

    public void setText(String text) {
        this.setCursorPositionStart();
        this.setSelectionPositionEnd();
        this.writeText(text);
    }

    public String getSelectedText() {
        if (this.cursorPositionComponent == this.selectionEndComponent) {
            int start = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
            int end = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
            return this.getCurrentLine().substring(start, end);
        } else {
            int startLine;
            int endLine;
            int startChar;
            int endChar;

            if (this.cursorPositionComponent < this.selectionEndComponent) {
                startLine = this.cursorPositionComponent;
                endLine = this.selectionEndComponent;
                startChar = this.cursorPosition;
                endChar = this.selectionEnd;
            } else {
                startLine = this.selectionEndComponent;
                endLine = this.cursorPositionComponent;
                startChar = this.selectionEnd;
                endChar = this.cursorPosition;
            }

            String firstLine = this.getLine(startLine);
            firstLine = firstLine.substring(startChar, firstLine.length());
            String lastLine = this.getLine(endLine).substring(0, endChar);

            String result = firstLine + "\n";

            for (int i = startLine + 1; i < endLine; i++) {
                result += this.getLine(i) + "\n";
            }

            return result + lastLine;
        }
    }

    public void writeText(String text) {
        if (this.cursorPositionComponent != this.selectionEndComponent) {
            int startLine;
            int endLine;
            int startChar;
            int endChar;

            if (this.cursorPositionComponent < this.selectionEndComponent) {
                startLine = this.cursorPositionComponent;
                endLine = this.selectionEndComponent;
                startChar = this.cursorPosition;
                endChar = this.selectionEnd;
            } else {
                startLine = this.selectionEndComponent;
                endLine = this.cursorPositionComponent;
                startChar = this.selectionEnd;
                endChar = this.cursorPosition;
            }

            String firstLine = this.getLine(startLine);
            firstLine = firstLine.substring(0, startChar);
            String lastLine = this.getLine(endLine);
            lastLine = lastLine.substring(endChar, lastLine.length());
            this.chatLines.set(startLine, new ChatComponentText(firstLine + lastLine));

            for (int i = startLine + 1; i <= endLine; i++) {
                this.chatLines.remove(startLine + 1);
            }

            this.setCursorPosition(startLine, startChar);
        }

        String startText = "";
        String endText = "";
        String[] lines = text.split("\n");
        boolean endsWithNewline = text.endsWith("\n");
        if (endsWithNewline) {
            String remaining = text;
            int lineCount = 0;
            do {
                remaining = remaining.substring(0, remaining.length() - 1);
                lineCount++;
            } while (remaining.endsWith("\n"));
            String[] temp = new String[lines.length + lineCount];
            System.arraycopy(lines, 0, temp, 0, lines.length);
            for (int i = 0; i < lineCount; i++) {
                temp[temp.length - lineCount + i] = "";
            }
            lines = temp;
        }
        boolean multiLine = endsWithNewline || lines.length > 1;

        int start = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
        int end = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;

        if (this.getCurrentLine().length() > 0) {
            startText = this.getCurrentLine().substring(0, start);
        }

        if (this.getCurrentLine().length() > 0 && end < this.getCurrentLine().length()) {
            endText = this.getCurrentLine().substring(end);
        }

        if (text.equals("\n")) {
            this.chatLines.set(this.cursorPositionComponent, new ChatComponentText(startText));
            this.chatLines.add(this.cursorPositionComponent + 1, new ChatComponentText(endText));
            this.cursorPositionComponent++;
            this.setCursorPositionStartRow();
            return;
        }

        if (multiLine) {
            for (int i = 0; i < lines.length; i++) {
                String line = ChatAllowedCharacters.filterAllowedCharacters(lines[i]);

                if (i == lines.length - 1) {
                    this.chatLines.add(this.cursorPositionComponent, new ChatComponentText(line + endText));
                    this.setCursorPosition(this.cursorPositionComponent, line.length());
                } else if (i == 0) {
                    this.chatLines.set(this.cursorPositionComponent, new ChatComponentText(startText + line));
                    this.cursorPositionComponent++;
                } else {
                    this.chatLines.add(this.cursorPositionComponent, new ChatComponentText(line));
                    this.cursorPositionComponent++;
                }
            }
        } else {
            this.chatLines.set(this.cursorPositionComponent, new ChatComponentText(startText + lines[0] + endText));
            this.setCursorPosition(this.cursorPositionComponent, startText.length() + lines[0].length());
        }
    }

    public void deleteWords(int direction) {
        if (this.getCurrentLine().length() != 0) {
            if (this.selectionEnd != this.cursorPosition || this.selectionEndComponent != this.cursorPositionComponent) {
                this.writeText("");
            } else {
                this.deleteFromCursor(this.getNthWordFromCursor(direction) - this.cursorPosition);
            }
        } else {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("");
            } else {
                if (this.chatLines.size() > 1) {
                    this.chatLines.remove(this.cursorPositionComponent);
                }
                if (direction < 0) {
                    this.cursorPositionComponent--;
                    this.setCursorPositionEndRow();
                }
            }
        }
    }

    public void deleteFromCursor(int amount) {
        //if (this.getCurrentLine().length() != 0) {
        if (this.selectionEnd != this.cursorPosition || this.selectionEndComponent != this.cursorPositionComponent) {
            this.writeText("");

            this.setCursorPosition(this.cursorPositionComponent, this.cursorPosition);
        } else {
            boolean isNegative = amount < 0;

            if (isNegative) {
                if (this.cursorPosition == 0) {
                    if (this.cursorPositionComponent > 0) {
                        IChatComponent prevLine = this.chatLines.get(this.cursorPositionComponent - 1);
                        IChatComponent currLine = this.chatLines.get(this.cursorPositionComponent);
                        this.chatLines.remove(this.cursorPositionComponent);

                        this.cursorPositionComponent--;
                        this.setCursorPositionEndRow();
                        this.chatLines.set(this.cursorPositionComponent, new ChatComponentText(prevLine.getUnformattedText() + currLine.getUnformattedText()));
                    }
                    return;
                }
            } else {
                if (this.cursorPosition == this.getCurrentLine().length()) {
                    if (this.cursorPositionComponent + 1 < this.chatLines.size()) {
                        IChatComponent currLine = this.chatLines.get(this.cursorPositionComponent);
                        IChatComponent nextLine = this.chatLines.get(this.cursorPositionComponent + 1);
                        this.chatLines.remove(this.cursorPositionComponent + 1);

                        this.chatLines.set(this.cursorPositionComponent, new ChatComponentText(currLine.getUnformattedText() + nextLine.getUnformattedText()));
                    }
                    return;
                }
            }

            int start = isNegative ? this.cursorPosition + amount : this.cursorPosition;
            int end = isNegative ? this.cursorPosition : this.cursorPosition + amount;
            String result = "";

            if (start >= 0) {
                result = this.getCurrentLine().substring(0, start);
            }

            if (end < this.getCurrentLine().length()) {
                result = result + this.getCurrentLine().substring(end);
            }

            this.chatLines.set(this.cursorPositionComponent, new ChatComponentText(result));

            if (isNegative) {
                this.moveCursorBy(amount);
            }

            this.setCursorPosition(this.cursorPositionComponent, this.cursorPosition);
        }
        //}
    }

    public int getNthWordFromCursor(int n) {
        return this.getNthWordFromPos(n, this.getCursorPosition());
    }

    public int getNthWordFromPos(int n, int position) {
        if (n < 0 && position == 0) {
            return -1;
        }
        return this.getNthWordFromPos(n, position, true);
    }

    public int getNthWordFromPos(int n, int position, boolean includeSpace) {
        int currPos = position;
        boolean isNegative = n < 0;
        int absN = Math.abs(n);

        String currentLine = this.getCurrentLine();

        for (int i = 0; i < absN; ++i) {
            if (isNegative) {
                while (includeSpace && currPos > 0 && currentLine.charAt(currPos - 1) == 32) {
                    --currPos;
                }

                while (currPos > 0 && currentLine.charAt(currPos - 1) != 32) {
                    --currPos;
                }
            } else {
                int length = currentLine.length();
                currPos = currentLine.indexOf(32, currPos);

                if (currPos == -1) {
                    currPos = length;
                } else {
                    while (includeSpace && currPos < length && currentLine.charAt(currPos) == 32) {
                        ++currPos;
                    }
                }
            }
        }

        return currPos;
    }

    public void moveCursorLine(boolean up, boolean shift) {
        String prevLine = "";
        int prevLineChar = 0;
        int remainingChars = this.cursorPosition;
        int prevX = 0;

        // XXX
        label:
        for (int i = 0; i < this.chatLines.size(); i++) {
            IChatComponent current = this.chatLines.get(i);
            String text = current.getUnformattedText();
            @SuppressWarnings("unchecked") List<String> lines = this.font.listFormattedStringToWidth(text, this.getWidth());

            int off = 0;
            boolean firstLine = true;

            for (String line : lines) {
                if (remainingChars == Integer.MIN_VALUE) {
                    String temp = this.font.trimStringToWidth(line, prevX);

                    if (firstLine) {
                        this.setCursorPosition(this.cursorPositionComponent + 1, temp.length());
                    } else {
                        this.setCursorPosition(this.cursorPositionComponent, off + temp.length());
                    }

                    return;
                }
                if (i == this.cursorPositionComponent && remainingChars >= 0) {
                    if (remainingChars <= line.length()) {
                        String sub = line.substring(0, remainingChars);
                        if (up) {
                            String temp = this.font.trimStringToWidth(prevLine, this.font.getStringWidth(sub));

                            if (firstLine) {
                                this.setCursorPosition(this.cursorPositionComponent - 1, prevLineChar + temp.length() - prevLine.length());
                            } else {
                                this.setCursorPosition(this.cursorPositionComponent, prevLineChar + temp.length() - prevLine.length());
                            }
                            break label;
                        } else {
                            remainingChars = Integer.MIN_VALUE;

                            prevX = this.font.getStringWidth(sub);

                            off += line.length();
                            if (off < text.length() && text.charAt(off) == ' ') {
                                off++;
                            }

                            prevLineChar = off;
                            prevLine = sub;
                            firstLine = false;

                            continue;
                        }

                        //cursorX += this.font.getStringWidth(sub);
                        //break label;
                    }

                    //if (remainingChars == 0) {
                        //break label;
                    //}

                    remainingChars -= line.length();
                }
                prevX = this.font.getStringWidth(line);

                off += line.length();
                if (off < text.length() && text.charAt(off) == ' ') {
                    off++;
                    line += " ";
                    if (i == this.cursorPositionComponent) {
                        remainingChars--;
                    }
                }

                prevLineChar = off;
                prevLine = line;
                firstLine = false;
            }
        }
    }

    public void moveCursorBy(int count) {
        if (count == 0) {
            return;
        }
        if (this.cursorPosition + count < 0) {
            int diff = this.cursorPosition + count + 1;
            this.cursorPositionComponent--;
            this.setCursorPositionEndRow();
            this.moveCursorBy(diff);
        } else if (this.cursorPosition + count > this.getCurrentLine().length()) {
            int diff = this.cursorPosition - this.getCurrentLine().length();
            int row = this.cursorPositionComponent++;
            this.setCursorPositionStartRow();
            if (row == this.cursorPositionComponent) {
                this.setCursorPositionEndRow();
            }
            this.moveCursorBy(diff);
        } else {
            this.setCursorPosition(this.cursorPositionComponent, this.cursorPosition + count);
        }
    }

    public void moveSelectionBy(int count) {
        if (count == 0) {
            return;
        }
        if (this.selectionEnd + count < 0) {
            int diff = this.selectionEnd + count + 1;
            if (this.selectionEndComponent > 0) {
                this.selectionEndComponent--;
                this.setSelectionPositionEndRow();
                this.moveSelectionBy(diff);
            }
        } else if (this.selectionEnd + count > this.getCurrentLineSelection().length()) {
            int diff = this.selectionEnd - this.getCurrentLineSelection().length();
            int row = this.selectionEndComponent++;
            this.setSelectionPositionStartRow();
            if (row == this.selectionEndComponent) {
                this.setSelectionPositionEndRow();
            }
            this.moveSelectionBy(diff);
        } else {
            this.setSelectionPos(this.selectionEndComponent, this.selectionEnd + count);
        }
    }

    public void setCursorPosition(int row, int pos) {
        this.cursorPositionComponent = row;
        int height = this.chatLines.size();

        if (this.cursorPositionComponent < 0) {
            this.cursorPositionComponent = 0;
        }

        if (this.cursorPositionComponent >= height) {
            this.cursorPositionComponent = height - 1;
        }

        this.cursorPosition = pos;
        int length = this.getCurrentLine().length();

        if (this.cursorPosition < 0) {
            this.cursorPosition = 0;
        }

        if (this.cursorPosition > length) {
            this.cursorPosition = length;
        }

        this.setSelectionPos(this.cursorPositionComponent, this.cursorPosition);
    }

    public void setCursorPositionStart() {
        this.setCursorPosition(0, 0);
    }

    public void setCursorPositionEnd() {
        this.setCursorPosition(this.chatLines.size(), this.getLine(this.chatLines.size() - 1).length());
    }

    public void setCursorPositionStartRow() {
        this.setCursorPosition(this.cursorPositionComponent, 0);
    }

    public void setCursorPositionEndRow() {
        this.setCursorPosition(this.cursorPositionComponent, this.getCurrentLine().length());
    }

    public void setSelectionPositionStart() {
        this.setSelectionPos(0, 0);
    }

    public void setSelectionPos(int row, int pos) {
        int chatHeight = this.chatLines.size();

        if (pos < 0) {
            row--;

            if (row < 0) {
                row = 0;
            }

            if (row >= chatHeight) {
                row = chatHeight - 1;
            }

            pos = this.getLine(row).length();
        }

        if (row < 0) {
            row = 0;
        }

        if (row >= chatHeight) {
            row = chatHeight - 1;
        }

        this.selectionEndComponent = row;
        int length = this.getCurrentLineSelection().length();

        if (pos > length) {
            pos = length;
        }

        if (pos < 0) {
            pos = 0;
        }

        this.selectionEnd = pos;

        if (this.font != null) {
            int totalLines = 0;
            int selectedLine = 0;

            int remainingChars = this.cursorPosition;

            // XXX
            for (int i = 0; i < this.chatLines.size(); i++) {
                IChatComponent current = this.chatLines.get(i);
                String text = current.getUnformattedText();
                @SuppressWarnings("unchecked") List<String> lines = this.font.listFormattedStringToWidth(text, this.width - 8);

                int off = 0;
                for (String line : lines) {
                    totalLines++;

                    if (i <= row) {
                        off += line.length();
                        if (off < text.length() && text.charAt(off) == ' ') {
                            off++;
                            line += " ";
                        }

                        if (i == this.cursorPositionComponent && remainingChars > 0) {
                            if (remainingChars < line.length()) {
                                remainingChars = -1;
                            } else {
                                remainingChars -= line.length();
                            }
                        }

                        if (remainingChars >= 0) {
                            selectedLine++;
                        }
                    }
                }
            }

            int height = this.getHeight() / this.font.FONT_HEIGHT;

            if (totalLines > height) {
                if (totalLines - this.lineScrollOffset < height) { // Don't let it scroll further
                    this.lineScrollOffset = totalLines - height;
                }

                if (selectedLine < this.lineScrollOffset) {
                    this.lineScrollOffset = selectedLine - 1;
                }

                if (selectedLine > this.lineScrollOffset + height + 1) {
                    this.lineScrollOffset = selectedLine - height;
                }

                if (this.lineScrollOffset < 0) { // Don't let it scroll up too far
                    this.lineScrollOffset = 0;
                }

                if (totalLines - this.lineScrollOffset < height) { // Don't let it scroll further
                    this.lineScrollOffset = totalLines - height;
                }
            } else {
                this.lineScrollOffset = 0;
            }
        }
    }

    private String getLine(int line) {
        if (line < 0 || line >= this.chatLines.size()) {
            return "";
        }
        IChatComponent component = this.chatLines.get(line);
        if (component != null) {
            return component.getUnformattedText();
        }
        return "";
    }

    private String getCurrentLineSelection() {
        if (this.selectionEndComponent < 0 || this.selectionEndComponent >= this.chatLines.size()) {
            return "";
        }
        IChatComponent component = this.chatLines.get(this.selectionEndComponent);
        if (component != null) {
            return component.getUnformattedText();
        }
        return "";
    }

    public int getHeight() {
        return this.getEnableBackgroundDrawing() ? this.height - 8 : this.height;
    }

    public boolean getEnableBackgroundDrawing() {
        return this.enableBackgroundDrawing;
    }

    public void setEnableBackgroundDrawing(boolean p_146185_1_) {
        this.enableBackgroundDrawing = p_146185_1_;
    }

    public void setSelectionPositionEnd() {
        this.setSelectionPos(this.chatLines.size(), this.getLine(this.chatLines.size() - 1).length());
    }

    public void setSelectionPositionStartRow() {
        this.setSelectionPos(this.selectionEndComponent, 0);
    }

    public void setSelectionPositionEndRow() {
        this.setSelectionPos(this.selectionEndComponent, this.getCurrentLineSelection().length());
    }

    public boolean textboxKeyTyped(char character, int key) {
        try {
            if (!this.isFocused) {
                return false;
            } else {
                switch (character) {
                    case 1: // Select all
                        this.setCursorPositionEnd();
                        this.setSelectionPos(0, 0);
                        return true;
                    case 3:
                        GuiScreen.setClipboardString(this.getSelectedText());
                        return true;
                    case 22:
                        if (this.isEnabled) {
                            this.writeText(GuiScreen.getClipboardString());
                        }

                        return true;
                    case 24:
                        GuiScreen.setClipboardString(this.getSelectedText());

                        if (this.isEnabled) {
                            this.writeText("");
                        }

                        return true;
                    default:
                        switch (key) {
                            case 14:
                                if (GuiScreen.isCtrlKeyDown()) {
                                    if (this.isEnabled) {
                                        this.deleteWords(-1);
                                    }
                                } else if (this.isEnabled) {
                                    this.deleteFromCursor(-1);
                                }

                                return true;
                            case 199: // Home
                                if (GuiScreen.isShiftKeyDown()) {
                                    this.setSelectionPos(this.selectionEndComponent, 0);
                                } else {
                                    this.setCursorPositionStartRow();
                                }

                                return true;
                            case 200: // Up arrow
                                this.moveCursorLine(true, GuiScreen.isCtrlKeyDown());
                                return true;
                            case 203: // Left arrow
                                if (GuiScreen.isShiftKeyDown()) {
                                    if (GuiScreen.isCtrlKeyDown()) {
                                        this.setSelectionPos(this.selectionEndComponent, this.getNthWordFromPos(-1, this.getSelectionEnd()));
                                    } else {
                                        this.moveSelectionBy(-1);
                                        //this.setSelectionPos(this.selectionEndComponent, this.getSelectionEnd() - 1);
                                    }
                                } else if (GuiScreen.isCtrlKeyDown()) {
                                    this.setCursorPosition(this.cursorPositionComponent, this.getNthWordFromCursor(-1));
                                } else {
                                    this.moveCursorBy(-1);
                                }

                                return true;
                            case 208: // Down arrow
                                this.moveCursorLine(false, GuiScreen.isCtrlKeyDown());
                                return true;
                            case 205: // Right arrow
                                if (GuiScreen.isShiftKeyDown()) {
                                    if (GuiScreen.isCtrlKeyDown()) {
                                        this.setSelectionPos(this.selectionEndComponent, this.getNthWordFromPos(1, this.getSelectionEnd()));
                                    } else {
                                        this.moveSelectionBy(1);
                                        //this.setSelectionPos(this.selectionEndComponent, this.getSelectionEnd() + 1);
                                    }
                                } else if (GuiScreen.isCtrlKeyDown()) {
                                    this.setCursorPosition(this.cursorPositionComponent, this.getNthWordFromCursor(1));
                                } else {
                                    this.moveCursorBy(1);
                                }

                                return true;
                            case 207: // Go to end
                                if (GuiScreen.isShiftKeyDown()) {
                                    this.setSelectionPos(this.selectionEnd, this.getLine(this.selectionEndComponent).length());
                                } else {
                                    this.setCursorPositionEnd();
                                }

                                return true;
                            case 211: // Backspace
                                if (GuiScreen.isCtrlKeyDown()) {
                                    if (this.isEnabled) {
                                        this.deleteWords(1);
                                    }
                                } else if (this.isEnabled) {
                                    this.deleteFromCursor(1);
                                }

                                return true;
                            default:
                                if (ChatAllowedCharacters.isAllowedCharacter(character)) {
                                    if (this.isEnabled) {
                                        this.writeText(Character.toString(character));
                                    }

                                    return true;
                                } else if (character == '\r') {
                                    if (this.isEnabled) {
                                        this.writeText("\n");
                                    }

                                    return true;
                                } else {
                                    return false;
                                }
                        }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            return true;
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
        boolean flag = mouseX >= this.posX && mouseX < this.posX + this.width && mouseY >= this.posY && mouseY < this.posY + this.height;

        if (this.canLoseFocus) {
            this.setFocused(flag);
        }

        if (this.isFocused && button == 0) {
            int cursorPosX = mouseX - this.posX;
            int cursorPosY = mouseY - this.posY;

            if (this.enableBackgroundDrawing) {
                cursorPosX -= 4;
                cursorPosY -= 4;
            }

            int lineY = cursorPosY / this.font.FONT_HEIGHT;
            int remainingLines = -this.lineScrollOffset;

            // XXX
            label:
            {
                for (int i = 0; i < this.chatLines.size(); i++) {
                    IChatComponent current = this.chatLines.get(i);
                    String text = current.getUnformattedText();
                    @SuppressWarnings("unchecked") List<String> lines = this.font.listFormattedStringToWidth(text, this.getWidth());

                    int off = 0;
                    for (String line : lines) {
                        if (remainingLines == lineY) {
                            this.setCursorPosition(i, off + this.font.trimStringToWidth(line, cursorPosX).length());
                            break label;
                        }

                        off += line.length();
                        if (off < text.length() && text.charAt(off) == ' ') {
                            off++;
                        }

                        remainingLines++;
                    }
                }
                this.setCursorPositionEnd();
            }
        }
    }

    public void drawTextBox() {
        try {
            if (this.getVisible()) {
                if (this.getEnableBackgroundDrawing()) {
                    Gui.drawRect(this.posX - 1, this.posY - 1, this.posX + this.width + 1, this.posY + this.height + 1, -6250336);
                    Gui.drawRect(this.posX, this.posY, this.posX + this.width, this.posY + this.height, -16777216);
                }

                int textColor = this.isEnabled ? this.enabledColor : this.disabledColor;
                boolean drawCursor = this.isFocused && this.cursorCounter / 6 % 2 == 0;// && textClipped;
                boolean hasSelection = this.selectionEnd != this.cursorPosition || this.selectionEndComponent != this.cursorPositionComponent;
                int textOffsetX = this.enableBackgroundDrawing ? this.posX + 4 : this.posX;
                int textOffsetY = this.enableBackgroundDrawing ? this.posY + 4 : this.posY;

                boolean drawCursorAsLine = this.cursorPositionComponent + 1 < this.chatLines.size() || this.cursorPosition < this.getCurrentLine().length();
                int cursorX = textOffsetX;
                int cursorY = textOffsetY - this.font.FONT_HEIGHT;

                int remainingChars = this.cursorPosition;
                int remainingCharsSel = this.selectionEnd;
                int remainingLines = -this.lineScrollOffset;

                int selectionEndX = textOffsetX;
                int selectionEndY = textOffsetY - this.font.FONT_HEIGHT;

                int height = this.getHeight() / this.font.FONT_HEIGHT;

                // XXX
                //label:
                for (int i = 0; i < this.chatLines.size(); i++) {
                    IChatComponent current = this.chatLines.get(i);
                    String text = current.getUnformattedText();
                    @SuppressWarnings("unchecked") List<String> lines = this.font.listFormattedStringToWidth(text, this.getWidth());

                    int off = 0;
                    for (String line : lines) {
                        off += line.length();
                        if (off < text.length() && text.charAt(off) == ' ') {
                            off++;
                            line += " ";
                        }

                        /*if (remainingLines > height - 1) {
                            //break label;
                        } else */
                        if (remainingLines >= 0) {
                            if (i <= this.cursorPositionComponent && remainingChars >= 0) {
                                cursorY += this.font.FONT_HEIGHT;
                            }
                            if (i <= this.selectionEndComponent && remainingCharsSel >= 0) {
                                selectionEndY += this.font.FONT_HEIGHT;
                            }
                        }
                        remainingLines++;

                        if (i == this.cursorPositionComponent && remainingChars >= 0) {
                            if (remainingChars <= line.length()) {
                                String sub = line.substring(0, remainingChars);
                                cursorX += this.font.getStringWidth(sub);
                                //break label;
                            }

                            //if (remainingChars == 0) {
                                //break label;
                            //}

                            remainingChars -= line.length();
                        }
                        if (i == this.selectionEndComponent && remainingCharsSel >= 0) {
                            if (remainingCharsSel <= line.length()) {
                                String sub = line.substring(0, remainingCharsSel);
                                selectionEndX += this.font.getStringWidth(sub);
                                //break label;
                            }

                            //if (remainingCharsSel == 0) {
                                //break label;
                            //}

                            remainingCharsSel -= line.length();
                        }
                    }
                }

                int offsetY = 0;
                remainingLines = -this.lineScrollOffset;

                // XXX
                label:
                for (IChatComponent current : this.chatLines) {
                    @SuppressWarnings("unchecked") List<String> lines = this.font.listFormattedStringToWidth(current.getFormattedText(), this.getWidth());
                    for (String line : lines) {
                        if (remainingLines > height - 1) {
                            break label;
                        } else if (remainingLines >= 0) {
                            this.font.drawStringWithShadow(line, textOffsetX, textOffsetY + offsetY, textColor);
                            offsetY += this.font.FONT_HEIGHT;
                        }
                        remainingLines++;
                    }
                }

                if (drawCursor) {
                    if (drawCursorAsLine) {
                        Gui.drawRect(cursorX, cursorY - 1, cursorX + 1, cursorY + 1 + this.font.FONT_HEIGHT, 0xFFD0D0D0);
                    } else {
                        this.font.drawStringWithShadow("_", cursorX, cursorY, textColor);
                    }
                }

                this.font.drawStringWithShadow(this.chatLines.size() + " lines +" + this.lineScrollOffset, this.posX + 4, this.posY + this.height + 2, textColor);
                this.font.drawStringWithShadow(this.cursorPositionComponent + "," + this.cursorPosition + " " + this.selectionEndComponent + "," + this.selectionEnd, this.posX + 4, this.posY + this.height + 11, textColor);
                this.font.drawStringWithShadow(cursorX + "," + cursorY + " " + selectionEndX + "," + selectionEndY, this.posX + 4, this.posY + this.height + 20, textColor);

                if (hasSelection) {
                    if (selectionEndY < cursorY) {
                        int temp = cursorY;
                        cursorY = selectionEndY;
                        selectionEndY = temp;
                        temp = cursorX;
                        cursorX = selectionEndX;
                        selectionEndX = temp;
                    }
                    if (selectionEndY != cursorY) { // Multiline selection
                        // Draw first line
                        this.drawSelection(cursorX, cursorY, this.getWidth() + this.posX, cursorY + this.font.FONT_HEIGHT);
                        // Draw last line
                        this.drawSelection(textOffsetX, selectionEndY, selectionEndX, selectionEndY + this.font.FONT_HEIGHT);
                        // Draw all lines inbetween
                        for (int i = cursorY + this.font.FONT_HEIGHT; i < selectionEndY; i += this.font.FONT_HEIGHT) {
                            this.drawSelection(textOffsetX, cursorY + i - cursorY, this.getWidth() + this.posX, cursorY + i - cursorY + this.font.FONT_HEIGHT);
                        }
                    } else {
                        this.drawSelection(cursorX, cursorY, selectionEndX, selectionEndY + this.font.FONT_HEIGHT);
                    }
                }
            }
        } catch (Throwable e) {
        }
    }

    public boolean getVisible() {
        return this.visible;
    }

    public String getCurrentLine() {
        if (this.cursorPositionComponent < 0 || this.cursorPositionComponent >= this.chatLines.size()) {
            return "";
        }
        IChatComponent component = this.chatLines.get(this.cursorPositionComponent);
        if (component != null) {
            return component.getUnformattedText();
        }
        return "";
    }

    public int getWidth() {
        return this.getEnableBackgroundDrawing() ? this.width - 8 : this.width;
    }

    private void drawSelection(int startX, int startY, int endX, int endY) {
        if (startX < endX) {
            int i1 = startX;
            startX = endX;
            endX = i1;
        }

        if (startY < endY) {
            int i1 = startY;
            startY = endY;
            endY = i1;
        }

        if (endX > this.posX + this.width) {
            endX = this.posX + this.width;
        }

        if (startX > this.posX + this.width) {
            startX = this.posX + this.width;
        }

        GlStateManager.color(0.0F, 0.0F, 1.0F, 1.0F);
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

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public int getCursorPosition() {
        return this.cursorPosition;
    }

    public void setTextColor(int color) {
        this.enabledColor = color;
    }

    public void setDisabledTextColour(int color) {
        this.disabledColor = color;
    }

    public boolean isFocused() {
        return this.isFocused;
    }

    public void setFocused(boolean focused) {
        if (focused && !this.isFocused) {
            this.cursorCounter = 0;
        }

        this.isFocused = focused;
    }

    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }

    public int getSelectionEnd() {
        return this.selectionEnd;
    }

    public void setCanLoseFocus(boolean flag) {
        this.canLoseFocus = flag;
    }
}

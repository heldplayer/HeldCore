package net.specialattack.forge.core.client.gui;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.specialattack.forge.core.client.gui.element.*;
import net.specialattack.forge.core.client.gui.layout.*;
import net.specialattack.forge.core.client.gui.style.StyleDefs;
import net.specialattack.forge.core.client.gui.style.background.ColoredTextureBackground;
import net.specialattack.forge.core.client.gui.style.background.TextureBackground;
import net.specialattack.forge.core.client.gui.style.border.InvisibleBorder;
import net.specialattack.forge.core.client.gui.style.border.SolidBorder;

public class GuiSGTest extends SGScreenRoot {

    private int prevSize = -1;

    public GuiSGTest() {
        SGMenuBar menuRoot = new SGMenuBar();
        {
            SGMenu menu = new SGMenu("File");
            menuRoot.addChild(menu);
            menu.addChild(new SGMenuItem("New Project..."));
            menu.addChild(new SGMenuItem("New Module"));
            menu.addChild(new SGMenuItem("Import Project..."));
            menu.addChild(new SGMenuItem("Import Module..."));
            menu.addChild(new SGMenuItem("New..."));
            menu.addChild(new SGMenuItem("Open..."));
            {
                SGMenu item = new SGMenu("Reopen Project");
                item.addChild(new SGMenuItem("IDEA Workspace"));
                item.addChild(new SGMenuItem("Minecraft Modding"));
                item.addChild(new SGMenuItem("Testing"));
                menu.addChild(item);
            }
            {
                SGMenuItem item = new SGMenuItem("Close Project");
                item.setEnabled(false);
                menu.addChild(item);
            }
            menu.addChild(new SGSeperator());
            menu.addChild(new SGMenuItem("Settings..."));
            menu.addChild(new SGMenuItem("Project Structure..."));
            {
                SGMenu item = new SGMenu("Other Settings");
                item.addChild(new SGMenuItem("Default Settings..."));
                item.addChild(new SGMenuItem("Configure Plugins..."));
                item.addChild(new SGMenuItem("Default Project Structure..."));
                menu.addChild(item);
            }
            menu.addChild(new SGSeperator());
            menu.addChild(new SGMenuItem("Import Settings..."));
            menu.addChild(new SGMenuItem("Export Settings..."));
            menu.addChild(new SGMenuItem("Export to Eclipse..."));
            menu.addChild(new SGSeperator());
            menu.addChild(new SGMenuItem("Save All"));
            menu.addChild(new SGMenuItem("Synchronize"));
            menu.addChild(new SGMenuItem("Invalidate Caches / Restart..."));
            menu.addChild(new SGSeperator());
            menu.addChild(new SGMenuItem("Export to HTML..."));
            menu.addChild(new SGMenuItem("Print..."));
            {
                SGMenu item = new SGMenu("Add to Favorites");
                item.addChild(new SGMenuItem("Minecraft Modding"));
                item.addChild(new SGSeperator());
                item.addChild(new SGMenuItem("Add To New Favorites List"));
                menu.addChild(item);
            }
            menu.addChild(new SGMenuItem("File Encoding"));
            {
                SGMenu item = new SGMenu("Line Seperators");
                item.addChild(new SGMenuItem("CRLF - Windows (\\r\\n)"));
                item.addChild(new SGMenuItem("LF Unix and OS X (\\n)"));
                item.addChild(new SGMenuItem("CR - Classic Mac (\\r)"));
                menu.addChild(item);
            }
            menu.addChild(new SGMenuItem("Make File Read-only"));
            menu.addChild(new SGSeperator());
            menu.addChild(new SGMenuItem("Power Save Mode"));
            menu.addChild(new SGSeperator());
            menu.addChild(new SGMenuItem("Exit"));
        }
        {
            SGMenu menu = new SGMenu("Edit");
            menuRoot.addChild(menu);
            menu.addChild(new SGMenuItem("Undo"));
            menu.addChild(new SGMenuItem("Redo"));
            menu.addChild(new SGSeperator());
            menu.addChild(new SGMenuItem("Cut"));
            menu.addChild(new SGMenuItem("Copy"));
            menu.addChild(new SGMenuItem("Copy Path"));
            menu.addChild(new SGMenuItem("Copy Reference"));
            menu.addChild(new SGMenuItem("Paste"));
            menu.addChild(new SGMenuItem("Paste from History..."));
            menu.addChild(new SGMenuItem("Paste Simple"));
            menu.addChild(new SGMenuItem("Delete"));
            menu.addChild(new SGSeperator());
            {
                SGMenu item = new SGMenu("Find");
                item.addChild(new SGMenuItem("Find..."));
                item.addChild(new SGMenuItem("Replace..."));
                item.addChild(new SGMenuItem("Find Next / Move to Next Occurrence"));
                item.addChild(new SGMenuItem("Find Previous / Move to Previous Occurrence"));
                item.addChild(new SGMenuItem("Find Word at Caret"));
                item.addChild(new SGMenuItem("Select All Occurrences"));
                item.addChild(new SGMenuItem("Add Selection for Next Occurrence"));
                item.addChild(new SGMenuItem("Unselect Occurrence"));
                item.addChild(new SGSeperator());
                item.addChild(new SGMenuItem("Find in Path..."));
                item.addChild(new SGMenuItem("Replace in Path..."));
                item.addChild(new SGMenuItem("Search Structurally..."));
                item.addChild(new SGMenuItem("Replace Structurally..."));
                item.addChild(new SGSeperator());
                item.addChild(new SGMenuItem("Find Usages"));
                item.addChild(new SGMenuItem("Find Usages Settings..."));
                item.addChild(new SGMenuItem("Show Usages"));
                item.addChild(new SGMenuItem("Find Usages in File"));
                item.addChild(new SGMenuItem("Highlight Usages in File"));
                item.addChild(new SGMenuItem("Recent Find Usages"));
                item.addChild(new SGSeperator());
                item.addChild(new SGMenuItem("Find by XPath..."));
                menu.addChild(item);
            }
            {
                SGMenu item = new SGMenu("Macros");
                item.addChild(new SGMenuItem("Play Back Last Macro"));
                item.addChild(new SGMenuItem("Start Macro Recording"));
                item.addChild(new SGMenuItem("Edit Macros"));
                item.addChild(new SGMenuItem("Play Saved Macros"));
                menu.addChild(item);
            }
            menu.addChild(new SGMenuItem("Column Selection Mode"));
            menu.addChild(new SGMenuItem("Select All"));
            menu.addChild(new SGMenuItem("Extend Selection"));
            menu.addChild(new SGMenuItem("Shrink Selection"));
            menu.addChild(new SGSeperator());
            menu.addChild(new SGMenuItem("Complete Current Statement"));
            menu.addChild(new SGMenuItem("Join Lines"));
            menu.addChild(new SGMenuItem("Fill Paragraph"));
            menu.addChild(new SGMenuItem("Duplicate Line"));
            menu.addChild(new SGMenuItem("Indent Selection"));
            menu.addChild(new SGMenuItem("Unindent Line or Selection"));
            menu.addChild(new SGMenuItem("Toggle Case"));
            {
                SGMenu item = new SGMenu("Convert Indents");
                item.addChild(new SGMenuItem("To Spaces"));
                item.addChild(new SGMenuItem("To Tabs"));
                menu.addChild(item);
            }
            menu.addChild(new SGMenuItem("Next Parameter"));
            menu.addChild(new SGMenuItem("Previous Parameter"));
        }
        {
            SGMenu menu = new SGMenu("View");
            menuRoot.addChild(menu);
        }
        {
            SGMenu menu = new SGMenu("Navigate");
            menuRoot.addChild(menu);
        }
        {
            SGMenu menu = new SGMenu("Code");
            menuRoot.addChild(menu);
        }
        {
            SGMenu menu = new SGMenu("Analyze");
            menuRoot.addChild(menu);
        }
        {
            SGMenu menu = new SGMenu("Refactor");
            menuRoot.addChild(menu);
        }
        {
            SGMenu menu = new SGMenu("Build");
            menuRoot.addChild(menu);
        }
        {
            SGMenu menu = new SGMenu("Run");
            menuRoot.addChild(menu);
        }
        {
            SGMenu menu = new SGMenu("Tools");
            menuRoot.addChild(menu);
        }
        {
            SGMenu menu = new SGMenu("VCS");
            menuRoot.addChild(menu);
        }
        {
            SGMenu menu = new SGMenu("Window");
            menuRoot.addChild(menu);
        }
        {
            SGMenu menu = new SGMenu("Help");
            menuRoot.addChild(menu);
        }
        this.setMenu(menuRoot);
        SGComponent root = new SGComponent();
        this.setRoot(root);
        root.setBackground(new ColoredTextureBackground(Gui.optionsBackground, 32.0F, new Color(0x404040)));
        root.setLayoutManager(new BorderedSGLayoutManager());
        //root = new SGComponent();
        {
            SGPanel panel = new SGPanel();
            panel.setBackground(new TextureBackground(new ResourceLocation("textures/blocks/glass_pink.png"), 32.0F));
            panel.setPreferredInnerSize(16, 0);
            root.addChild(panel, BorderedSGLayoutManager.Border.LEFT);

            panel.setLayoutManager(new FlowSGLayoutManager(FlowDirection.VERTICAL, FlowLayout.CENTER));
            {
                SGPanel side = new SGPanel();
                side.setBackground(new TextureBackground(new ResourceLocation("textures/blocks/glass_white.png"), 16.0F));
                side.setPreferredInnerSize(32, 32);
                panel.addChild(side);
            }
            {
                final SGScrollPane side = new SGScrollPane();
                side.setCanScroll(true, true);
                side.setBorder(new SolidBorder(StyleDefs.COLOR_MENU_BORDER, 3)); // FIXME
                side.setBackground(new TextureBackground(new ResourceLocation("textures/blocks/glass_gray.png"), 16.0F));
                side.setPreferredTotalSize(64, 256);
                side.setLayoutManager(new FlowSGLayoutManager(FlowDirection.VERTICAL, FlowLayout.CENTER));
                panel.addChild(side);
                for (int i = 1; i <= 30; i++) {
                    final SGButton element = new SGButton("Test " + i);
                    side.addChild(element, true);
                    element.setMouseHandler(new MouseHandler() {
                        @Override
                        public boolean onClick(int mouseX, int mouseY, int button) {
                            side.removeChild(element);
                            return true;
                        }
                    });
                }
            }
        }
        {
            SGPanel panel = new SGPanel();
            panel.setBackground(new TextureBackground(new ResourceLocation("textures/blocks/glass_orange.png"), 32.0F));
            panel.setPreferredInnerSize(96, 0);
            root.addChild(panel, BorderedSGLayoutManager.Border.RIGHT);
            panel.setBorder(new SolidBorder(new Color(0x44FFFFFF), 3));

            panel.setLayoutManager(new FlowSGLayoutManager(FlowDirection.VERTICAL, FlowLayout.MIN));
            {
                SGTextField text = new SGTextField("Text me!");
                text.setPreferredInnerSize(90, 14);
                panel.addChild(text, true);
            }
            {
                SGPasswordField text = new SGPasswordField("Password?");
                text.setPreferredInnerSize(90, 14);
                panel.addChild(text, true);
            }
            {
                SGProgressBar progressBar = new SGProgressBar(SGProgressBar.State.NORMAL);
                progressBar.setProgress(50.0F);
                panel.addChild(progressBar, true);
            }
            {
                SGProgressBar progressBar = new SGProgressBar(SGProgressBar.State.PAUSED);
                progressBar.setProgress(50.0F);
                panel.addChild(progressBar, true);
            }
            {
                SGProgressBar progressBar = new SGProgressBar(SGProgressBar.State.ERROR);
                progressBar.setProgress(50.0F);
                panel.addChild(progressBar, true);
            }
            {
                SGProgressBar progressBar = new SGProgressBar(SGProgressBar.State.INDETERMINATE);
                panel.addChild(progressBar, true);
            }
            {
                SGProgressBar progressBar = new SGProgressBar(SGProgressBar.State.CONTINUOUS);
                panel.addChild(progressBar, true);
            }
        }
        {
            SGPanel panel = new SGPanel();
            panel.setBackground(new TextureBackground(new ResourceLocation("textures/blocks/glass_blue.png"), 16.0F));
            panel.setPreferredInnerSize(0, 32);
            root.addChild(panel, BorderedSGLayoutManager.Border.TOP);

            panel.setLayoutManager(new FlowSGLayoutManager(FlowDirection.HORIZONTAL, FlowLayout.CENTER));
            {
                SGLabel label = new SGLabel("I want a border\nright here");
                panel.addChild(label, true);
                label.setColor(new Color(0xFFFF8888));
                label.setBorder(new SolidBorder(new Color(0x44337777), 3));
            }
            {
                SGLabel label = new SGLabel("Next label!");
                panel.addChild(label, true);
                label.setColor(new Color(0xFFFF8888));
                label.setBorder(new InvisibleBorder(8));
            }
            {
                SGButton button = new SGButton("I'm actually a button");
                button.setPreferredInnerSize(64, 30);
                button.setColors(StyleDefs.COLOR_TEXTBOX_TEXT_DISABLED, StyleDefs.COLOR_TEXTBOX_TEXT, StyleDefs.COLOR_TEXTBOX_TEXT_DISABLED);
                button.setBackground(StyleDefs.BACKGROUND_TEXTBOX);
                button.setBorder(StyleDefs.BORDER_TEXTBOX);
                panel.addChild(button);
            }
            {
                SGButton button = new SGButton("I'm special");
                //button.setPreferredInnerSize(256.0F, 256.0F);
                button.setBackgrounds(StyleDefs.BACKGROUND_BUTTON_NORMAL, StyleDefs.BACKGROUND_BUTTON_HOVER, StyleDefs.BACKGROUND_BUTTON_DISABLED);
                panel.addChild(button);
            }
            {
                SGTank tank = new SGTank(new FluidTank(new FluidStack(FluidRegistry.WATER, 2000), 3000));
                tank.setPreferredInnerSize(64, 30);
                tank.setBorder(new SolidBorder(new Color(0xFF000000), 1));
                panel.addChild(tank);
            }
            {
                SGCheckbox checkbox = new SGCheckbox();
                checkbox.setMouseHandler(new MouseHandler() {
                    @Override
                    public boolean onClick(int mouseX, int mouseY, int button) {
                        SGComponent.DEBUG = !SGComponent.DEBUG;
                        return false;
                    }
                });
                panel.addChild(checkbox);
            }
            {
                SGComboBox box = new SGComboBox("Test", "Tost", "Tast");
                panel.addChild(box, false);
            }
        }
        {
            SGPanel panel = new SGPanel();
            panel.setBackground(new TextureBackground(new ResourceLocation("textures/blocks/glass_green.png"), 64.0F));
            panel.setPreferredInnerSize(0, 64);
            root.addChild(panel, BorderedSGLayoutManager.Border.BOTTOM);

            panel.setLayoutManager(new FlowSGLayoutManager(FlowDirection.HORIZONTAL, FlowLayout.CENTER));
            {
                SGProgressBar progressBar = new SGProgressBar(SGProgressBar.State.NORMAL, FlowDirection.VERTICAL);
                progressBar.setProgress(50.0F);
                panel.addChild(progressBar, true);
            }
            {
                SGProgressBar progressBar = new SGProgressBar(SGProgressBar.State.PAUSED, FlowDirection.VERTICAL);
                progressBar.setProgress(50.0F);
                panel.addChild(progressBar, true);
            }
            {
                SGProgressBar progressBar = new SGProgressBar(SGProgressBar.State.ERROR, FlowDirection.VERTICAL);
                progressBar.setProgress(50.0F);
                panel.addChild(progressBar, true);
            }
            {
                SGProgressBar progressBar = new SGProgressBar(SGProgressBar.State.INDETERMINATE, FlowDirection.VERTICAL);
                panel.addChild(progressBar, true);
            }
            {
                SGProgressBar progressBar = new SGProgressBar(SGProgressBar.State.CONTINUOUS, FlowDirection.VERTICAL);
                panel.addChild(progressBar, true);
            }
            {
                SGLabel label = new SGLabel("God damnit Techmac and manmaed");
                label.setBorder(new InvisibleBorder(3));
                panel.addChild(label);
                label.setColor(new Color(0xFFFF0000));
            }
            {
                final SGScrollPane side = new SGScrollPane() {
                    @Override
                    public Region predictSize() {
                        return super.predictSize();
                    }

                    @Override
                    public void updateLayout() {
                        super.updateLayout();
                    }

                    @Override
                    public void setDimensions(int left, int top, int width, int height) {
                        super.setDimensions(left, top, width, height);
                    }

                    @Override
                    public void setPreferredInnerSize(int width, int height) {
                        super.setPreferredInnerSize(width, height);
                    }
                };
                side.setCanScroll(true, true);
                side.setBorder(new SolidBorder(StyleDefs.COLOR_MENU_BORDER, 3)); // FIXME
                side.setBackground(new TextureBackground(new ResourceLocation("textures/blocks/glass_gray.png"), 16.0F));
                side.setPreferredTotalSize(256, 64);
                side.setShouldForceSize(true);
                side.setLayoutManager(new FlowSGLayoutManager(FlowDirection.HORIZONTAL, FlowLayout.CENTER));
                panel.addChild(side);
                for (int i = 0; i <= 100; i++) {
                    final SGProgressBar scroll = new SGProgressBar(SGProgressBar.State.NORMAL, FlowDirection.VERTICAL);
                    scroll.setProgress(i);
                    scroll.setMouseHandler(new MouseHandler() {
                        @Override
                        public boolean onClick(int mouseX, int mouseY, int button) {
                            side.removeChild(scroll);
                            return true;
                        }
                    });
                    side.addChild(scroll, true);
                }
            }
        }
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (this.prevSize == -1) {
            this.prevSize = this.mc.gameSettings.guiScale;
            this.mc.gameSettings.guiScale = 0;
            ScaledResolution resolution = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
            this.setWorldAndResolution(this.mc, resolution.getScaledWidth(), resolution.getScaledHeight());
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        if (this.prevSize != -1) {
            this.mc.gameSettings.guiScale = this.prevSize;
        }
    }
}

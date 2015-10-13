package net.specialattack.forge.core.client;

import cpw.mods.fml.client.GuiModList;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Timer;
import net.minecraft.world.ChunkPosition;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.specialattack.forge.core.Assets;
import net.specialattack.forge.core.CommonProxy;
import net.specialattack.forge.core.SpACore;
import net.specialattack.forge.core.client.gui.GuiButtonIcon;
import net.specialattack.forge.core.client.gui.GuiSGTest;
import net.specialattack.forge.core.client.resources.data.*;
import net.specialattack.forge.core.client.shader.*;
import net.specialattack.forge.core.client.texture.IconHolder;
import net.specialattack.forge.core.client.texture.IconTextureMap;
import net.specialattack.forge.core.sync.SyncClientDebug;
import net.specialattack.forge.core.sync.SyncHandler;
import net.specialattack.forge.core.sync.SyncHandlerClient;
import net.specialattack.forge.core.sync.SyncTileEntity;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    public static IIcon iconReportBug;
    public static Timer minecraftTimer;
    public static IMetadataSerializer metadataSerializer;
    public static Set<IconHolder> iconHolders = new HashSet<IconHolder>();
    public static SyncHandlerClient syncClientInstance;

    public static ShaderManager.ShaderBinding colorBlindShader;

    public static Timer getMinecraftTimer() {
        if (ClientProxy.minecraftTimer == null) {
            ClientProxy.minecraftTimer = ObfuscationReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), "timer", "field_71428_T");
        }
        return ClientProxy.minecraftTimer;
    }

    @Override
    public EntityPlayer getClientPlayer() {
        return MC.getPlayer();
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        GLUtil.initialize();
        SpACore.registerIconHolder(ClientProxy.iconReportBug = new IconHolder(Assets.DOMAIN + "report-bug"));

        // Prepare debug settings
        KeyHandler.registerKeyBind(new KeyHandler.KeyData(new KeyBinding("key.spacore:debug", 0, "key.categories.misc"), false) {
            @Override
            public void keyDown(boolean isRepeat) {
                super.keyDown(isRepeat);
                if (!isRepeat) {
                    ClientDebug.setColorMode(ClientDebug.ColorMode.getRandom());
                }
            }
        });
        ClientDebug.setColorMode(ClientDebug.ColorMode.NORMAL);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

        MC.getTextureManager().loadTextureMap(Assets.TEXTURE_MAP, new IconTextureMap(SpACore.config.textureMapId, "textures/spacore"));

        ClientProxy.metadataSerializer = ObfuscationReflectionHelper.getPrivateValue(Minecraft.class, MC.getMc(), "metadataSerializer_", "field_110452_an");
        ClientProxy.metadataSerializer.registerMetadataSectionType(new TextureMetadataSectionSerializer(), TextureMetadataSection.class);
        ClientProxy.metadataSerializer.registerMetadataSectionType(new ShaderMetadataSectionSerializer(), ShaderMetadataSection.class);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);

        ClientProxy.syncClientInstance = SyncHandlerClient.initialize();

        if (SyncHandler.debug) {
            new SyncClientDebug();
        }

        MC.getResourceManager().registerReloadListener(new AdvancedTexturesManager());
        MC.getResourceManager().registerReloadListener(new ShaderManager());

        ClientProxy.colorBlindShader = ShaderManager.getShader(new ResourceLocation("spacore:shaders/color"));
        if (ClientProxy.colorBlindShader != null && ClientProxy.colorBlindShader.getShader() != null) {
            ShaderProgram shader = ClientProxy.colorBlindShader.getShader();
            shader.addCallback(new ShaderCallback() {

                private float time;
                private float prevPartial;

                @Override
                public void call(ShaderProgram program) {
                    ShaderUniform colorCorrection = program.getUniform("colorCorrection");
                    colorCorrection.setMatrix3(true, ClientDebug.COLOR_MATRIX);
                }
            });
        }
    }

    @SubscribeEvent
    public void onChunkUnload(ChunkEvent.Unload event) {
        if (event.world.isRemote) {
            @SuppressWarnings("unchecked") Map<ChunkPosition, TileEntity> tiles = event.getChunk().chunkTileEntityMap;

            for (TileEntity tile : tiles.values()) {
                if (tile instanceof SyncTileEntity) {
                    SyncTileEntity syncTile = (SyncTileEntity) tile;
                    SyncHandlerClient.requestStopTracking(syncTile, (syncTile).tracker.uuid);
                }
            }
        }
    }

    @Override
    public void registerIconHolder(IIcon icon) {
        if (icon instanceof IconHolder) {
            ClientProxy.iconHolders.add((IconHolder) icon);
        }
    }

    @SuppressWarnings("unchecked")
    @SubscribeEvent
    public void onInitGuiPost(InitGuiEvent.Post event) {
        if (SpACore.config.showReportBugs) {
            if (event.gui != null && event.gui instanceof GuiMainMenu) {
                int centerX = event.gui.width / 2;
                int fourthY = event.gui.height / 4;
                int buttonWidth = 20;
                int buttonHeight = 20;

                GuiButton button = new GuiButtonIcon(-123, 0, 0, buttonWidth, buttonHeight, null, ClientProxy.iconReportBug, Assets.TEXTURE_MAP);
                //button.enabled = false;

                if (ClientProxy.addButtonCheckClear(event.gui, new Rectangle(centerX - 124, fourthY + 96, buttonWidth, buttonHeight), button)) {
                    return;
                }
                if (ClientProxy.addButtonCheckClear(event.gui, new Rectangle(centerX + 104, fourthY + 96, buttonWidth, buttonHeight), button)) {
                    return;
                }
                if (ClientProxy.addButtonCheckClear(event.gui, new Rectangle(centerX + 104, fourthY + 132, buttonWidth, buttonHeight), button)) {
                    return;
                }
                if (ClientProxy.addButtonCheckClear(event.gui, new Rectangle(centerX - 124, fourthY + 72, buttonWidth, buttonHeight), button)) {
                    return;
                }
                if (ClientProxy.addButtonCheckClear(event.gui, new Rectangle(centerX + 104, fourthY + 72, buttonWidth, buttonHeight), button)) {
                    return;
                }
                if (ClientProxy.addButtonCheckClear(event.gui, new Rectangle(centerX - 124, fourthY + 48, buttonWidth, buttonHeight), button)) {
                    return;
                }
                if (ClientProxy.addButtonCheckClear(event.gui, new Rectangle(centerX + 104, fourthY + 48, buttonWidth, buttonHeight), button)) {
                    return;
                }
            }
        }
        if (SpACore.config.replaceModOptions) {
            if (event.gui != null && event.gui instanceof GuiIngameMenu) {
                for (int i = 0; i < event.gui.buttonList.size(); i++) {
                    Object obj = event.gui.buttonList.get(i);
                    if (obj instanceof GuiButton && ((GuiButton) obj).id == 12) {
                        event.gui.buttonList.remove(i);
                        break;
                    }
                }
                event.gui.buttonList.add(new GuiButton(12, event.gui.width / 2 + 2, event.gui.height / 4 + 80, 98, 20, "Mods"));
            }
        }
    }

    private static boolean addButtonCheckClear(GuiScreen gui, Rectangle area, GuiButton button) {
        List<GuiButton> buttonList = ObfuscationReflectionHelper.getPrivateValue(GuiScreen.class, gui, "buttonList", "field_146292_n");
        for (GuiButton current : buttonList) {
            if (area.intersects(new Rectangle(current.xPosition, current.yPosition, current.width, current.height))) {
                return false;
            }
        }
        button.xPosition = area.x;
        button.yPosition = area.y;
        button.width = area.width;
        button.height = area.height;
        buttonList.add(button);
        return true;
    }

    @SubscribeEvent
    public void onActionPerformedPost(ActionPerformedEvent.Pre event) {
        if (SpACore.config.showReportBugs) {
            if (event.button != null && event.button.id == -123 && event.gui != null && event.gui instanceof GuiMainMenu) {
                MC.getMc().displayGuiScreen(new GuiSGTest());
                event.setCanceled(true);
                event.button.func_146113_a(MC.getSoundHandler());
            }
        }
        if (SpACore.config.replaceModOptions) {
            if (event.button != null && event.button.id == 12 && event.gui != null && event.gui instanceof GuiIngameMenu) {
                MC.getMc().displayGuiScreen(new GuiModList(event.gui));
                event.setCanceled(true);
                event.button.func_146113_a(MC.getSoundHandler());
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            KeyHandler.tickKeys();
        }
    }

    @Override
    public Side getSide() {
        return Side.CLIENT;
    }
}

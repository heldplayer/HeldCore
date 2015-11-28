package net.specialattack.forge.core.client;

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
import net.minecraft.client.renderer.texture.IIconCreator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Timer;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.client.GuiModList;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.specialattack.forge.core.Assets;
import net.specialattack.forge.core.CommonProxy;
import net.specialattack.forge.core.SpACore;
import net.specialattack.forge.core.client.gui.GuiButtonIcon;
import net.specialattack.forge.core.client.gui.deprecated.GuiSGTest;
import net.specialattack.forge.core.client.resources.data.*;
import net.specialattack.forge.core.client.shader.GLUtil;
import net.specialattack.forge.core.client.shader.ShaderManager;
import net.specialattack.forge.core.client.texture.IconHolder;
import net.specialattack.forge.core.sync.SyncClientDebug;
import net.specialattack.forge.core.sync.SyncHandler;
import net.specialattack.forge.core.sync.SyncHandlerClient;
import net.specialattack.forge.core.sync.SyncTileEntity;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    public static IconHolder iconReportBug;
    public static Timer minecraftTimer;
    public static IMetadataSerializer metadataSerializer;
    public static Set<IconHolder> iconHolders = new HashSet<IconHolder>();
    public static SyncHandlerClient syncClientInstance;

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
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

        MC.getTextureManager().loadTickableTexture(Assets.TEXTURE_MAP, new TextureMap("textures/spacore", new IIconCreator() {
            @Override
            public void registerSprites(TextureMap map) {
                for (IconHolder holder : ClientProxy.iconHolders) {
                    holder.register(map);
                }
            }
        }));

        FMLCommonHandler.instance().bus().register(this);
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
    }

    @SubscribeEvent
    public void onChunkUnload(ChunkEvent.Unload event) {
        if (event.world.isRemote) {
            @SuppressWarnings("unchecked") Map<BlockPos, TileEntity> tiles = event.getChunk().chunkTileEntityMap;

            for (TileEntity tile : tiles.values()) {
                if (tile instanceof SyncTileEntity) {
                    SyncTileEntity syncTile = (SyncTileEntity) tile;
                    SyncHandlerClient.requestStopTracking(syncTile, (syncTile).tracker.uuid);
                }
            }
        }
    }

    @Override
    public void registerIconHolder(IconHolder icon) {
        ClientProxy.iconHolders.add(icon);
    }

    @SuppressWarnings("unchecked")
    @SubscribeEvent
    public void onInitGuiPost(GuiScreenEvent.InitGuiEvent.Post event) {
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
    public void onActionPerformedPost(GuiScreenEvent.ActionPerformedEvent.Pre event) {
        if (SpACore.config.showReportBugs) {
            if (event.button != null && event.button.id == -123 && event.gui != null && event.gui instanceof GuiMainMenu) {
                MC.getMc().displayGuiScreen(new GuiSGTest());
                event.setCanceled(true);
                event.button.playPressSound(MC.getSoundHandler());
            }
        }
        if (SpACore.config.replaceModOptions) {
            if (event.button != null && event.button.id == 12 && event.gui != null && event.gui instanceof GuiIngameMenu) {
                MC.getMc().displayGuiScreen(new GuiModList(event.gui));
                event.setCanceled(true);
                event.button.playPressSound(MC.getSoundHandler());
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

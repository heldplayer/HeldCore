package net.specialattack.forge.core.client;

import cpw.mods.fml.client.GuiModList;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
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
import net.specialattack.forge.core.client.shader.GLUtil;
import net.specialattack.forge.core.client.shader.ShaderManager;
import net.specialattack.forge.core.client.texture.IconHolder;
import net.specialattack.forge.core.client.texture.IconTextureMap;
import net.specialattack.forge.core.sync.ISyncableObjectOwner;
import net.specialattack.forge.core.sync.SyncHandler;
import net.specialattack.forge.core.sync.packet.Packet1TrackingStatus;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    public static IIcon iconReportBug;
    public static Timer minecraftTimer;
    public static IMetadataSerializer metadataSerializer;
    public static Set<IconHolder> iconHolders = new HashSet<IconHolder>();

    public static Timer getMinecraftTimer() {
        if (minecraftTimer == null) {
            minecraftTimer = ObfuscationReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), "timer", "field_71428_T");
        }
        return minecraftTimer;
    }

    @Override
    public EntityPlayer getClientPlayer() {
        return MC.getPlayer();
    }

    @Override
    protected void initializeSyncHandler() {
        SyncHandler.initializeClient();
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

        MC.getRenderEngine().loadTextureMap(Assets.TEXTURE_MAP, new IconTextureMap(SpACore.textureMapId.getValue(), "textures/spacore"));

        metadataSerializer = ObfuscationReflectionHelper.getPrivateValue(Minecraft.class, MC.getMinecraft(), "metadataSerializer_", "field_110452_an");
        metadataSerializer.registerMetadataSectionType(new TextureMetadataSectionSerializer(), TextureMetadataSection.class);
        metadataSerializer.registerMetadataSectionType(new ShaderMetadataSectionSerializer(), ShaderMetadataSection.class);

        FMLCommonHandler.instance().bus().register(this);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);

        MC.getResourceManager().registerReloadListener(new AdvancedTexturesManager());
        MC.getResourceManager().registerReloadListener(new ShaderManager());
    }

    @SubscribeEvent
    public void onChunkUnload(ChunkEvent.Unload event) {
        if (event.world.isRemote) {
            @SuppressWarnings("unchecked") Map<ChunkPosition, TileEntity> tiles = event.getChunk().chunkTileEntityMap;

            for (TileEntity tile : tiles.values()) {
                if (tile instanceof ISyncableObjectOwner) {
                    SpACore.syncPacketHandler.sendPacketToServer(new Packet1TrackingStatus((ISyncableObjectOwner) tile, false));
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
        if (SpACore.showReportBugs.getValue()) {
            if (event.gui != null && event.gui instanceof GuiMainMenu) {
                int guiPosX = event.gui.width / 2;
                int guiPosY = event.gui.height / 4;
                int buttonWidth = 20;
                int buttonHeight = 20;

                GuiButton button = new GuiButtonIcon(-123, 0, 0, buttonWidth, buttonHeight, null, ClientProxy.iconReportBug, Assets.TEXTURE_MAP);
                //button.enabled = false;

                if (ClientProxy.addButtonCheckClear(event.gui, new Rectangle(guiPosX - 124, guiPosY + 96, buttonWidth, buttonHeight), button)) {
                    return;
                }
                if (ClientProxy.addButtonCheckClear(event.gui, new Rectangle(guiPosX + 104, guiPosY + 96, buttonWidth, buttonHeight), button)) {
                    return;
                }
                if (ClientProxy.addButtonCheckClear(event.gui, new Rectangle(guiPosX + 104, guiPosY + 132, buttonWidth, buttonHeight), button)) {
                    return;
                }
                if (ClientProxy.addButtonCheckClear(event.gui, new Rectangle(guiPosX - 124, guiPosY + 72, buttonWidth, buttonHeight), button)) {
                    return;
                }
                if (ClientProxy.addButtonCheckClear(event.gui, new Rectangle(guiPosX + 104, guiPosY + 72, buttonWidth, buttonHeight), button)) {
                    return;
                }
                if (ClientProxy.addButtonCheckClear(event.gui, new Rectangle(guiPosX - 124, guiPosY + 48, buttonWidth, buttonHeight), button)) {
                    return;
                }
                if (ClientProxy.addButtonCheckClear(event.gui, new Rectangle(guiPosX + 104, guiPosY + 48, buttonWidth, buttonHeight), button)) {
                    return;
                }
            }
        }
        if (SpACore.replaceModOptions.getValue()) {
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
        if (SpACore.showReportBugs.getValue()) {
            if (event.button != null && event.button.id == -123 && event.gui != null && event.gui instanceof GuiMainMenu) {
                MC.getMinecraft().displayGuiScreen(new GuiSGTest());
                event.setCanceled(true);
                event.button.func_146113_a(MC.getSoundHandler());
            }
        }
        if (SpACore.replaceModOptions.getValue()) {
            if (event.button != null && event.button.id == 12 && event.gui != null && event.gui instanceof GuiIngameMenu) {
                MC.getMinecraft().displayGuiScreen(new GuiModList(event.gui));
                event.setCanceled(true);
                event.button.func_146113_a(MC.getSoundHandler());
            }
        }
    }

}

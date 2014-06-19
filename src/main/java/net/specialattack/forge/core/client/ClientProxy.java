
package net.specialattack.forge.core.client;

import java.awt.Rectangle;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.ChunkPosition;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.specialattack.forge.core.Assets;
import net.specialattack.forge.core.CommonProxy;
import net.specialattack.forge.core.SpACore;
import net.specialattack.forge.core.client.gui.GuiButtonIcon;
import net.specialattack.forge.core.client.gui.GuiScreenReportBug;
import net.specialattack.forge.core.sync.ISyncableObjectOwner;
import net.specialattack.forge.core.sync.SyncHandler;
import net.specialattack.forge.core.sync.packet.Packet1TrackingStatus;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

        MC.getRenderEngine().loadTextureMap(Assets.TEXTURE_MAP, new TextureMap(SpACore.textureMapId.getValue(), "textures/spacore"));
    }

    @SubscribeEvent
    public void onClientConnectedToServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        SyncHandler.initializationCounter = 20;
    }

    @SubscribeEvent
    public void onClientDisconnectionFromServer(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        SyncHandler.clientSyncables.clear();
    }

    @SubscribeEvent
    public void onChunkUnload(ChunkEvent.Unload event) {
        if (event.world.isRemote) {
            @SuppressWarnings("unchecked")
            Map<ChunkPosition, TileEntity> tiles = event.getChunk().chunkTileEntityMap;
            Iterator<TileEntity> iterator = tiles.values().iterator();

            while (iterator.hasNext()) {
                TileEntity tile = iterator.next();

                if (tile instanceof ISyncableObjectOwner) {
                    SpACore.packetHandler.sendPacketToServer(new Packet1TrackingStatus((ISyncableObjectOwner) tile, false));
                }
            }
        }
    }

    public static IIcon iconReportBug;

    @SubscribeEvent
    public void onTextureStitchedPost(TextureStitchEvent.Pre event) {
        TextureMap map = event.map;

        if (map.getTextureType() == SpACore.textureMapId.getValue()) {
            ClientProxy.iconReportBug = map.registerIcon("spacore:report-bug");
        }
    }

    @SubscribeEvent
    public void onInitGuiPost(InitGuiEvent.Post event) {
        if (SpACore.showReportBugs.getValue()) {
            if (event.gui != null && event.gui instanceof GuiMainMenu) {
                GuiButton button = new GuiButtonIcon(-123, 0, 0, 20, 20, "", ClientProxy.iconReportBug, Assets.TEXTURE_MAP);
                if (ClientProxy.addButtonCheckClear(event.gui, new Rectangle(event.gui.width / 2 - 124, event.gui.height / 4 + 96, 20, 20), button)) {
                    return;
                }
                if (ClientProxy.addButtonCheckClear(event.gui, new Rectangle(event.gui.width / 2 + 104, event.gui.height / 4 + 96, 20, 20), button)) {
                    return;
                }
                if (ClientProxy.addButtonCheckClear(event.gui, new Rectangle(event.gui.width / 2 + 104, event.gui.height / 4 + 132, 20, 20), button)) {
                    return;
                }
                if (ClientProxy.addButtonCheckClear(event.gui, new Rectangle(event.gui.width / 2 - 124, event.gui.height / 4 + 72, 20, 20), button)) {
                    return;
                }
                if (ClientProxy.addButtonCheckClear(event.gui, new Rectangle(event.gui.width / 2 + 104, event.gui.height / 4 + 72, 20, 20), button)) {
                    return;
                }
                if (ClientProxy.addButtonCheckClear(event.gui, new Rectangle(event.gui.width / 2 - 124, event.gui.height / 4 + 48, 20, 20), button)) {
                    return;
                }
                if (ClientProxy.addButtonCheckClear(event.gui, new Rectangle(event.gui.width / 2 + 104, event.gui.height / 4 + 48, 20, 20), button)) {
                    return;
                }
            }
        }
    }

    @SubscribeEvent
    public void onActionPerformedPost(ActionPerformedEvent.Post event) {
        if (SpACore.showReportBugs.getValue()) {
            if (event.button != null && event.button.id == -123 && event.gui != null && event.gui instanceof GuiMainMenu) {
                MC.getMinecraft().displayGuiScreen(new GuiScreenReportBug());
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

}

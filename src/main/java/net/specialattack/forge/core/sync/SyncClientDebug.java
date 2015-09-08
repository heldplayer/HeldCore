package net.specialattack.forge.core.sync;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.specialattack.forge.core.client.MC;

@SideOnly(Side.CLIENT)
public class SyncClientDebug extends Gui {

    public SyncClientDebug() {
        FMLCommonHandler.instance().bus().register(this);
    }

    public static String getShortUUID(UUID uuid) {
        return uuid == null ? "00000000" : uuid.toString().substring(0, 8);
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Minecraft mc = MC.getMc();
            ScaledResolution resolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);

            int left = this.drawClientSyncInfo(1, SyncHandlerClient.globalStorage);
            this.drawClientSyncInfo(left, SyncHandlerClient.worldStorage);

            left = this.drawServerSyncInfo(resolution.getScaledHeight(), 1, SyncHandler.globalStorage);
            Map<UUID, SyncTrackingStorage> perWorldStorage = SyncHandler.syncStorages;
            for (SyncTrackingStorage storage : perWorldStorage.values()) {
                if (storage != SyncHandler.globalStorage) {
                    left = this.drawServerSyncInfo(resolution.getScaledHeight(), left, storage);
                }
            }
        }
    }

    private List<IChatComponent> createComponents(SyncTrackingStorage storage) {
        List<IChatComponent> rows = new ArrayList<IChatComponent>(storage.globalSyncableOwners.size() + storage.globalSyncables.size() + 1);
        if (storage.globalSyncableOwners.size() == 0) {
            rows.add(new ChatComponentText("No global objects").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
        } else {
            for (ISyncableOwner owner : storage.globalSyncableOwners) {
                rows.add(new ChatComponentText(owner.getDebugDisplay() + " (" + SyncClientDebug.getShortUUID(owner.getSyncUUID()) + ")").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN)));
                for (ISyncable syncable : owner.getSyncables().values()) {
                    rows.add(new ChatComponentText("- " + syncable.getDebugDisplay()).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.DARK_GREEN)));
                }
            }
        }
        return rows;
    }

    private int drawClientSyncInfo(int left, SyncTrackingStorage storage) {
        if (storage == null) {
            return left;
        }
        String name = storage.name + " (" + SyncClientDebug.getShortUUID(storage.uuid) + ")";
        FontRenderer font = MC.getFontRenderer();
        int width = font.getStringWidth(name);

        List<IChatComponent> rows = this.createComponents(storage);

        for (IChatComponent row : rows) {
            width = Math.max(width, font.getStringWidth(row.getFormattedText()));
        }

        this.drawGradientRect(left, 1, left + width, (rows.size() + 1) * font.FONT_HEIGHT, 0x90505050, 0x90505050);
        font.drawString(name, left + (width - font.getStringWidth(name)) / 2, 1, 0xFFFFFF00);
        for (int i = 0; i < rows.size(); i++) {
            int top = (i + 1) * font.FONT_HEIGHT + 1;
            font.drawString(rows.get(i).getFormattedText(), left, top, 0xFFE0E0E0);
        }
        return left + width + 1;
    }

    private int drawServerSyncInfo(int height, int left, SyncTrackingStorage storage) {
        if (storage == null) {
            return left;
        }
        String name = storage.name + " (" + SyncClientDebug.getShortUUID(storage.uuid) + ")";
        FontRenderer font = MC.getFontRenderer();
        int width = font.getStringWidth(name);

        List<IChatComponent> rows = this.createComponents(storage);

        int trackingOwners = 0, trackingSyncables = 0;
        if (storage.trackingSyncableOwners != null) {
            trackingOwners = storage.trackingSyncableOwners.size();
        }
        if (storage.trackingSyncables != null) {
            trackingSyncables = storage.trackingSyncables.size();
        }
        rows.add(new ChatComponentText("Tracking: " + trackingOwners + "/" + trackingSyncables).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GRAY)));

        for (IChatComponent row : rows) {
            width = Math.max(width, font.getStringWidth(row.getFormattedText()));
        }

        int renderHeight = (rows.size() + 1) * font.FONT_HEIGHT - 1;

        this.drawGradientRect(left, height - renderHeight, left + width, height - 1, 0x90505050, 0x90505050);
        font.drawString(name, left + (width - font.getStringWidth(name)) / 2, height - renderHeight, 0xFFFFFF00);
        for (int i = 0; i < rows.size(); i++) {
            int top = (i + 1) * font.FONT_HEIGHT - 1;
            font.drawString(rows.get(i).getFormattedText(), left, height - renderHeight + top, 0xFFE0E0E0);
        }
        return left + width + 1;
    }

}

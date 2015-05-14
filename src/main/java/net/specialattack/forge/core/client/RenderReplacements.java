package net.specialattack.forge.core.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import cpw.mods.fml.common.FMLCommonHandler;
import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StringUtils;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.specialattack.forge.core.client.gui.GuiHelper;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class RenderReplacements extends Gui {

    public static void renderCrosshairs(RenderGameOverlayEvent eventParent, int width, int height) {
        Minecraft mc = MC.getMinecraft();
        if (mc.gameSettings.showDebugInfo) {
            // Render the new crosshair
            GL11.glPushMatrix();
            try {
                Entity entity = mc.renderViewEntity;
                GLState.glEnable(GL11.GL_BLEND);
                GLState.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                GLState.glLineWidth(2.0F);
                GLState.glDisable(GL11.GL_TEXTURE_2D);
                GLState.glDepthMask(false);
                GL11.glTranslatef((float) width / 2.0F, (float) height / 2.0F, 0.0F);
                GL11.glScalef(-1.0F, 1.0F, 1.0F);
                MC.getEntityRenderer().orientCamera(ClientProxy.getMinecraftTimer().renderPartialTicks);
                GLState.glBegin(GL11.GL_LINES);
                // X
                GLState.glColor4f(1.0F, 0.0F, 0.0F, 1.0F);
                GL11.glVertex3f(0.0F, 0.0F, 0.0F);
                GL11.glVertex3f(-12.5F, 0.0F, 0.0F);
                // Z
                GLState.glColor4f(0.0F, 0.0F, 1.0F, 1.0F);
                GL11.glVertex3f(0.0F, 0.0F, 0.0F);
                GL11.glVertex3f(0.0F, 0.0F, -12.5F);
                // Y
                GLState.glColor4f(0.0F, 1.0F, 0.0F, 1.0F);
                GL11.glVertex3f(0.0F, 0.0F, 0.0F);
                GL11.glVertex3f(0.0F, -8.25F, 0.0F);
                GLState.glEnd();
                GLState.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GLState.glDepthMask(true);
                GLState.glEnable(GL11.GL_TEXTURE_2D);
                GLState.glDisable(GL11.GL_BLEND);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
            GL11.glPopMatrix();
        } else {
            // Try the default crosshair
            if (MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Pre(eventParent, RenderGameOverlayEvent.ElementType.CROSSHAIRS)))
                return;
            MC.getRenderEngine().bindTexture(Gui.icons);
            GLState.glEnable(GL11.GL_BLEND);
            GLState.glBlendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ONE_MINUS_SRC_COLOR, 1, 0);
            GuiHelper.drawTexturedModalRect(width / 2 - 7, height / 2 - 7, 0, 0, 16, 16);
            GLState.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            GLState.glDisable(GL11.GL_BLEND);
            MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Post(eventParent, RenderGameOverlayEvent.ElementType.CROSSHAIRS));
        }
    }

    public static void renderHUDText(RenderGameOverlayEvent eventParent, int width, int height) {
        Minecraft mc = MC.getMinecraft();
        mc.mcProfiler.startSection("forgeHudText");
        GLState.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        ArrayList<String> left = new ArrayList<String>();
        ArrayList<String> right = new ArrayList<String>();

        // Demo text
        WorldClient world = mc.theWorld;
        if (mc.isDemo() && world != null) {
            long time = world.getTotalWorldTime();
            if (time >= 120500L) {
                right.add(I18n.format("demo.demoExpired"));
            } else {
                right.add(I18n.format("demo.remainingTime", StringUtils.ticksToElapsedTime((int) (120500L - time))));
            }
        }

        // Debug text
        if (mc.gameSettings.showDebugInfo && !MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Pre(eventParent, RenderGameOverlayEvent.ElementType.DEBUG))) {
            mc.mcProfiler.startSection("debug");
            // Main debug info
            left.add(String.format("Minecraft %1$s (%1$s/%2$s)", MinecraftForge.MC_VERSION, ClientBrandRetriever.getClientModName()));
            left.add(mc.debug);
            left.add(mc.debugInfoRenders());
            left.add(mc.getEntityDebug());
            left.add(mc.debugInfoEntities());
            left.add(mc.getWorldProviderName());
            left.add(null);

            EntityClientPlayerMP player = mc.thePlayer;
            int x = MathHelper.floor_double(player.posX);
            int y = MathHelper.floor_double(player.boundingBox.minY);
            int z = MathHelper.floor_double(player.posZ);
            float yaw = player.rotationYaw;
            int heading = MathHelper.floor_double((double) (yaw * 4.0F / 360.0F) + 0.5D) & 3;
            String headingString = "Invalid";

            switch (heading) {
                case 0:
                    headingString = "Towards positive Z";
                    break;
                case 1:
                    headingString = "Towards negative X";
                    break;
                case 2:
                    headingString = "Towards negative Z";
                    break;
                case 3:
                    headingString = "Towards positive X";
            }

            // Player position and facing
            left.add(String.format("XYZ: %.3f / %.5f / %.3f", player.posX, player.boundingBox.minY, player.posZ));
            left.add(String.format("Block: %d %d %d", x, y, z));
            left.add(String.format("Chunk: %d %d %d in %d %d %d", x >> 4, y >> 4, z >> 4, x & 15, y & 15, z & 15));
            left.add(String.format("Facing: %s (%s) (%.1f / %.1f)", Direction.directions[heading], headingString, MathHelper.wrapAngleTo180_float(yaw), MathHelper.wrapAngleTo180_float(player.rotationPitch)));

            // Biome and light information
            if (world != null && world.blockExists(x, y, z)) {
                Chunk chunk = world.getChunkFromBlockCoords(x, z);
                left.add(String.format("Biome: %s", chunk.getBiomeGenForWorldCoords(x & 15, z & 15, world.getWorldChunkManager()).biomeName));
                left.add(String.format("Light: %d (%d sky, %d block)", chunk.getBlockLightValue(x & 15, y, z & 15, 0), chunk.getSavedLightValue(EnumSkyBlock.Sky, x & 15, y, z & 15), chunk.getSavedLightValue(EnumSkyBlock.Block, x & 15, y, z & 15)));
            } else {
                left.add(null);
            }

            // Current shader
            if (mc.entityRenderer != null && mc.entityRenderer.isShaderActive()) {
                left.add(String.format("Shader: %s", mc.entityRenderer.getShaderGroup().getShaderGroupName()));
            }

            // Memory usage
            long maxMemory = Runtime.getRuntime().maxMemory();
            long totalMemory = Runtime.getRuntime().totalMemory();
            long freeMemory = Runtime.getRuntime().freeMemory();
            long usedMemory = totalMemory - freeMemory;
            right.add(String.format("Java: %s %dbit", System.getProperty("java.version"), mc.isJava64bit() ? 64 : 32));
            right.add(String.format("Mem: %3d%% %03d/%03dMB", usedMemory * 100L / maxMemory, bytesToMb(usedMemory), bytesToMb(maxMemory)));
            right.add(String.format("Allocated: %3d%% %03dMB", totalMemory * 100L / maxMemory, bytesToMb(totalMemory)));
            right.add(null);
            // Display and OpenGL information
            right.add(String.format("Display: %dx%d (%s)", Display.getWidth(), Display.getHeight(), GL11.glGetString(GL11.GL_VENDOR)));
            right.add(GL11.glGetString(GL11.GL_RENDERER));
            right.add(GL11.glGetString(GL11.GL_VERSION));
            right.add(null);
            // Client brandings
            right.addAll(FMLCommonHandler.instance().getBrandings(false));

            // Hovered over block
            if (world != null) {
                MovingObjectPosition position = mc.objectMouseOver;
                if (position != null && position.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    Block block = world.getBlock(position.blockX, position.blockY, position.blockZ);
                    int meta = world.getBlockMetadata(position.blockX, position.blockY, position.blockZ);

                    right.add(null);
                    right.add(String.valueOf(Block.blockRegistry.getNameForObject(block)));
                    right.add(String.format("Metadata: %d (%4s)", meta, RenderReplacements.formatBinary(meta, 4)));
                    left.add(String.format("Looking at: %d %d %d", position.blockX, position.blockY, position.blockZ));
                }
            }

            mc.mcProfiler.endSection(); // End "debug" section
            MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Post(eventParent, RenderGameOverlayEvent.ElementType.DEBUG));
        }

        RenderGameOverlayEvent.Text event = new RenderGameOverlayEvent.Text(eventParent, left, right);
        if (!MinecraftForge.EVENT_BUS.post(event)) {
            mc.mcProfiler.startSection("render");
            FontRenderer font = mc.fontRenderer;
            int top = 2;
            for (String msg : left) {
                if (msg == null) {
                    top += font.FONT_HEIGHT;
                    continue;
                }
                Gui.drawRect(1, top - 1, 2 + font.getStringWidth(msg) + 1, top + font.FONT_HEIGHT - 1, 0x90505050);
                font.drawString(msg, 2, top, 0xFFE0E0E0);
                top += font.FONT_HEIGHT;
            }

            top = 2;
            for (String msg : right) {
                if (msg == null) {
                    top += font.FONT_HEIGHT;
                    continue;
                }
                int w = font.getStringWidth(msg);
                int l = width - 2 - w;
                Gui.drawRect(l - 1, top - 1, l + w + 1, top + font.FONT_HEIGHT - 1, 0x90505050);
                font.drawString(msg, l, top, 0xFFE0E0E0);
                top += font.FONT_HEIGHT;
            }
            mc.mcProfiler.endSection(); // End "render" section
        }

        mc.mcProfiler.endSection(); // End "forgeHudText"
        MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Post(eventParent, RenderGameOverlayEvent.ElementType.TEXT));
    }

    private static long bytesToMb(long bytes) {
        return bytes / 1024L / 1024L;
    }

    private static String formatBinary(int number, int count) {
        StringBuilder numbers = new StringBuilder(Integer.toBinaryString(number));
        while (numbers.length() < count) {
            numbers.insert(0, '0');
        }
        StringBuilder result = new StringBuilder();
        char prev = 0;
        for (char character : numbers.toString().toCharArray()) {
            if (character != prev) {
                if (character == '1') {
                    result.append(ChatFormatting.GREEN);
                } else {
                    result.append(ChatFormatting.RED);
                }
            }
            result.append(character);
            prev = character;
        }
        return result.append(ChatFormatting.RESET).toString();
    }

}

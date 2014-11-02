package net.specialattack.forge.core.client.shader;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.*;
import java.util.concurrent.Callable;
import net.minecraft.client.shader.Framebuffer;
import net.minecraftforge.client.event.RenderWorldEvent;
import net.minecraftforge.common.MinecraftForge;

@SideOnly(Side.CLIENT)
public final class FrameBufferStorage {

    private static final Map<Framebuffer, Long> framebuffers = new IdentityHashMap<Framebuffer, Long>();
    private static final List<Callable<Void>> delayedTasks = new ArrayList<Callable<Void>>();
    private static boolean initialized = false;

    private FrameBufferStorage() {
    }

    @SubscribeEvent
    public void onRender(RenderWorldEvent.Post event) {
        synchronized (FrameBufferStorage.delayedTasks) {
            for (Callable<Void> task : FrameBufferStorage.delayedTasks) {
                try {
                    task.call();
                } catch (Exception e) {
                }
            }
            FrameBufferStorage.delayedTasks.clear();
        }
    }

    public static void updateBuffer(Framebuffer framebuffer) {
        synchronized (FrameBufferStorage.framebuffers) {
            framebuffers.put(framebuffer, System.currentTimeMillis());
        }
    }

    public static void removeBuffer(Framebuffer framebuffer) {
        synchronized (FrameBufferStorage.framebuffers) {
            framebuffers.remove(framebuffer);
            //System.out.println("Freed up framebuffer " + framebuffer.framebufferObject);
        }
    }

    public static void initialize() {
        if (!initialized) {
            initialized = true;
            MinecraftForge.EVENT_BUS.register(new FrameBufferStorage());
            Thread thread = new Thread(new FrameBufferInvalidator(), "Frame buffer invalidation Thread");
            thread.setDaemon(true);
            thread.start();
        }
    }

    private static class FrameBufferInvalidator implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    synchronized (FrameBufferStorage.framebuffers) {
                        Iterator<Map.Entry<Framebuffer, Long>> i = FrameBufferStorage.framebuffers.entrySet().iterator();
                        while (i.hasNext()) {
                            Map.Entry<Framebuffer, Long> entry = i.next();
                            if (entry.getValue() + 60000L < System.currentTimeMillis()) { // 60 second timeout
                                final Framebuffer framebuffer = entry.getKey();
                                FrameBufferStorage.delayedTasks.add(new Callable<Void>() {
                                    @Override
                                    public Void call() throws Exception {
                                        synchronized (FrameBufferStorage.delayedTasks) {
                                            framebuffer.deleteFramebuffer();
                                        }
                                        //System.out.println("Freed up framebuffer " + framebuffer.framebufferObject);
                                        return null;
                                    }
                                });
                                i.remove();
                            }
                        }
                    }

                    Thread.sleep(10000L); // Run every 10 seconds
                } catch (Exception e) {
                    new RuntimeException("Error with Frame Buffer invalidation", e).printStackTrace();
                    return;
                }
            }
        }
    }

}

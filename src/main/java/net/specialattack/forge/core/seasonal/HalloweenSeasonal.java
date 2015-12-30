package net.specialattack.forge.core.seasonal;

import java.util.Calendar;
import java.util.Random;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Timer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.specialattack.forge.core.Objects;
import net.specialattack.forge.core.SpACore;
import net.specialattack.forge.core.client.ClientHooks;
import net.specialattack.forge.core.client.MC;
import net.specialattack.util.math.MathHelper;
import org.lwjgl.util.vector.Matrix3f;

@SideOnly(Side.CLIENT)
public class HalloweenSeasonal implements ISeasonal {

    private boolean inEffect = false;
    private int effectLength = 0;
    private int remaining = 0;
    private int nextRun, nextSound;
    private float strength = 0.0F;
    private Random rand = new Random();

    private ClientHooks.ScreenColorizer halloweenColorizer = new ClientHooks.ScreenColorizer() {
        private float time;

        @Override
        public Matrix3f apply(Matrix3f original) {
            if (!SpACore.config.enableSeasonals) {
                return original;
            }
            Matrix3f intermediate = new Matrix3f();
            float strength = net.minecraft.util.MathHelper.clamp_float(HalloweenSeasonal.this.strength, 0.0F, 1.0F);
            intermediate.m00 = MathHelper.partial(1.0F, 0.75F, strength);
            intermediate.m10 = MathHelper.partial(0.0F, 0.125F, strength);
            intermediate.m20 = MathHelper.partial(0.0F, 0.125F, strength);
            intermediate.m01 = MathHelper.partial(0.0F, 0.125F, strength);
            intermediate.m11 = MathHelper.partial(1.0F, 0.5F, strength);
            intermediate.m21 = MathHelper.partial(0.0F, 0.125F, strength);
            intermediate.m02 = MathHelper.partial(0.0F, 0.0F, strength);
            intermediate.m12 = MathHelper.partial(0.0F, 0.0F, strength);
            intermediate.m22 = MathHelper.partial(1.0F, 0.0F, strength);
            if (original == null) {
                return intermediate;
            }
            return Matrix3f.mul(original, intermediate, new Matrix3f());
        }

        @Override
        public int getPriority() {
            return 0;
        }

        @Override
        public String getIdentifier() {
            return "spacore:seasonal-halloween";
        }

        @Override
        public boolean isDirty() {
            if (!SpACore.config.enableSeasonals) {
                if (this.time != 0.0F) {
                    this.time = 0.0F;
                    HalloweenSeasonal.this.strength = 0.0F;
                    return true;
                }
                return false;
            }
            if (!HalloweenSeasonal.this.inEffect) {
                if (this.time != 0.0F) {
                    this.time = 0.0F;
                    HalloweenSeasonal.this.strength = 0.0F;
                    return true;
                }
                return false;
            }
            Timer timer = net.specialattack.forge.core.client.ClientProxy.getMinecraftTimer();
            float partial = timer.renderPartialTicks;
            this.time = (float) (HalloweenSeasonal.this.effectLength - HalloweenSeasonal.this.remaining) + partial;
            float strength = HalloweenSeasonal.this.strength;
            float length = (float) HalloweenSeasonal.this.effectLength;
            float newStrength = (-(this.time * this.time) + this.time * length) * 8.0F / (length * length);
            boolean changed = false;
            if (strength < 0.0F && newStrength > 0.0F) {
                changed = true;
            } else if (strength > 0.0F && newStrength < 0.0F) {
                changed = true;
            } else if (strength > 1.0F && newStrength < 1.0F) {
                changed = true;
            } else if (strength < 1.0F && newStrength > 1.0F) {
                changed = true;
            } else if (strength >= 0.0F && strength <= 1.0F && newStrength >= 0.0F && newStrength <= 1.0F && strength != newStrength) {
                changed = true;
            }
            HalloweenSeasonal.this.strength = newStrength;
            return changed;
        }
    };

    @Override
    public void init() {
        ClientHooks.addColorizer(this.halloweenColorizer);
        MinecraftForge.EVENT_BUS.register(this);
        this.nextRun = 6000; // Start after 300 seconds
        //this.nextRun = 200; // Start after 10 seconds
    }

    @Override
    public boolean itsTheSeason() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH) == Calendar.OCTOBER && calendar.get(Calendar.DAY_OF_MONTH) == 31;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (!SpACore.config.enableSeasonals) {
            return;
        }
        if (!this.inEffect) {
            this.nextRun--;
            if (this.nextRun <= 0) {
                Objects.log.info("Spoooooopy sounds! (Disable in the config, or use the in-game menu)");
                this.inEffect = true;
                this.effectLength = this.rand.nextInt(400) + 200; // Last 10-30 seconds
                this.remaining = this.effectLength;
            }
        }
        if (this.inEffect) {
            if (this.remaining <= 0) {
                this.inEffect = false;
                this.nextRun = this.rand.nextInt(2400) + 6000; // Re-reun after 5-7 minutes
                //this.nextRun = this.rand.nextInt(80) + 20; // Re-reun after 1-5 seconds
            }
            this.remaining--;

            this.nextSound--;
            if (this.nextSound <= 0) {
                this.nextSound = this.rand.nextInt(60) + 40; // Next after 2-5 seconds
                EntityPlayerSP player = MC.getPlayer();
                String sound = null;
                float volume = 1.0F;
                float pitch = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F;
                switch (this.rand.nextInt(2)) {
                    case 0:
                        sound = "mob.skeleton.say";
                        break;
                    case 1:
                        sound = "mob.creeper.say";
                        volume = 0.2F;
                        break;
                }
                if (sound != null) {
                    if (player != null) {
                        MC.getSoundHandler().playSound(new PositionedSoundRecord(new ResourceLocation(sound), volume, pitch, (float) player.posX, (float) player.posY, (float) player.posZ));
                    } else {
                        MC.getSoundHandler().playSound(new PositionedSoundRecord(new ResourceLocation(sound), volume, pitch, 0.0F, 0.0F, 0.0F));
                    }
                }
            }
        }
    }
}

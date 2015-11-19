package net.specialattack.forge.core.world.storage;

import java.util.UUID;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

public class SpACoreWorldData extends WorldSavedData {

    private static final String key = "spacore";

    private UUID syncUUID;

    public SpACoreWorldData() {
        this(SpACoreWorldData.key);
    }

    public SpACoreWorldData(String key) {
        super(key);
    }

    public static SpACoreWorldData getData(World world) {
        SpACoreWorldData result = (SpACoreWorldData) world.getPerWorldStorage().loadData(SpACoreWorldData.class, SpACoreWorldData.key);
        if (result == null) {
            result = new SpACoreWorldData();
            result.fillInDefaults();
            world.getPerWorldStorage().setData(SpACoreWorldData.key, result);
        }
        return result;
    }

    private void fillInDefaults() {
        this.markDirty();
        this.syncUUID = UUID.randomUUID();
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        String uuid = tag.getString("syncUUID");
        if (uuid.isEmpty()) {
            this.syncUUID = UUID.randomUUID();
        } else {
            this.syncUUID = UUID.fromString(uuid);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        tag.setString("syncUUID", this.syncUUID.toString());
    }

    public UUID getSyncUUID() {
        return this.syncUUID;
    }
}

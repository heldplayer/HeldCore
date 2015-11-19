package net.specialattack.forge.core.client.resources.data;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.resources.data.IMetadataSection;

public class ShaderMetadataSection implements IMetadataSection {

    public List<String> uniforms = new ArrayList<String>();
    public List<String> attributes = new ArrayList<String>();

}

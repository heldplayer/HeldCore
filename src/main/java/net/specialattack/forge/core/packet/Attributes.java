package net.specialattack.forge.core.packet;

import io.netty.buffer.ByteBuf;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.specialattack.forge.core.CommonProxy;
import net.specialattack.util.Table;

public class Attributes {

    public static final AttributeKey<EntityPlayer> SENDING_PLAYER = new AttributeKey<EntityPlayer>("spacore:sendingPlayer");
    public static final AttributeKey<UUID> TARGET_PLAYER = new AttributeKey<UUID>("spacore:targetPlayer");
    public static final AttributeKey<BlockPosition> BLOCK_POSITION = new AttributeKey<BlockPosition>("spacore:blockPosition");

    static final Table<String, AttributeKey<?>, AttributeHandler<?>> attributeHandlers = new Table<String, AttributeKey<?>, AttributeHandler<?>>();

    static {
        registerAttribute(SENDING_PLAYER, new AttributeHandler<EntityPlayer>() {
            @Override
            public EntityPlayer readValue(ByteBuf in) {
                int length = in.readInt();
                if (length == 0) {
                    return null;
                }
                byte[] bytes = new byte[length];
                in.readBytes(bytes);
                UUID uuid = UUID.fromString(new String(bytes));
                return CommonProxy.getPlayerFromUUID(uuid);
            }

            @Override
            public void writeValue(ByteBuf out, EntityPlayer value) {
                UUID uuid = value.getUniqueID();
                if (uuid != null) {
                    byte[] bytes = uuid.toString().getBytes();
                    out.writeInt(bytes.length);
                    out.writeBytes(bytes);
                } else {
                    out.writeInt(0);
                }
            }
        });
        registerAttribute(BLOCK_POSITION, new AttributeHandler<BlockPosition>() {
            @Override
            public BlockPosition readValue(ByteBuf in) {
                return BlockPosition.readBlockPosition(in);
            }

            @Override
            public void writeValue(ByteBuf out, BlockPosition value) {
                BlockPosition.writeBlockPosition(out, value);
            }
        });
    }

    public static <T> void registerAttribute(AttributeKey<T> attribute, AttributeHandler<T> handler) {
        attributeHandlers.insert(attribute.name(), attribute, handler);
    }

    @SuppressWarnings("unchecked")
    public static void readAttribute(SpACorePacket context, String id, ByteBuf in) {
        Table.Value<AttributeKey<?>, AttributeHandler<?>> value = attributeHandlers.getValue(id);
        if (value != null) {
            ((Attribute) context.attr(value.getValue1())).set(value.getValue2().readValue(in));
        }
    }

    @SuppressWarnings("unchecked")
    public static void writeAttribute(SpACorePacket context, String id, ByteBuf out) {
        Table.Value<AttributeKey<?>, AttributeHandler<?>> value = attributeHandlers.getValue(id);
        if (value != null) {
            ((AttributeHandler) value.getValue2()).writeValue(out, context.attr(value.getValue1()).get());
        }
    }

    public static void readAttributes(SpACorePacket context, ByteBuf in) {
        int length = in.readInt();
        while (length != 0) {
            byte[] nameBytes = new byte[length];
            in.readBytes(nameBytes);
            String name = new String(nameBytes);
            readAttribute(context, name, in);
            length = in.readInt();
        }
    }

    public static void writeAttributes(SpACorePacket context, ByteBuf out) {
        for (Attribute<?> attribute : context.map.values()) {
            AttributeKey<?> key = attribute.key();
            if (context.attr(key).get() != null) {
                byte[] nameBytes = key.name().getBytes();
                out.writeInt(nameBytes.length);
                out.writeBytes(nameBytes);
                writeAttribute(context, key.name(), out);
            }
        }
        out.writeInt(0);
    }

    public static abstract class AttributeHandler<T> {

        abstract T readValue(ByteBuf in);

        abstract void writeValue(ByteBuf out, T value);

    }

}

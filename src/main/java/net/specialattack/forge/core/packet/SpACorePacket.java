package net.specialattack.forge.core.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.AttributeMap;
import java.io.IOException;
import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;

public abstract class SpACorePacket implements AttributeMap {

    public final World world;
    public Map<AttributeKey<?>, Attribute<?>> map;

    public SpACorePacket(World world) {
        this.world = world;
    }

    public boolean isMapPacket() {
        return false;
    }

    public void requireAttribute(AttributeKey<?> attribute) {
        if (this.map == null || !this.map.containsKey(attribute)) {
            throw new IllegalStateException(String.format("Expected attribute %s but it was not there.", attribute.name()));
        }
    }

    public void requireAttribute(AttributeKey<?> attribute, AttributeMap map) {
        if (map.attr(attribute).get() != null) {
            throw new IllegalStateException(String.format("Expected attribute %s but it was not there.", attribute.name()));
        }
    }

    public String getDebugInfo() {
        return this.toString();
    }

    public abstract Side getSendingSide();

    public abstract void read(ChannelHandlerContext context, ByteBuf in) throws IOException;

    public abstract void write(ChannelHandlerContext context, ByteBuf out) throws IOException;

    public abstract void onData(ChannelHandlerContext context);

    @Override
    public <T> Attribute<T> attr(AttributeKey<T> key) {
        if (map == null) {
            map = new IdentityHashMap<AttributeKey<?>, Attribute<?>>(2);
        }
        Map<AttributeKey<?>, Attribute<?>> map = this.map;

        synchronized (map) {
            @SuppressWarnings("unchecked") Attribute<T> attr = (Attribute<T>) map.get(key);
            if (attr == null) {
                attr = new PacketAttribute<T>(map, key);
                map.put(key, attr);
            }
            return attr;
        }
    }

    private static final class PacketAttribute<T> implements Attribute<T> {

        private static final long serialVersionUID = -2661411462200283011L;

        private final Map<AttributeKey<?>, Attribute<?>> map;
        private final AttributeKey<T> key;
        private T value;

        PacketAttribute(Map<AttributeKey<?>, Attribute<?>> map, AttributeKey<T> key) {
            this.map = map;
            this.key = key;
        }

        @Override
        public AttributeKey<T> key() {
            return key;
        }

        @Override
        public T setIfAbsent(T value) {
            while (!compareAndSet(null, value)) {
                T old = get();
                if (old != null) {
                    return old;
                }
            }
            return null;
        }

        @Override
        public T getAndRemove() {
            T oldValue = getAndSet(null);
            synchronized (map) {
                map.remove(key);
            }
            return oldValue;
        }

        @Override
        public void remove() {
            set(null);
            synchronized (map) {
                map.remove(key);
            }
        }

        @Override
        public T get() {
            return this.value;
        }

        @Override
        public void set(T value) {
            this.value = value;
        }

        @Override
        public T getAndSet(T value) {
            T old = get();
            set(value);
            return old;
        }

        @Override
        public boolean compareAndSet(T oldValue, T newValue) {
            if (oldValue == get()) {
                set(newValue);
                return true;
            }
            return false;
        }
    }

}

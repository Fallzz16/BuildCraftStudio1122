package dev.buildcraftstudio.net;

import dev.buildcraftstudio.server.PlacementManager;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/** Coordinates and safe block ID only: the client never supplies NBT or authorization. */
public final class PlacePacket implements IMessage {
    public int dimension;
    public final List<Entry> entries = new ArrayList<>();
    public PlacePacket() { }
    public PlacePacket(int dimension) { this.dimension = dimension; }
    public void add(int x, int y, int z, int blockId, int meta) {
        entries.add(new Entry(x,y,z,blockId,meta));
    }
    @Override public void toBytes(ByteBuf buf) {
        buf.writeInt(dimension);
        buf.writeInt(entries.size());
        for (Entry e : entries) {
            buf.writeInt(e.x); buf.writeInt(e.y); buf.writeInt(e.z);
            buf.writeInt(e.blockId); buf.writeInt(e.meta);
        }
    }
    @Override public void fromBytes(ByteBuf buf) {
        dimension = buf.readInt();
        int count = buf.readInt();
        if (count < 1 || count > PlacementManager.MAX_PLACEMENT || buf.readableBytes() != count * 20) {
            throw new DecoderException("Invalid BuildCraft placement payload");
        }
        for (int i=0; i<count; i++) {
            entries.add(new Entry(buf.readInt(),buf.readInt(),buf.readInt(),buf.readInt(),buf.readInt()));
        }
    }
    public static final class Entry {
        public final int x,y,z,blockId,meta;
        public Entry(int x,int y,int z,int blockId,int meta) {
            this.x=x; this.y=y; this.z=z; this.blockId=blockId; this.meta=meta;
        }
    }
    public static final class Handler implements IMessageHandler<PlacePacket, IMessage> {
        @Override public IMessage onMessage(PlacePacket packet, MessageContext ctx) {
            final EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> PlacementManager.INSTANCE.request(player, packet));
            return null;
        }
    }
}

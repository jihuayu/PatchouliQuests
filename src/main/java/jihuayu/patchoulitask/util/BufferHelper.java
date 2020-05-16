package jihuayu.patchoulitask.util;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

import static jihuayu.patchoulitask.util.PaletteHelper.BOOK_LIST;
import static jihuayu.patchoulitask.util.PaletteHelper.PALETTE_LIST;

public class BufferHelper {
    public static void writeTaskId(PacketBuffer buffer, ResourceLocation book, ResourceLocation entry, int page) {
        if (!BOOK_LIST.contains(book) || PALETTE_LIST.get(book) == null || !PALETTE_LIST.get(book).contains(entry)) {
            buffer.writeBoolean(false);
            buffer.writeResourceLocation(book);
            buffer.writeResourceLocation(entry);
        } else {
            buffer.writeBoolean(true);
            buffer.writeVarInt(BOOK_LIST.indexOf(book));
            buffer.writeVarInt(PALETTE_LIST.get(book).indexOf(entry));
        }
        buffer.writeVarInt(page);

    }

    public static TaskRead readTaskId(PacketBuffer buffer) {
        TaskRead tr = new TaskRead();
        if (buffer.readBoolean()) {
            tr.book = BOOK_LIST.get(buffer.readVarInt());
            tr.entry = PALETTE_LIST.get(tr.book).get(buffer.readVarInt());
        } else {
            tr.book = buffer.readResourceLocation();
            tr.entry = buffer.readResourceLocation();
        }
        tr.id = buffer.readVarInt();
        return tr;
    }

    public static <T> List<T> readList(PacketBuffer buffer, ReadFunction fn, T p) {
        int num = buffer.readVarInt();
        List<T> list = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            fn.apply(buffer, list);
        }
        return list;
    }

    public static void writeList(PacketBuffer buffer, WriteFunction fn, List<?> list) {
        buffer.writeVarInt(list.size());
        for (Object i : list) {
            fn.apply(buffer, i);
        }
    }

    @FunctionalInterface
    public static interface ReadFunction<T> {
        void apply(PacketBuffer buffer, List<T> list);
    }

    @FunctionalInterface
    public static interface WriteFunction {
        void apply(PacketBuffer buffer, Object list);
    }

    public static class TaskRead {
        public ResourceLocation book;
        public ResourceLocation entry;
        public int id;
    }
}

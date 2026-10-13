package dev.buildcraftstudio.core;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

/** Virtual-only sparse project; never calls World#setBlockState. Positions are local to origin. */
public final class BuildDocument {
    public static final int MAX_PREVIEW_BLOCKS = 512;
    private final Map<BlockPos, IBlockState> blocks = new LinkedHashMap<>();
    private final Deque<Edit> undo = new ArrayDeque<>();
    private final Deque<Edit> redo = new ArrayDeque<>();
    private BlockPos origin;
    private boolean dirty;
    private long revision;
    public long revision() { return revision; }

    public BuildDocument(BlockPos origin) { this.origin = origin; }
    public BlockPos origin() { return origin; }
    public Map<BlockPos, IBlockState> blocks() { return Collections.unmodifiableMap(blocks); }
    public boolean isDirty() { return dirty; }
    public void saved() { dirty = false; }

    public boolean edit(BlockPos local, IBlockState state) {
        if (!validLocal(local)) return false;
        IBlockState before = blocks.get(local);
        if (before == state || (before != null && before.equals(state))) return false;
        if (state != null && before == null && blocks.size() >= MAX_PREVIEW_BLOCKS) return false;
        apply(local, state);
        undo.push(new Edit(local, before, state));
        redo.clear();
        if (undo.size() > 1024) undo.removeLast();
        dirty = true;
        revision++;
        return true;
    }

    public boolean undo() {
        if (undo.isEmpty()) return false;
        Edit edit = undo.pop();
        apply(edit.local, edit.before);
        redo.push(edit);
        dirty = true;
        revision++;
        return true;
    }

    public boolean redo() {
        if (redo.isEmpty()) return false;
        Edit edit = redo.pop();
        apply(edit.local, edit.after);
        undo.push(edit);
        dirty = true;
        revision++;
        return true;
    }

    private void apply(BlockPos local, IBlockState state) {
        if (state == null) blocks.remove(local);
        else blocks.put(local, state);
    }

    public static boolean validLocal(BlockPos p) {
        return p.getX() >= -32 && p.getX() < 32 && p.getY() >= -32 && p.getY() < 32 && p.getZ() >= -32 && p.getZ() < 32;
    }

    /** Loading from disk resets history; no partially reconstructed undo stack. */
    public void loadBlock(BlockPos local, IBlockState state) {
        if (validLocal(local) && blocks.size() < MAX_PREVIEW_BLOCKS && state != null) { blocks.put(local, state); revision++; }
    }

    private static final class Edit {
        final BlockPos local;
        final IBlockState before, after;
        Edit(BlockPos local, IBlockState before, IBlockState after) {
            this.local = local; this.before = before; this.after = after;
        }
    }
}

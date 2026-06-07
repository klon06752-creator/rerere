package com.example.invtabsfix.util;

import com.example.invtabsfix.compat.SableCompat;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;

/**
 * Drop-in replacement for {@code level.getBlockEntity(pos)} that additionally
 * checks Sable contraption worlds when the normal lookup returns {@code null}.
 */
public final class ContraptionBlockEntityHelper {

    private ContraptionBlockEntityHelper() {}

    /**
     * Returns the {@link BlockEntity} at {@code pos} in {@code level}, falling
     * back to a Sable contraption lookup if the standard call returns null.
     */
    @Nullable
    public static BlockEntity getBlockEntity(Level level, BlockPos pos) {
        // Normal world lookup first – fast path.
        BlockEntity be = level.getBlockEntity(pos);
        if (be != null) return be;

        // Sable fallback: the block might be inside a moving contraption.
        return SableCompat.getBlockEntityInContraption(level, pos);
    }
}

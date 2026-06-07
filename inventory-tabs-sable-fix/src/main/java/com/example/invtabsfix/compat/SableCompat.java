package com.example.invtabsfix.compat;

import com.example.invtabsfix.InvTabsFixMod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.fml.ModList;

import javax.annotation.Nullable;
import java.lang.reflect.Method;

/**
 * Compatibility bridge for Sable physics (used by Create Aeronautics).
 *
 * Sable wraps moving contraptions in a special {@code ContraptionWorld} whose
 * block-entity lookup operates on *contraption-local* coordinates, not world
 * coordinates.  Inventory Tabs calls {@code level.getBlockEntity(pos)} with the
 * player's look-target position in world space, which returns {@code null} when
 * the container is riding a Sable contraption – hence no tab appears.
 *
 * This class resolves the ContraptionWorld for a given world-space position via
 * reflection so we don't need a hard compile-time dependency on Sable/Aeronautics.
 */
public final class SableCompat {

    public static boolean SABLE_PRESENT = false;

    // Reflected handles – resolved lazily at setup time.
    private static Class<?> contraptionWorldClass;
    private static Method   getContraptionAtMethod;   // Level -> BlockPos -> @Nullable ContraptionWorld
    private static Method   getBlockEntityLocal;      // ContraptionWorld -> BlockPos -> @Nullable BlockEntity

    private SableCompat() {}

    public static void init() {
        try {
            // Check whether the Sable level extension class exists.
            // Package/class names here match the Sable 0.5.x API; adjust if upstream renames them.
            contraptionWorldClass = Class.forName("com.simibubi.create.aeronautics.contraptions.ContraptionWorld");

            // Static helper: ContraptionWorld.getContraptionWorldAt(Level, BlockPos)
            getContraptionAtMethod = contraptionWorldClass.getMethod("getContraptionWorldAt",
                    Level.class, BlockPos.class);

            // Instance method on ContraptionWorld to fetch a block entity by contraption-local pos.
            getBlockEntityLocal = contraptionWorldClass.getMethod("getBlockEntityLocal", BlockPos.class);

            SABLE_PRESENT = true;
            InvTabsFixMod.LOGGER.info("[InvTabsFix] Sable/Create Aeronautics detected – contraption BE lookup active.");
        } catch (ClassNotFoundException ignored) {
            // Aeronautics not installed – nothing to do.
            InvTabsFixMod.LOGGER.info("[InvTabsFix] Sable/Create Aeronautics not found – skipping compat.");
        } catch (NoSuchMethodException e) {
            // API mismatch – warn but don't crash.
            InvTabsFixMod.LOGGER.warn("[InvTabsFix] Sable detected but API changed – fix may not work: {}", e.getMessage());
        }
    }

    /**
     * Attempts to find a {@link BlockEntity} at {@code worldPos} inside any Sable
     * contraption that occupies that position.  Returns {@code null} if Sable is
     * absent, if no contraption is there, or if the contraption has no BE at that
     * position.
     */
    @Nullable
    public static BlockEntity getBlockEntityInContraption(Level level, BlockPos worldPos) {
        if (!SABLE_PRESENT) return null;
        try {
            // getContraptionWorldAt returns null when no contraption occupies worldPos.
            Object contraptionWorld = getContraptionAtMethod.invoke(null, level, worldPos);
            if (contraptionWorld == null) return null;

            return (BlockEntity) getBlockEntityLocal.invoke(contraptionWorld, worldPos);
        } catch (Exception e) {
            InvTabsFixMod.LOGGER.debug("[InvTabsFix] ContraptionWorld lookup threw an exception: {}", e.getMessage());
            return null;
        }
    }
}

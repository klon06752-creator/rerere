package com.example.invtabsfix.mixin;

import com.example.invtabsfix.util.ContraptionBlockEntityHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Patches {@code TabManager} (the Inventory Tabs class that determines which
 * container the player is looking at) so that {@code level.getBlockEntity()}
 * calls are replaced by our contraption-aware version.
 *
 * <p><b>Target class</b>: {@code me.b100d.inventorytabs.tab.TabManager}<br>
 * Adjust the {@code value} in {@code @Mixin} if the upstream mod changes its
 * package structure.
 *
 * <p>The {@code @Redirect} replaces <em>every</em> call to
 * {@link Level#getBlockEntity(BlockPos)} that occurs inside
 * {@code TabManager#getTabForBlockEntity} (the method that resolves the
 * hovered block to a tab).  If Sable is absent the helper just forwards the
 * call transparently, so there is zero overhead in vanilla/non-Aeronautics
 * worlds.
 */
@Mixin(targets = "me.b100d.inventorytabs.tab.TabManager", remap = false)
public abstract class MixinTabManager {

    /**
     * Redirects Level#getBlockEntity calls inside the tab-resolution method so
     * that moving contraption blocks are also considered.
     *
     * The method name {@code getTabForBlockEntity} is the one used by
     * inventorytabs-1.21.1-x.x.x.  If the mod renames it, update the
     * {@code method} value below accordingly.
     */
    @Redirect(
        method = "getTabForBlockEntity",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"
        ),
        remap = true   // vanilla method – remap normally
    )
    private BlockEntity invtabsfix$redirectGetBlockEntity(Level level, BlockPos pos) {
        return ContraptionBlockEntityHelper.getBlockEntity(level, pos);
    }
}

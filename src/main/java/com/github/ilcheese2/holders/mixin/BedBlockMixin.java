package com.github.ilcheese2.holders.mixin;

import net.minecraft.block.BedBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.CollisionView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Optional;

@Mixin(BedBlock.class)
public interface BedBlockMixin {
    @Invoker("findWakeUpPosition")
    static Optional<Vec3d> invokeFindWakeUpPosition(EntityType<?> type, CollisionView world, BlockPos pos, Direction bedDirection, Direction respawnDirection) {
        throw new AssertionError();
    }
}

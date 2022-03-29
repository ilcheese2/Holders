package com.github.ilcheese2.holders;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public record DimensionPos(String dimension, BlockPos pos) {
    public DimensionPos(World dimension, BlockPos pos) {
        this(dimension.getRegistryKey().getValue().toString(), pos);
    }
}

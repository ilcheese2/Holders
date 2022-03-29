package com.github.ilcheese2.holders;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class HolderBlockEntity extends BlockEntity  {

    public HolderBlockEntity (BlockPos pos, BlockState state) {
        super(Holders.HOLDER_BLOCK_ENTITY, pos, state);
    }



}

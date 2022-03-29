package com.github.ilcheese2.holders;

import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.BlockView;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.explosion.Explosion;

public class HolderWallBlock extends Block {

    public HolderWallBlock(Settings settings) {
        super(settings);
    }

    @SuppressWarnings("deprecation")
    @Override
    public float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
        return 0;
    }

    @Override
    public boolean shouldDropItemsOnExplosion(Explosion explosion) {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient && player.isSneaking()) {
            ServerWorld dimension  = world.getServer().getWorld(RegistryKey.of(Registry.WORLD_KEY, new Identifier(HoldersManager.getDimensionLocation(getMainBlock(pos)).dimension())));
            FabricDimensions.teleport(player, dimension, new TeleportTarget(BedBlock.findWakeUpPosition(EntityType.PLAYER, dimension, HoldersManager.getDimensionLocation(getMainBlock(pos)).pos(), ((ServerPlayerEntity) player).getSpawnAngle()).get(), player.getVelocity(), player.getYaw(),player.getPitch()));
        }
        return ActionResult.PASS;
    }

    private static BlockPos getMainBlock(BlockPos pos) {
        return new BlockPos(Math.floor((double) pos.getX()/16)*16, Math.floor((double) pos.getY()/16)*16, Math.floor((double) pos.getZ()/16)*16);
    }

    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        if (!world.isClient()) {
            world.setBlockState(pos, state, Block.NO_REDRAW);
        }
    }
}

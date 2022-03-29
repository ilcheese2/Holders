package com.github.ilcheese2.holders;

import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.BlockView;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;

import static com.github.ilcheese2.holders.Holders.HOLDER_KEY;

public class HolderBlock extends HorizontalFacingBlock implements BlockEntityProvider {
    public HolderBlock(Settings settings) {
        super(settings);
    }

    private static final ChunkTicketType<DimensionPos> HOLDER_LOADER = ChunkTicketType.create("holder_loader", Comparator.comparing(pos -> new Vec3i(pos.pos().getX(), pos.pos().getY(), pos.pos().getZ())));

    private void clear(World world, BlockPos pos) {
        BlockPos pos2 = HoldersManager.getLocation(new DimensionPos(world, pos));
        if (pos2 != null) {
            ServerChunkManager chunkManager = ((ServerWorld) world).getServer().getWorld(HOLDER_KEY).getChunkManager();
            HoldersManager.removeLocation(new DimensionPos(world, pos), world);
            chunkManager.removeTicket(HOLDER_LOADER, new ChunkPos(pos2), 1, new DimensionPos(world, pos));
        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new HolderBlockEntity(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(Properties.HORIZONTAL_FACING);
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            BlockPos blockPos = HoldersManager.addLocation(new DimensionPos(world, pos), world);
            FabricDimensions.teleport(player, ((ServerWorld) world).getServer().getWorld(HOLDER_KEY), new TeleportTarget(new Vec3d((double)blockPos.getX() + 8, blockPos.getY() + 2, (double)blockPos.getZ() + 8), player.getVelocity(), player.getYaw(), player.getPitch()));
        }
        return ActionResult.PASS;
    }

    @SuppressWarnings("deprecation")
    @Override
    public float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
        if (player.isSneaking()) {
            return super.calcBlockBreakingDelta(state, player, world, pos);
        }
        else {
            player.sendMessage(new LiteralText("Breaking this block will break all blocks inside. Sneak while breaking to break this block."), true);
            return 0.0F;
        }
    }

    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        if (!world.isClient()) {
            clear((World) world, pos);
        }
        super.onBroken(world, pos, state);
    }

    @Override
    public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
        if (!world.isClient()) {
            clear(world, pos);
        }
        super.onDestroyedByExplosion(world, pos, explosion);
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    private void update(World world, BlockPos pos) {
        BlockPos pos2 = HoldersManager.getLocation(new DimensionPos(world, pos));
        if (pos2 != null) {
            ServerChunkManager chunkManager = ((ServerWorld) world).getServer().getWorld(HOLDER_KEY).getChunkManager();
            if (world.isReceivingRedstonePower(pos)) {
                chunkManager.addTicket(HOLDER_LOADER, new ChunkPos(pos2), 3, new DimensionPos(world, pos));
            }
            else  {
                chunkManager.removeTicket(HOLDER_LOADER, new ChunkPos(pos2), 3, new DimensionPos(world, pos));
            }

        }
    }
    @SuppressWarnings("deprecation")
    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        if (!world.isClient()) {
            update(world, pos);
        }
        super.neighborUpdate(state, world, pos, block, fromPos, notify);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!world.isClient()) {
            update(world, pos);
        }
        super.onBlockAdded(state, world, pos, oldState, notify);
    }
}

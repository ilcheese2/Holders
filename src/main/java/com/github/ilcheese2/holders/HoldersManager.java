package com.github.ilcheese2.holders;

import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import com.github.ilcheese2.holders.mixin.BedBlockMixin;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.github.ilcheese2.holders.Holders.HOLDER_KEY;
import static com.github.ilcheese2.holders.Holders.HOLDER_WALL_BLOCK;


public class HoldersManager {

    private static HoldersState holdersState;

    private static void createRoom(BlockPos origin, ServerWorld serverWorld) {
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    if ((y == 0) || (y == 15) || (x == 0) || (x == 15) || (z == 15) || (z == 0)) {
                        serverWorld.setBlockState(origin.add(x,y,z), HOLDER_WALL_BLOCK.getDefaultState());
                    }
                }
            }
        }
    }
    private static void destroyRoom(BlockPos origin, DimensionPos dimensionPos, ServerWorld serverWorld) {
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    if ((y == 0) || (y == 15) || (x == 0) || (x == 15) || (z == 15) || (z == 0)) {
                        serverWorld.setBlockState(origin.add(x,y,z), Blocks.AIR.getDefaultState());
                    }
                }
            }
        }
        clearInterior(origin, dimensionPos, serverWorld);
    }
    public static BlockPos addLocation(DimensionPos dimensionPos, World world) {
        ServerWorld serverWorld = ((ServerWorld) world).getServer().getWorld(HOLDER_KEY);
        if (!holdersState.locations.containsKey(dimensionPos)) {
            BlockPos available = BlockPos.ORIGIN;
            while (holdersState.locations.containsValue(available)) {
                if (available.getY() == 256) {
                    available = available.add(16, 0, 0);
                } else {
                    available = available.add(0, 16, 0);
                }
            }
            holdersState.locations.put(dimensionPos, available);
            holdersState.markDirty();
            createRoom(available, serverWorld);
            return available;
        }
        return holdersState.locations.get(dimensionPos);
    }

    public static BlockPos getLocation(DimensionPos dimensionPos) {
        return holdersState.locations.get(dimensionPos);
    }

    public static DimensionPos getDimensionLocation(BlockPos pos) {
        return holdersState.locations.inverse().get(pos);
    }

    public static void removeLocation(DimensionPos dimensionPos, World world) {
        ServerWorld serverWorld = ((ServerWorld) world).getServer().getWorld(HOLDER_KEY);
        BlockPos origin = holdersState.locations.remove(dimensionPos);
        destroyRoom(origin, dimensionPos, serverWorld);
        holdersState.markDirty();
    }

    private static void clearInterior(BlockPos origin, DimensionPos pos, ServerWorld serverWorld) {
        BlockPos inner = origin.add(1,1,1);
        for (int y = 0; y < 15; y++) {
            for (int x = 0; x < 15; x++) {
                for (int z = 0; z < 15; z++) {
                    serverWorld.breakBlock(inner.add(x,y,z), true);
                }
            }
        }
        List<Entity> entities = serverWorld.getOtherEntities(null, new Box(origin).expand(16));
        ServerWorld dimension  = serverWorld.getServer().getWorld(RegistryKey.of(Registry.WORLD_KEY, new Identifier(pos.dimension())));
        HashMap<EntityType, Optional<Vec3d>> spawnPoints = new HashMap<>();
        for (Entity entity: entities) {
            if (!spawnPoints.containsKey(entity.getType())) {
                spawnPoints.put(entity.getType(), BedBlockMixin.invokeFindWakeUpPosition(entity.getType(), dimension, pos.pos(), Direction.DOWN, Direction.UP));
            }
            FabricDimensions.teleport(entity, dimension, new TeleportTarget(spawnPoints.get(entity.getType()).get(), entity.getVelocity(), entity.getYaw(), entity.getPitch()));
        }
    }

    public static void initHolders(ServerWorld world) {
        holdersState = world.getPersistentStateManager().getOrCreate(HoldersState::new, HoldersState::new, "holders");
        for (DimensionPos pos: Set.copyOf(holdersState.locations.keySet())) {
            ServerWorld dimension  = world.getServer().getWorld(RegistryKey.of(Registry.WORLD_KEY, new Identifier(pos.dimension())));
            if (!(dimension.getBlockEntity(pos.pos()) instanceof HolderBlockEntity)) {
                removeLocation(pos, world);
            }
        }
    }

}
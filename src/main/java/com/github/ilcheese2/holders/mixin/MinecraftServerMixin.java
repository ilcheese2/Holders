package com.github.ilcheese2.holders.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.github.ilcheese2.holders.HoldersManager;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    @Shadow public @Nullable abstract ServerWorld getWorld(RegistryKey<World> key);

    @Inject(method = "createWorlds", at = @At(value = "TAIL"))
    public void initHolder(WorldGenerationProgressListener worldGenerationProgressListener, CallbackInfo ci) {
        HoldersManager.initHolders(getWorld(World.OVERWORLD));
    }

}

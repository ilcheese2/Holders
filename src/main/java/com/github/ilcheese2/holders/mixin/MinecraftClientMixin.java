package com.github.ilcheese2.holders.mixin;

import com.mojang.serialization.Lifecycle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.gen.GeneratorOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Redirect(method = "startIntegratedServer(Ljava/lang/String;Ljava/util/function/Function;Ljava/util/function/Function;ZLnet/minecraft/client/MinecraftClient$WorldLoadAction;)V",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/GeneratorOptions;isLegacyCustomizedType()Z"))
    public boolean removeWorldgenScreen(GeneratorOptions instance) {
        return false;
    }
    @Redirect(method = "startIntegratedServer(Ljava/lang/String;Ljava/util/function/Function;Ljava/util/function/Function;ZLnet/minecraft/client/MinecraftClient$WorldLoadAction;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/SaveProperties;getLifecycle()Lcom/mojang/serialization/Lifecycle;"))
    public Lifecycle removeWorldgenScreen2(SaveProperties instance) {
        return Lifecycle.stable();
    }
}
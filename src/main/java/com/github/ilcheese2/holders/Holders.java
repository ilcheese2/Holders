package com.github.ilcheese2.holders;

import com.github.ilcheese2.holders.HolderBlock;
import com.github.ilcheese2.holders.HolderBlockEntity;
import com.github.ilcheese2.holders.HolderWallBlock;
import com.github.ilcheese2.holders.dimension.HolderChunkGenerator;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Holders implements ModInitializer {

    public static final Logger logger  = LogManager.getLogger("holders");

    private static final RegistryKey<DimensionOptions> DIMENSION_KEY = RegistryKey.of(Registry.DIMENSION_KEY, new Identifier("holders", "holder"));

    public static RegistryKey<World> HOLDER_KEY = RegistryKey.of(Registry.WORLD_KEY, DIMENSION_KEY.getValue());

    private static final RegistryKey<DimensionType> DIMENSION_TYPE_KEY = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, new Identifier("holders", "holder"));

    private static final Block HOLDER_BLOCK = new HolderBlock(FabricBlockSettings.of(Material.STONE).strength(3.5f).requiresTool());

    public static final Block HOLDER_WALL_BLOCK = new HolderWallBlock(FabricBlockSettings.of(Material.STONE).strength(3.5f, 3600000.0f).dropsNothing().allowsSpawning((a, b, c, d)-> false));

    public static final BlockEntityType<HolderBlockEntity> HOLDER_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier("holders", "holder"), FabricBlockEntityTypeBuilder.create(HolderBlockEntity::new, HOLDER_BLOCK).build());

    @Override
    public void onInitialize() {
        Registry.register(Registry.BLOCK, new Identifier("holders", "holder"), HOLDER_BLOCK);
        Registry.register(Registry.ITEM, new Identifier("holders", "holder"), new BlockItem(HOLDER_BLOCK, new FabricItemSettings().group(ItemGroup.MISC)));
        Registry.register(Registry.BLOCK, new Identifier("holders", "holder_wall"), HOLDER_WALL_BLOCK);
        Registry.register(Registry.CHUNK_GENERATOR, new Identifier("holders", "holder_generator"), HolderChunkGenerator.CODEC);
        HOLDER_KEY = RegistryKey.of(Registry.WORLD_KEY, new Identifier("holders", "holder"));
    }
}

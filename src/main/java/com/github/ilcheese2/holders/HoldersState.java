package com.github.ilcheese2.holders;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;

public class HoldersState extends PersistentState {

    public BiMap<DimensionPos, BlockPos> locations = HashBiMap.create();

    public HoldersState(NbtCompound nbt) {
        NbtList list = nbt.getList("Locations", 10);
        for (int i = 0; i < list.size(); ++i) {
            NbtCompound nbtCompound = list.getCompound(i);
            locations.put(new DimensionPos(nbtCompound.getString("MachineDimension"), BlockPos.fromLong(nbtCompound.getLong("MachinePos"))),BlockPos.fromLong(nbtCompound.getLong("DimensionPos")));
        }
    }

    public HoldersState() {}

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList list = new NbtList();
        for (DimensionPos dimensionPos :locations.keySet()) {
            NbtCompound nbtCompound = new NbtCompound();
            nbtCompound.putString("MachineDimension", dimensionPos.dimension());
            nbtCompound.putLong("MachinePos", dimensionPos.pos().asLong());
            nbtCompound.putLong("DimensionPos", locations.get(dimensionPos).asLong());
            list.add(nbtCompound);
        }
        nbt.put("Locations", list);
        return nbt;
    }
}

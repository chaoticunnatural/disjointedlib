package dev.raven.disjointedlib.impl;

import dev.raven.disjointedlib.DisjointedLib;
import dev.raven.disjointedlib.impl.items.DistanceJointItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class DisjointedItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, DisjointedLib.MODID);

    public static final RegistryObject<Item> DISTANCE_ITEM = ITEMS.register("distance_joint", DistanceJointItem::new);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}

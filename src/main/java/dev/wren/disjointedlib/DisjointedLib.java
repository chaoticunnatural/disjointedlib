package dev.wren.disjointedlib;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(DisjointedLib.MODID)
public class DisjointedLib {
    public static final String MODID = "disjointedlib";
    private static final Logger LOGGER = LogUtils.getLogger();

    public DisjointedLib(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        MinecraftForge.EVENT_BUS.register(this);
    }


}

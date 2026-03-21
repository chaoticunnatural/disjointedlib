package dev.raven.disjointedlib;

import com.mojang.logging.LogUtils;
import dev.raven.disjointedlib.impl.DisjointedItems;
import dev.raven.disjointedlib.infrastructure.JointManager;
import dev.raven.disjointedlib.impl.joints.DisjointedDistanceJoint;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.valkyrienskies.core.internal.joints.VSJointType;
import org.valkyrienskies.mod.common.ValkyrienSkiesMod;

@Mod(DisjointedLib.MODID)
public class DisjointedLib {
    public static final String MODID = "disjointedlib";
    public static final String NAME = "Disjointed Lib";
    public static final Logger LOGGER = LogUtils.getLogger();

    public DisjointedLib(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        JointManager.registerConverter(VSJointType.DISTANCE, DisjointedDistanceJoint::tagToJoint);

        DisjointedItems.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::common);

        ValkyrienSkiesMod.getApi().getShipLoadEvent().on(JointManager::onShipLoad);

        LOGGER.info("{} ({}) initialized!", NAME, MODID);
    }

    private void common(final FMLCommonSetupEvent event) {
        LOGGER.info("removing your joints..."); // muahahaha
    }
}

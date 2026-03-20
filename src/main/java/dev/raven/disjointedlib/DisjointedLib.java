package dev.raven.disjointedlib;

import com.mojang.logging.LogUtils;
import dev.raven.disjointedlib.infrastructure.JointPersistence;
import dev.raven.disjointedlib.joints.DisjointedDistanceJoint;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
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

        JointPersistence.registerConverter(VSJointType.DISTANCE, DisjointedDistanceJoint::tagToJoint);

        MinecraftForge.EVENT_BUS.register(this);

        ValkyrienSkiesMod.getApi().getShipLoadEvent().on(JointPersistence::onShipLoad);

        LOGGER.info("{} ({}) initialized!", NAME, MODID);
    }
}

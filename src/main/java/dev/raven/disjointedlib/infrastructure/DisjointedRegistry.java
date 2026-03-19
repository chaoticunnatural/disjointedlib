package dev.raven.disjointedlib.infrastructure;

import dev.raven.disjointedlib.DisjointedLib;
import dev.raven.disjointedlib.internal.DisjointedJoint;
import dev.raven.disjointedlib.internal.utility.JointUtility;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import org.apache.commons.lang3.function.TriFunction;
import org.valkyrienskies.core.internal.joints.VSJoint;
import org.valkyrienskies.core.internal.joints.VSJointType;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class DisjointedRegistry {

    private static final StackWalker STACK_WALKER = StackWalker.getInstance();

    private static final EnumMap<VSJointType, TriFunction<ServerLevel, Integer, CompoundTag, DisjointedJoint<?>>> tagToJoint = new EnumMap<>(VSJointType.class);
    private static final EnumMap<VSJointType, Function<DisjointedJoint<?>, CompoundTag>> jointToTag = new EnumMap<>(VSJointType.class);

    public static void forJointType(VSJointType jointType, TriFunction<ServerLevel, Integer, CompoundTag, DisjointedJoint<?>> toJoint, Function<DisjointedJoint<?>, CompoundTag> toTag) {
        registerTagToJoint(jointType, toJoint);
        registerJointToTag(jointType, toTag);
    }

    public static void registerTagToJoint(VSJointType jointType, TriFunction<ServerLevel, Integer, CompoundTag, DisjointedJoint<?>> function) {
        DisjointedLib.LOGGER.info("Registering tag to joint converter for joint type {}", jointType.name());
        if (!STACK_WALKER.getCallerClass().getPackageName().startsWith("dev.raven.disjointedlib"))
            DisjointedLib.LOGGER.warn("Tag to joint converter is not internal, found {}", STACK_WALKER.getCallerClass());

        tagToJoint.put(jointType, function);
    }

    public static void registerJointToTag(VSJointType jointType, Function<DisjointedJoint<?>, CompoundTag> function) {
        DisjointedLib.LOGGER.info("Registering joint to tag converter for joint type {}", jointType.name());
        if (!STACK_WALKER.getCallerClass().getPackageName().startsWith("dev.raven.disjointedlib"))
            DisjointedLib.LOGGER.warn("Joint to tag converter is not internal, found {}", STACK_WALKER.getCallerClass());

        jointToTag.put(jointType, function);
    }


    public static CompoundTag convert(DisjointedJoint<?> joint) {
        return jointToTag.get(joint.getJointType()).apply(joint);
    }

    @SuppressWarnings("unchecked")
    public static <J extends VSJoint> DisjointedJoint<J> convert(VSJointType jointType, ServerLevel serverLevel, Integer id, CompoundTag tag) {
        return (DisjointedJoint<J>) tagToJoint.get(jointType).apply(serverLevel, id, tag);
    }

}

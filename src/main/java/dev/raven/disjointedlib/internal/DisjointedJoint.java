package dev.raven.disjointedlib.internal;

import dev.raven.disjointedlib.internal.utility.JointValues;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import org.valkyrienskies.core.internal.joints.VSJoint;
import org.valkyrienskies.core.internal.joints.VSJointType;

public interface DisjointedJoint<J extends VSJoint> {

    VSJointType getJointType();

    Integer getId();

    Integer getJointId();

    J getJoint();

    boolean canRestore();

    Class<J> getJointClass();

    JointValues<J> getJointValues();

    CompoundTag toTag();

    boolean checkIfRestore(Long shipId);

    void createJoint(ServerLevel serverLevel);

}

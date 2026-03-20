package dev.raven.disjointedlib.joints;

import dev.raven.disjointedlib.internal.DisjointedJoint;
import dev.raven.disjointedlib.internal.utility.*;
import kotlin.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import org.valkyrienskies.core.internal.joints.VSDistanceJoint;
import org.valkyrienskies.core.internal.joints.VSJointMaxForceTorque;
import org.valkyrienskies.core.internal.joints.VSJointType;

import java.util.List;

public class DisjointedDistanceJoint implements DisjointedJoint<VSDistanceJoint> {

    private final Integer id;
    private JointPosData posData0;
    private JointPosData posData1;
    private final JointUtility.ConnectionType connectionType;
    private final DistanceJointValues jointValues;
    private VSDistanceJoint joint;
    private Integer jointId;
    int restorationAttempts = 0;

    private DisjointedDistanceJoint(Integer id, JointPosData posData0, JointPosData posData1, DistanceJointValues jointValues) {
        this.id = id;
        this.posData0 = posData0;
        this.posData1 = posData1;
        this.jointValues = jointValues;
        this.connectionType = JointUtility.getConnectionType(posData0, posData1);
    }

    @Override
    public VSJointType getJointType() {
        return VSJointType.DISTANCE;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public Integer getJointId() {
        return jointId;
    }

    @Override
    public VSDistanceJoint getJoint() {
        return joint;
    }

    @Override
    public boolean canRestore() {
        return false;
    }

    @Override
    public Class<VSDistanceJoint> getJointClass() {
        return VSDistanceJoint.class;
    }

    @Override
    public JointValues<VSDistanceJoint> getJointValues() {
        return jointValues;
    }


    @Override
    public boolean checkIfRestore(Long shipId) {
        restorationAttempts++;
        return (this.posData0.isShip(shipId) || this.posData1.isShip(shipId)) && this.connectionType.doesRestore(restorationAttempts);
    }

    @Override
    public void createJoint(ServerLevel serverLevel) {

    }


    public static DisjointedDistanceJoint create(ServerLevel serverLevel, Integer id, Long ship0, Long ship1, BlockPos blockPos0, BlockPos blockPos1) {
        Pair<JointPosData, JointPosData> posDataPair = JointPosData.create(serverLevel, ship0, ship1, blockPos0, blockPos1);
        JointPosData posData0 = posDataPair.component1();
        JointPosData posData1 = posDataPair.component2();

        float length = (float) posData0.localPos().distance(posData1.localPos());
        float mass0 = ShipUtility.getMassForShip(serverLevel, ship0);
        float mass1 = ShipUtility.getMassForShip(serverLevel, ship1);
        //double compliance = 1e-12f / Math.max(Math.min(mass0, mass1), 100.0f) * (posData0.isWorld() || posData1.isWorld() ? 0.05f : 1f); idk maybe not needed
        float maxForce = 5e13f * Math.min(Math.max(mass0, mass1) / Math.min(mass0, mass1), 20.0f) * (posData0.isWorld() || posData1.isWorld() ? 10f : 1f);

        return new DisjointedDistanceJoint(id, posData0, posData1, new DistanceJointValues(length, new VSJointMaxForceTorque(maxForce, maxForce)));
    }

    public static DisjointedDistanceJoint tagToJoint(ServerLevel serverLevel, Integer id, CompoundTag tag) {
        Pair<JointPosData, JointPosData> posDataPair = JointPosData.create(serverLevel, tag.getLong("ship0"), tag.getLong("ship1"), NbtUtils.readBlockPos(tag.getCompound("blockPos0")), NbtUtils.readBlockPos(tag.getCompound("blockPos1")));
        JointPosData posData0 = posDataPair.component1();
        JointPosData posData1 = posDataPair.component2();

        DistanceJointValues jointValues = new DistanceJointValues(tag);

        return new DisjointedDistanceJoint(id, posData0, posData1, jointValues);
    }


    @Override
    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();

        tag.putString("jointType", VSJointType.DISTANCE.name());
        tag.putLong("ship0", posData0.getShipIdSafe());
        tag.putLong("ship1", posData1.getShipIdSafe());
        tag.put("blockPos0", NbtUtils.writeBlockPos(posData0.blockPos()));
        tag.put("blockPos1", NbtUtils.writeBlockPos(posData1.blockPos()));

        return tag;
    }

    public static class DistanceJointValues extends JointValues<VSDistanceJoint> {

        public DistanceJointValues(float maxLength, VSJointMaxForceTorque maxForceTorque) {
            this.put(JointUtility.JointValue.MAXLENGTH, maxLength);
            this.put(JointUtility.JointValue.MAXFORCETORQUE, maxForceTorque);
            this.fillRemaining();
        }

        public DistanceJointValues(CompoundTag tag) {
            this.fillFromTag(tag);
        }

        @Override
        public Class<VSDistanceJoint> getJointClass() {
            return VSDistanceJoint.class;
        }

        @Override
        public List<JointUtility.JointValue> validValues() {
            return List.of(
                    JointUtility.JointValue.MAXLENGTH,
                    JointUtility.JointValue.MINLENGTH,
                    JointUtility.JointValue.MAXFORCETORQUE,
                    JointUtility.JointValue.COMPLIANCE,
                    JointUtility.JointValue.STIFFNESS,
                    JointUtility.JointValue.TOLERANCE,
                    JointUtility.JointValue.DAMPING
            );
        }
    }

}

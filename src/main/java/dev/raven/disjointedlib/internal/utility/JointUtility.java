package dev.raven.disjointedlib.internal.utility;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.physics_api.joints.MaxForceTorque;

public class JointUtility {

    public static Vector3d getLocalPos(Level level, BlockPos pos) {
        Vector3d blockPos;
        try {
            VoxelShape shape = level.getBlockState(pos).getShape(level, pos);
            Vec3 vec = shape.bounds().getCenter().add(pos.getCenter());
            blockPos = new Vector3d(vec.x - 0.5, vec.y - 0.5, vec.z - 0.5);
        } catch (UnsupportedOperationException ex) {
            blockPos = new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        }

        return blockPos;
    }

    public static Vector3d getWorldPos(Level level, BlockPos pos, Long shipId) {
        Vector3d localPos = getLocalPos(level, pos);
        if (shipId != null) {
            Ship shipObject = VSGameUtilsKt.getShipObjectWorld(level).getAllShips().getById(shipId);
            if (shipObject != null) {
                Vector3d worldPos = new Vector3d();
                shipObject.getTransform().getShipToWorld().transformPosition(localPos, worldPos);
                return worldPos;
            }
        }
        return localPos;
    }

    public static BlockPos containingBlockPos(Vector3d pos) {
        return BlockPos.containing(pos.x, pos.y, pos.z);
    }

    public static Vector3d convertLocalToWorld(Level level, Vector3d localPos, Long ship) {
        if (ship == null || level == null) return localPos;

        try {
            Ship shipObject = VSGameUtilsKt.getShipObjectWorld(level).getAllShips().getById(ship);
            if (shipObject != null) {
                Vector3d worldPos = new Vector3d();
                shipObject.getTransform().getShipToWorld().transformPosition(localPos, worldPos);
                return worldPos;
            }

            return new Vector3d(localPos);
        } catch (Exception e) {
            return new Vector3d(localPos);
        }
    }

    public static Vector3d renderLocalToWorld(Level level, Vector3d localPos, Long ship) {
        if (ship == null || level == null) return localPos;

        var shipWorld = VSGameUtilsKt.getShipObjectWorld(level);

        ClientShip clientShip = (ClientShip) shipWorld.getAllShips().getById(ship);
        if (clientShip == null) return localPos;
        Vector3d transformedPos = clientShip.getRenderTransform().getShipToWorld().transformPosition(new Vector3d(localPos), new Vector3d());
        return new Vector3d(transformedPos.x, transformedPos.y, transformedPos.z);
    }

    public static ConnectionType getConnectionType(JointPosData posData0, JointPosData posData1) {
        if (posData0.isWorld() && !posData1.isWorld()) {
            return ConnectionType.SW;
        } else if (posData0.isWorld()) {
            return ConnectionType.WW;
        } else {
            return ConnectionType.SS;
        }
    }

    public enum ConnectionType {
        WW(0),
        SW(1),
        SS(2);

        private final int requiredAttempts;

        ConnectionType(int requiredAttempts) {
            this.requiredAttempts = requiredAttempts;
        }

        public boolean doesRestore(int attempts) {
            return attempts >= requiredAttempts;
        }
    }

    public enum JointValue {
        MAXLENGTH(Float.class),
        MINLENGTH(Float.class),
        MAXFORCETORQUE(MaxForceTorque.class),
        COMPLIANCE(Double.class),
        STIFFNESS(Float.class),
        TOLERANCE(Float.class),
        DAMPING(Float.class)
        ;

        public final Class<?> type;

        JointValue(Class<?> type) {
            this.type = type;
        }
    }
}

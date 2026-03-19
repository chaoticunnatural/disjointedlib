package dev.raven.disjointedlib.infrastructure;

import dev.raven.disjointedlib.internal.DisjointedJoint;
import net.minecraft.server.level.ServerLevel;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JointManager {

    private static final Map<Integer, DisjointedJoint<?>> joints = new ConcurrentHashMap<>();
    private static int nextId = 1;


    public static int getNextId() {
        return ++nextId;
    }

    public static void resetId() {
        nextId = 1;
    }

    public static void addJoint(ServerLevel level, DisjointedJoint<?> joint) {
        joints.put(joint.getId(), joint);

        JointPersistence persistence = JointPersistence.get(level);

        persistence.addJoint(joint);
    }

    public static DisjointedJoint<?> getJoint(Integer id) {
        return joints.get(id);
    }

    public static boolean hasJoint(Integer id) {
        return joints.containsKey(id);
    }

    public static void replaceJoint(Integer id, DisjointedJoint<?> rope) {
        joints.put(id, rope);
    }

    public static void removeJoint(ServerLevel level, Integer constraintId) {

        DisjointedJoint<?> data = joints.remove(constraintId);
        if (data != null) {

            JointPersistence persistence = JointPersistence.get(level);
            persistence.removeJoint(constraintId);
        }
    }

    public static Map<Integer, DisjointedJoint<?>> getJoints() {
        return new HashMap<>(joints);
    }

    public static void addJointToManager(DisjointedJoint<?> joint) {
        joints.put(joint.getId(), joint);
    }
}

package dev.raven.disjointedlib.infrastructure;

import dev.raven.disjointedlib.DisjointedLib;
import dev.raven.disjointedlib.internal.DisjointedJoint;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.core.api.event.RegisteredListener;
import org.valkyrienskies.core.api.events.ShipLoadEvent;
import org.valkyrienskies.core.internal.joints.VSJoint;
import org.valkyrienskies.core.internal.joints.VSJointType;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.ValkyrienSkiesMod;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class JointManager extends SavedData {

    private static final String DATA_NAME = "disjointedlib_data";
    private int nextId = 0;

    private void resetId() {
        this.nextId = 0;
    }

    private int getNextId() {
        return nextId++;
    }

    private final Map<Integer, DisjointedJoint<?>> joints = new HashMap<>();


    public static JointManager get(ServerLevel level) {
        DisjointedLib.LOGGER.info("Getting JointManager for ServerLevel {}", level.dimensionType());
        DimensionDataStorage storage = level.getDataStorage();
        return storage.computeIfAbsent(tag -> JointManager.load(level, tag), JointManager::new, DATA_NAME);
    }

    // persistence stuff
    public static JointManager load(ServerLevel serverLevel, CompoundTag tag) {
        JointManager data = new JointManager();

        ListTag jointList = tag.getList("joints", Tag.TAG_COMPOUND);
        for (Tag t : jointList) {
            CompoundTag jointTag = (CompoundTag) t;
            int nextId = data.getNextId();
            VSJointType jointType = VSJointType.valueOf(jointTag.getString("jointType"));
            DisjointedJoint<?> joint = convert(jointType, serverLevel, nextId, jointTag);

            data.joints.put(nextId, joint);
        }

        DisjointedLib.LOGGER.info("Loaded {} joints from data", data.joints.size());
        return data;
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag) {
        ListTag jointList = new ListTag();
        for (Map.Entry<Integer, DisjointedJoint<?>> entry : joints.entrySet()) {
            jointList.add(entry.getValue().toTag());
        }
        tag.put("joints", jointList);
        return tag;
    }

    public void saveNow(ServerLevel level) {
        setDirty();
        level.getDataStorage().save();
    }

    public <J extends DisjointedJoint<? extends VSJoint>> void addJoint(Function<Integer, J> jointFunc) {
        J joint = jointFunc.apply(getNextId());

        joints.put(joint.getId(), joint);

        setDirty();
    }

    public DisjointedJoint<?> getJoint(Integer id) {
        return joints.get(id);
    }

    public boolean hasJoint(Integer id) {
        return joints.containsKey(id);
    }

    public void replaceJoint(Integer id, DisjointedJoint<?> rope) {
        joints.put(id, rope);
        setDirty();
    }

    public void removeJoint(Integer constraintId) {
        DisjointedJoint<?> data = joints.remove(constraintId);
        setDirty();
    }

    public Map<Integer, DisjointedJoint<?>> getJoints() {
        return new HashMap<>(joints);
    }


    public static void onShipLoad(ShipLoadEvent shipLoadEvent, RegisteredListener registeredListener) {
        Long loadedId = shipLoadEvent.getShip().getId();

        MinecraftServer server = ValkyrienSkiesMod.getCurrentServer();
        if (server == null) return;

        ServerLevel level = VSGameUtilsKt.getLevelFromDimensionId(server, shipLoadEvent.getShip().getChunkClaimDimension());
        if (level == null) return;

        JointManager ropePersistence = JointManager.get(level);

        ropePersistence.joints.values().stream()
                .filter(joint -> joint.checkIfRestore(loadedId))
                .forEach(joint -> joint.createJoint(level));
    }

    private static final EnumMap<VSJointType, TriFunction<ServerLevel, Integer, CompoundTag, ? extends DisjointedJoint<? extends VSJoint>>> functionMap = new EnumMap<>(VSJointType.class);

    public static void registerConverter(VSJointType jointType, TriFunction<ServerLevel, Integer, CompoundTag, ? extends DisjointedJoint<? extends VSJoint>> function) {
        functionMap.put(jointType, function);
    }

    @SuppressWarnings("unchecked")
    public static <J extends DisjointedJoint<? extends VSJoint>> J convert(VSJointType jointType, ServerLevel serverLevel, Integer id, CompoundTag tag) {
        return (J) functionMap.get(jointType).apply(serverLevel, id, tag);
    }
}
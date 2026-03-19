package dev.raven.disjointedlib.internal.utility;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import org.joml.Vector3d;

public class TagUtility {

    public static CompoundTag writeVector3d(Vector3d vector3d) {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putDouble("X", vector3d.x);
        compoundTag.putDouble("Y", vector3d.y);
        compoundTag.putDouble("Z", vector3d.z);
        return compoundTag;
    }

    public static Vector3d readVector3d(CompoundTag tag) {
        return new Vector3d(tag.getDouble("X"), tag.getDouble("Y"), tag.getDouble("Z"));
    }


    public static CompoundTag writePosData(JointPosData posData) {
        CompoundTag tag = new CompoundTag();

        tag.putLong("shipId", posData.shipId() == null ? -1 : posData.shipId()); // use -1 to denote ground body
        tag.put("blockPos", NbtUtils.writeBlockPos(posData.blockPos()));

        return tag;
    }

    public static JointPosData readPosData(ServerLevel level, CompoundTag tag) {
        Long shipId = tag.getLong("shipId");
        shipId = shipId == -1 ? null : shipId;
        BlockPos blockPos = NbtUtils.readBlockPos(tag.getCompound("blockPos"));

        return JointPosData.create(level, shipId, blockPos);
    }

}

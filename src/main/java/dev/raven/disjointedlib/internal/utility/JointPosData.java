package dev.raven.disjointedlib.internal.utility;

import dev.raven.disjointedlib.DisjointedLib;
import kotlin.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

import javax.annotation.Nullable;

import static dev.raven.disjointedlib.internal.utility.JointUtility.getLocalPos;

public record JointPosData(@Nullable Long shipId, BlockPos blockPos, Vector3d localPos, boolean isWorld) {
    public static JointPosData create(ServerLevel level, Long id, BlockPos pos) {
        if (ShipUtility.getGroundBodyId(level).equals(id)) {
            DisjointedLib.LOGGER.warn("Received actual id for ground body identifier instead of null (expected value), correcting");
            id = null;
        }

        Vector3d localPos = getLocalPos(level, pos);

        return new JointPosData(id, pos, localPos, id == null);
    }

    public @NotNull Long getShipIdSafe(ServerLevel level) {
        return shipId == null ? ShipUtility.getGroundBodyId(level) : shipId;
    }

    public @NotNull Long getShipIdSafe() {
        return shipId == null ? -1 : shipId;
    }

    public boolean isShip(Long otherId) {
        if (this.shipId == null) return otherId == null;
        return this.shipId.equals(otherId);
    }

    public Vector3d getWorldPos(ServerLevel level) {
        return JointUtility.getWorldPos(level, blockPos, shipId);
    }

    public static Pair<JointPosData, JointPosData> create(ServerLevel level, Long ship0, Long ship1, BlockPos blockPos0, BlockPos blockPos1) {
        ship0 = (ShipUtility.getGroundBodyId(level).equals(ship0)) ? null : ship0;
        ship1 = (ShipUtility.getGroundBodyId(level).equals(ship1)) ? null : ship1;
        JointPosData posData0 = JointPosData.create(level, ship0, blockPos0);
        JointPosData posData1 = JointPosData.create(level, ship1, blockPos1);

        if (posData1.isWorld() && !posData0.isWorld()) {
            return new Pair<>(posData1, posData0);
        } else {
            return new Pair<>(posData0, posData1);
        }

    }
}
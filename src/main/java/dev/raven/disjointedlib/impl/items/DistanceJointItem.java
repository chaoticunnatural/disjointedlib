package dev.raven.disjointedlib.impl.items;

import dev.raven.disjointedlib.impl.joints.DisjointedDistanceJoint;
import dev.raven.disjointedlib.infrastructure.JointManager;
import dev.raven.disjointedlib.internal.utility.ShipUtility;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class DistanceJointItem extends Item {
    public DistanceJointItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains("first");
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos().immutable();
        ItemStack stack = context.getItemInHand();
        Player player = context.getPlayer();


        if (!(level instanceof ServerLevel serverLevel)) return InteractionResult.PASS;
        if (player == null) return InteractionResult.PASS;

        Long shipId = ShipUtility.getLoadedShipIdAtPos(serverLevel, clickedPos);

        if (!isFoil(stack)) {
            CompoundTag tag = stack.getOrCreateTagElement("first");

            tag.putLong("shipId", shipId == null ? -1 : shipId);
            tag.put("pos", NbtUtils.writeBlockPos(clickedPos));

            return InteractionResult.SUCCESS;
        }

        CompoundTag tag = stack.getTagElement("first");

        JointManager.get(serverLevel).addJoint(integer ->
            DisjointedDistanceJoint.create(
                    serverLevel,
                    integer,
                    tag.getLong("shipId"),
                    ShipUtility.getLoadedShipIdAtPos(serverLevel, clickedPos),
                    NbtUtils.readBlockPos(tag.getCompound("pos")),
                    clickedPos
            )
        );

        return InteractionResult.SUCCESS;
    }
}

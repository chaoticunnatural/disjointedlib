package dev.raven.disjointedlib.impl.items;

import dev.raven.disjointedlib.infrastructure.JointManager;
import dev.raven.disjointedlib.internal.DisjointedJoint;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.stream.Stream;

public class JointRemoverItem extends Item {
    public JointRemoverItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos().immutable();
        ItemStack stack = context.getItemInHand();
        Player player = context.getPlayer();


        if (!(level instanceof ServerLevel serverLevel)) return InteractionResult.PASS;
        if (player == null) return InteractionResult.PASS;

        HashMap<Integer, DisjointedJoint<?>> joints = JointManager.get(serverLevel).getJoints();

        Stream<DisjointedJoint<?>> toRemove = joints.values().stream().filter(joint -> joint.isAtPos(clickedPos));
    }
}

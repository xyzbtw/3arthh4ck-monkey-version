package me.earth.earthhack.impl.modules.combat.ccthing;

import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.holeesp.invalidation.SimpleHoleManager;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.HoleUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import static me.earth.earthhack.impl.util.math.MathUtil.round;

public class ListenerUpdate extends ModuleListener<CCthing, UpdateEvent> {
    public ListenerUpdate(CCthing module) {
        super(module, UpdateEvent.class);
    }

    @Override
    public void invoke(UpdateEvent event) {
        if(mc.player==null || mc.world==null) return;
        BlockPos furthestPos = null;

        EntityPlayer enemy = EntityUtil.getClosestEnemy();
        if(enemy == null) return;
        //ChatUtil.sendMessage("Enemy is " + enemy.getName());

        if (PlayerUtil.isInHole(enemy)) {
            double maxDistance = Double.MIN_VALUE;
            for (Vec3i offset : module.offsets) {
                ChatUtil.sendMessage("Starting for loop");
                BlockPos pos = enemy.getPosition().add(offset);

                double distance = mc.player.getDistanceSq(pos);
                if (distance > module.range.getValue()) {
                    ChatUtil.sendMessage("Range too big, continuing");
                    continue;
                }
                if (!BlockUtil.isReplaceable(pos)) {
                    ChatUtil.sendMessage("Wasn't replaceable");
                    continue;
                }

                if (distance > maxDistance) {
                    maxDistance = distance;
                    furthestPos = pos;
                    ChatUtil.sendMessage( "FurthestPos is " + furthestPos);
                }

            }
            if (furthestPos != null && module.delayTimer.passed(module.cycledelay.getValue())) {
                EnumFacing facing = RayTraceUtil.getFacing(
                        RotationUtil.getRotationPlayer(),furthestPos, true);
                module.placeBlock(furthestPos, facing);
                ChatUtil.sendMessage("Placing block at " + furthestPos);
                if (!BlockUtil.isAir(furthestPos)) {
                    assert facing != null;
                    mc.playerController.onPlayerDamageBlock(furthestPos, facing);
                    ChatUtil.sendMessage("Hitting " + furthestPos);
                }
                module.delayTimer.reset();
            }
        }
    }
}

package me.earth.earthhack.impl.modules.combat.blockerbait;

import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.otherplayers.IgnoreSelfClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class ListenerUpdate extends ModuleListener<BlockerBait, UpdateEvent> {
    public ListenerUpdate(BlockerBait module) {
        super(module, UpdateEvent.class);
    }

    @Override
    public void invoke(UpdateEvent event) {
        EntityPlayer target = EntityUtil.getClosestEnemy();
        if(target!=null){
            for(Vec3i hit : module.offsets){
                BlockPos enemyPos = target.getPosition();
                BlockPos hitPos = enemyPos.add(hit);
                EnumFacing facing = RayTraceUtil.getFacing(mc.player, hitPos, true);

                if(BlockUtil.isAir(hitPos) || mc.world.getBlockState(hitPos).getBlock() == (Blocks.BEDROCK)) continue;

                if(module.timer.passed(module.delay.getValue())) {
                    assert facing != null;
                    mc.playerController.onPlayerDamageBlock(hitPos, facing);
                    module.timer.reset();
                }
            }
        }
    }
}

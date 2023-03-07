package me.earth.earthhack.impl.modules.movement.smartblocklag;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.movement.blocklag.BlockLag;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;


public class ListenerUpdate extends ModuleListener<SmartBlockLag, UpdateEvent> {
    public ListenerUpdate(SmartBlockLag module) {
        super(module, UpdateEvent.class);
    }
    private static final ModuleCache<BlockLag> burrow = Caches.getModule(BlockLag.class);
    boolean burrowed = false;
    StopWatch delayTimer;

    @Override
    public void invoke(UpdateEvent event) {
        EntityPlayer target = EntityUtil.getClosestEnemy();
        if ((!(module.holeonly.getValue())
                || PlayerUtil.isInHole(mc.player))
                    && !mc.isSingleplayer() && target!=null
                && delayTimer.passed(module.delay.getValue()))
        {
            if (mc.player.getDistance(target) < module.smartRange.getValue()
                    && !PlayerUtil.isInHole(target)
                    && !burrow.isEnabled()
                    && mc.world.getBlockState(PlayerUtil.getPlayerPos().add(0, 0.2, 0)).getBlock() instanceof BlockAir
                    && !burrowed) {
                burrow.enable();
                if(module.turnoff.getValue()){
                    module.disable();
                }
            }
            burrowed= !(mc.world.getBlockState(PlayerUtil.getPlayerPos()).getBlock() instanceof BlockAir);
        }
    }
}

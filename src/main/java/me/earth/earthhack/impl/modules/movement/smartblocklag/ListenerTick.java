package me.earth.earthhack.impl.modules.movement.smartblocklag;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.movement.blocklag.BlockLag;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;


public class ListenerTick extends ModuleListener<SmartBlockLag, TickEvent> {
    public ListenerTick(SmartBlockLag module) {
        super(module, TickEvent.class);
    }
    private static final ModuleCache<BlockLag> burrow =
            Caches.getModule(BlockLag.class);


    @Override
    public void invoke(TickEvent event) {

        if(mc.world == null || mc.player == null){
            return;
        }

        EntityPlayer target = EntityUtil.getClosestEnemy();
        BlockPos pos = PlayerUtil.getPlayerPos();
        if (    !(Boolean)module.holeonly.getValue() || PlayerUtil.isInHole(mc.player)
                && !mc.isSingleplayer()
                && target != null
                && !Managers.FRIENDS.contains(target)
                && mc.player.getDistance(target) <= module.smartRange.getValue()
                && !PlayerUtil.isInHole(target)
                && !burrow.isEnabled()
                && mc.world.getBlockState(pos.add(0, 0.2, 0)).getBlock() instanceof BlockAir
                && !module.isPhasing())
            {
                if(!module.delayTimer.passed(module.delay.getValue())){
                    return;
                }
                burrow.enable();
                module.delayTimer.reset();
                if(module.turnoff.getValue()){
                    module.disable();
                }
            }
    }
}


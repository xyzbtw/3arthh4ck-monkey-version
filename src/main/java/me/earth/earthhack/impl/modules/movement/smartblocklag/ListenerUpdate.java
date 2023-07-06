package me.earth.earthhack.impl.modules.movement.smartblocklag;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.movement.blocklag.BlockLag;
import me.earth.earthhack.impl.modules.render.holeesp.invalidation.SimpleHoleManager;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.minecraft.PhaseUtil;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import me.earth.earthhack.impl.util.minecraft.PushMode;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;


public class ListenerUpdate extends ModuleListener<SmartBlockLag, UpdateEvent> {
    public ListenerUpdate(SmartBlockLag module) {
        super(module, UpdateEvent.class);
    }
    public static final ModuleCache<BlockLag> burrow =
            Caches.getModule(BlockLag.class);
    SimpleHoleManager holeManager = new SimpleHoleManager();

    @Override
    public void invoke(UpdateEvent event) {
        if(mc.world == null || mc.player == null){
            return;
        }
        module.target = EntityUtil.getClosestEnemy();
        module.pos = PlayerUtil.getPlayerPos();
        BlockPos posabovehead = module.pos.add(0,2,0);
        if (    !module.holeonly.getValue() || isInHole(mc.player)
                && module.target != null
                && !Managers.FRIENDS.contains(module.target)
                && !PhaseUtil.isPhasing(module.target, PushMode.MP)
                && mc.player.getDistanceSq(module.target) < MathUtil.square(module.smartRange.getValue())
                && !isInHole(module.target)
                && !burrow.isEnabled()
                && BlockUtil.isReplaceable(module.pos.add(0,0.2,0))
                && !PhaseUtil.isPhasing(mc.player, PushMode.MP)
                && BlockUtil.isAir(posabovehead))
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
    public boolean isInHole(EntityPlayer player) {
        BlockPos position = PositionUtil.getPosition(player);
        int count = 0;
        for (EnumFacing face : EnumFacing.values()) {
            if (face == EnumFacing.UP || face == EnumFacing.DOWN) continue;
            if (!BlockUtil.isReplaceable(position.offset(face))) count++;
        }
        return count >= (module.twobyone.getValue() ? 3 : 4);
    }
}


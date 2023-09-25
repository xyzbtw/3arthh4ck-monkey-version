package me.earth.earthhack.impl.modules.combat.privatecevbreaker;

import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyModule;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyUtil;
import me.earth.earthhack.impl.util.math.path.BasePath;
import me.earth.earthhack.impl.util.math.path.PathFinder;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;


public class ListenerUpdate extends ModuleListener<PrivateCevBreaker, UpdateEvent> {
    public ListenerUpdate(PrivateCevBreaker module) {
        super(module, UpdateEvent.class);
    }


    @Override
    public void invoke(UpdateEvent event) {
        if(!module.isSafe()) return;
        if(!module.delay.passed(module.clearDelay.getValue())) return;

        EntityPlayer target = EntityUtil.getClosestEnemy();
        if(target==null) return;

        for(Vec3i offset : module.offsetsPlace){
            BlockPos targetPos = target.getPosition();
            BlockPos pos = target.getPosition().add(offset);
            BlockPos helpPos = null;
            if(BlockUtil.isAir(pos.add(0,-1,0))) {
                for (EnumFacing facing : EnumFacing.HORIZONTALS) {
                    if (pos.offset(facing) != targetPos.add(0, 2, 0)) {
                        BlockPos pos2 = pos.offset(facing);
                        helpPos=pos2.add(1,0,0);
                        break;
                    }
                }
                if(helpPos!=null) {

                    BasePath path = new BasePath(RotationUtil.getRotationPlayer(),
                            pos,
                            module.pathLength.getValue());

                    PathFinder.findPath(
                            path,
                            6,
                            mc.world.loadedEntityList,
                            module.smartRay.getValue(),
                            ObbyModule.HELPER,
                            Blocks.OBSIDIAN.getDefaultState(),
                            PathFinder.CHECK,
                            pos.down(),
                            pos.down(1), pos.down(2));
                    ObbyUtil.place(module, path);
                    if(module.debug.getValue()) ChatUtil.sendMessage("Placing block at " + path.getPos());
                    module.delay.reset();
                }
            }
            }
        }
    }

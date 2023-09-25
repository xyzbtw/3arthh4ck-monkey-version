package me.earth.earthhack.impl.modules.player.foreverspeedmine;

import me.earth.earthhack.impl.core.ducks.network.IPlayerControllerMP;
import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class ListenerUpdate extends ModuleListener<ForeverSpeedMine, UpdateEvent> {
    public ListenerUpdate(ForeverSpeedMine module) {
        super(module, UpdateEvent.class);
    }

    @Override
    public void invoke(UpdateEvent event) {
        if(mc.player==null || mc.world == null) return;
        if (module.currentPos != null) {


            if (module.checkCurrentPos() && module.mineDamage >= module.speed.getValue() - 0.05) module.doSyncAutoCrystal();

//            if (checked) {
//                mineDamage = 0;
//            }

            if (module.mineDamage <= module.speed.getValue() && module.currentPos != null) module.mineDamage += module.getBlockStrength(mc.world.getBlockState(module.currentPos), module.currentPos);

            if (module.isPlaced && !module.resetOnPlace.getValue() || mc.world.getBlockState(module.currentPos).getBlock() == Blocks.ENDER_CHEST) {
                if (module.mineDamage >= module.speed.getValue()) {
                    module.swapTo();
                }
            } else if (module.mineDamage >= module.speed.getValue() && mc.world.getBlockState(module.currentPos).getBlock() != Blocks.AIR) {
                module.swapTo();
            } else if (module.resetOnPlace.getValue() && module.isPlaced) {
                module. resetProgress(false);
                module.currentPos = null;
                module.isPlaced = false;
                return;
            }

            if (module.reBreak.getValue()) {
                if (mc.world.getBlockState(module.currentPos).getBlock() == Blocks.AIR) {
                    if (!module.checked) {
                        module.resetProgress(true);
                        module.checked = true;
                        module.strictCheck = false;
                    }
                } else {
                    if (module.strictReBreak.getValue() && !module.strictCheck) {
                        Block block = mc.world.getBlockState(module.currentPos).getBlock();
                        if (!(block.equals(Blocks.ENDER_CHEST) || block.equals(Blocks.ANVIL) || block.equals(Blocks.AIR))) {
                            module.rebreakCount = 0;
                            module.mineDamage = 0;
                            module.currentPos = null;
                            module.strictCheck = true;
                            return;
                        }
                    }
                    module.checked = false;
                }

            }

            if (module.currentPos != null && mc.player.getDistanceSq(module.currentPos) >= (module.range.getValue() * module.range.getValue())) {
                module. resetProgress(false);
                module. currentPos = null;
            }

        }
        ((IPlayerControllerMP) mc.playerController).setBlockHitDelay(0);
    }
}

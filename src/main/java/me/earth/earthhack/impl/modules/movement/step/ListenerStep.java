package me.earth.earthhack.impl.modules.movement.step;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.movement.StepEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypassModule;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import me.earth.earthhack.pingbypass.protocol.c2s.C2SStepPacket;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.network.play.client.CPacketPlayer;


final class ListenerStep extends ModuleListener<Step, StepEvent> {
    public ListenerStep(Step module) {
        super(module, StepEvent.class);
    }

    @Override
    public void invoke(StepEvent event) {
        if (!Managers.NCP.passed(module.lagTime.getValue())) {
            module.reset();
            return;
        }
        if(module.stopsneak.getValue() && mc.player.isSneaking()) return;
        if (event.getStage() == Stage.PRE) {
            if (mc.player.getRidingEntity() != null) {
                mc.player.getRidingEntity().stepHeight =
                        module.entityStep.getValue()
                                ? 256.0f
                                : 1.0f;
            }

            if (module.mode.getValue() != StepMode.Slow || !module.stepping) {
                // x and z assignments are probably unnecessary here
                module.x = mc.player.posX;
                module.y = event.getBB().minY;
                module.z = mc.player.posZ;

                //noinspection AssignmentUsedAsCondition
                if (module.stepping = module.canStep()) {
                    if (module.useTimer.getValue()) {
                        Managers.TIMER.setTimer(module.timer.getValue().floatValue());
                    }
                    event.setHeight(module.height.getValue());
                    if(event.getStage()== Stage.POST && module.bypass.getValue() && !module.bypassAlways.getValue()) {
                        module.bypasstime.reset();
                    }
                } else {
                    module.reset();
                }
            }
        } else if (module.stepping) {
            double height = event.getBB().minY - module.y;
            if (module.mode.getValue() == StepMode.Normal
                    && height > event.getHeight()) {
                double[] offsets = getOffsets(height);
                if (PingBypassModule.CACHE.isEnabled()
                        && !PingBypassModule.CACHE.get().isOld()) {
                    mc.player.connection.sendPacket(
                            new C2SStepPacket(
                                    offsets, module.x, module.y, module.z));
                } else {
                    for (double offset : offsets) {
                        mc.player.connection.sendPacket(
                                new CPacketPlayer.Position(
                                        mc.player.posX,
                                        mc.player.posY + offset,
                                        mc.player.posZ,
                                        true));
                    }
                }
            } else if (module.mode.getValue() == StepMode.Slow
                    && height > event.getHeight()
                    && module.offsets == null) {
                module.offsets = getOffsets(height);
                module.bb = event.getBB();
                module.index = 0;
                module.currHeight = height;
                mc.player.setPosition(module.x, module.y, module.z);
            }

            if (module.gapple.getValue()
                    && module.stepping
                    && module.mode.getValue() != StepMode.Slow
                    && !module.breakTimer.passed(60)
                    && InventoryUtil.isHolding(ItemPickaxe.class)
                    && !InventoryUtil.isHolding(ItemAppleGold.class)) {
                Entity closest = EntityUtil.getClosestEnemy();
                if (closest != null && closest.getDistanceSq(mc.player) < 144) {
                    int slot = InventoryUtil.findHotbarItem(Items.GOLDEN_APPLE);
                    if (slot != -1) {
                        Locks.acquire(Locks.PLACE_SWITCH_LOCK,
                                () -> InventoryUtil.switchTo(slot));
                    }
                }
            }

            if (module.mode.getValue() != StepMode.Slow
                    && height > event.getHeight()) {
                module.reset();
                if (module.autoOff.getValue()) {
                    module.disable();
                }
            }
        }
    }

    private double[] getOffsets(double height) {
        double[] offsets = new double[0];
        if (height > 2.0){
            offsets = new double[9];
            offsets[0] = 0.42;
            offsets[1] = 0.78;
            offsets[2] = 0.63;
            offsets[3] = 0.51;
            offsets[4] = 0.9;
            offsets[5] = 1.21;
            offsets[6] = 1.45;
            offsets[7] = 1.43;
            offsets[8] = 2.125;
        }
        else if (height == 2.0)
        {
            offsets = new double[8];
            offsets[0] = 0.42;
            offsets[1] = 0.78;
            offsets[2] = 0.63;
            offsets[3] = 0.51;
            offsets[4] = 0.9;
            offsets[5] = 1.21;
            offsets[6] = 1.45;
            offsets[7] = 1.43;
        }
        else if(height == 1.875)
        {
            offsets = new double[8];
            offsets[0] = 0.425;
            offsets[1] = 0.821;
            offsets[2] = 0.699;
            offsets[3] = 0.599;
            offsets[4] = 1.022;
            offsets[5] = 1.372;
            offsets[6] = 1.652;
            offsets[7] = 1.869;
        } else if (height<1.875) {
            offsets = new double[height > 1.0 ? 6 : 2];
            offsets[0] = height < 1.0 && height > 0.8 ? 0.39 : 0.42;
            offsets[1] = height < 1.0 && height > 0.8 ? 0.7 : 0.7532;
            if (height > 1.0) {
                offsets[2] = 1.0;
                offsets[3] = 1.16;
                offsets[4] = 1.23;
                offsets[5] = 1.2;
            }
        }

            return offsets;
        }


}
